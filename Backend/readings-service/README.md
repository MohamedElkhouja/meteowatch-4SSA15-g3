# readings-service (Microservice 4 – Gestion des relevés météo)

**Responsable :** Ramzi Ben Hmida · **Port :** 8084 · **Techno :** Spring Boot 3 · **Base :** MongoDB (`observation_db`) · **Nom Eureka :** `readings-service`

## Responsabilités (contexte borné : les mesures des stations)
- Enregistrer les relevés envoyés par les stations (température, humidité, pression, vent, précipitations)
- Vérifier la station auprès de `station-service` avant d'accepter un relevé
- Consulter l'historique d'une station (filtre par période, pagination) et ses derniers relevés
- Calculer les statistiques journalières (min, max, moyenne) par type de mesure
- Publier l'événement `ObservationRecorded` pour `alert-service` et `analytics-service`

Le service est la référence sur les mesures : `forecast-service` et `analytics-service` l'interrogent pour comparer prévisions et réel.

## Entités (diagramme de classes)
Diagramme : `../../Documentation/Diagrams/readings-service/readings-class-diagram.png`

| Collection | Champs |
|---|---|
| `releves` (**Releve**) | id, stationId, horodatage, temperature (°C), humidite (%), pression (hPa), vitesseVent (km/h), precipitation (mm), region, statut (`VALIDE` / `A_VALIDER`), creeLe |
| `stats_journalieres` (**StatJournaliere**) | id, stationId, date, type (`TEMPERATURE`, `HUMIDITE`, `PRESSION`, `VENT`, `PRECIPITATION`), min, max, moyenne, nombreReleves |

Règles de validation d'un relevé : `stationId` obligatoire ; `horodatage` obligatoire et pas dans le futur ; température entre -90 et 60 ; humidité entre 0 et 100 ; pression entre 800 et 1100 ; vent et précipitations supérieurs ou égaux à 0.
`region` est la ville de la station (champ `ville` de `station-service`).
Index : unique `(stationId, horodatage)` (pas de doublon) et `(statut, horodatage)`.

**Pourquoi MongoDB ?** Gros volume d'écritures, données horodatées (séries temporelles), schéma flexible selon les capteurs, aucune jointure nécessaire.

## Contrat d'API REST
| Méthode | URI | Description | Codes |
|---|---|---|---|
| POST | `/api/releves` | Enregistrer un relevé | 201, 202, 400, 404, 409 |
| GET | `/api/releves?stationId=1&from=&to=&page=&size=` | Historique d'une station (paginé, 100 max par page) | 200, 400 |
| GET | `/api/releves/derniers?stationId=1&limite=10` | Derniers relevés (le plus récent d'abord) | 200, 400 |
| GET | `/api/releves/stats?stationId=1&date=2026-11-09&type=TEMPERATURE` | Min, max, moyenne d'une journée | 200, 400, 404 |
| GET | `/api/releves/{id}` | Détail d'un relevé | 200, 404 |
| DELETE | `/api/releves/{id}` | Supprimer un relevé | 204, 404 |

Codes du POST : **201** station vérifiée · **202** `station-service` indisponible, relevé enregistré « à valider » · **400** données invalides · **404** station inexistante ou désactivée · **409** relevé déjà enregistré pour cette station à cet instant.
`type` vaut `TEMPERATURE` par défaut ; `date` vaut aujourd'hui (UTC) par défaut.

**Exemple – POST /api/releves**
```json
{ "stationId": "1", "horodatage": "2026-11-09T10:00:00Z", "temperature": 22.4, "humidite": 61, "pression": 1014, "vitesseVent": 18, "precipitation": 0.2 }
```
Réponse `201 Created` (en-tête `Location: /api/releves/{id}`) :
```json
{ "id": "6730a1f2c4d5e6f708192a3b", "stationId": "1", "horodatage": "2026-11-09T10:00:00Z", "temperature": 22.4, "humidite": 61.0, "pression": 1014.0, "vitesseVent": 18.0, "precipitation": 0.2, "region": "Tunis", "statut": "VALIDE" }
```

**Exemple – GET /api/releves/stats?stationId=1&date=2026-11-09&type=TEMPERATURE**
```json
{ "stationId": "1", "date": "2026-11-09", "type": "TEMPERATURE", "min": 14.2, "max": 24.8, "moyenne": 19.6, "nombreReleves": 96 }
```

**Format d'erreur (400 / 404 / 409)**
```json
{ "timestamp": "2026-11-09T10:00:00Z", "status": 404, "erreur": "Not Found", "message": "Station 99 introuvable" }
```

**Sécurité (API Gateway + Keycloak)** : lecture ouverte aux rôles `USER`, `METEOROLOGUE` et `ADMIN` ; enregistrement d'un relevé (`POST`) réservé à `METEOROLOGUE` et `ADMIN` ; suppression (`DELETE`) réservée à `ADMIN`.
Par défaut la sécurité est désactivée en local ; elle s'active avec le profil Spring `keycloak`.

## Interactions
Diagramme de séquence : `../../Documentation/Diagrams/readings-service/readings-interactions.png`

| Direction | Service | Mode | Données | Si indisponible |
|---|---|---|---|---|
| Appelle | `station-service` | OpenFeign (synchrone) | `GET /api/stations/{id}` : vérifier la station (existante et active) et récupérer sa ville | timeout 2 s, 1 retry, circuit breaker (Resilience4j) + fallback : relevé enregistré **« à valider »** (réponse 202) puis revérifié toutes les 60 s |
| Publie | RabbitMQ | asynchrone | `ObservationRecorded` après chaque relevé valide | si RabbitMQ est hors service, le relevé reste enregistré en base et l'erreur est journalisée |
| Appelé par | `forecast-service`, `analytics-service` | HTTP / OpenFeign (synchrone) | `GET /api/releves` : historique des relevés | circuit breaker + fallback côté appelant |

**Événement publié : `ObservationRecorded`** sur l'exchange topic `meteowatch.events`, clé de routage `observation.recorded`, **un événement par mesure** du relevé. Consommé par `alert-service` (comparaison aux seuils) et `analytics-service` (statistiques). Chaque consommateur a sa propre file durable.
```json
{ "eventId": "b3f1c9e2-7d0a-4c55-9b0a-1f2e3d4c5b6a", "occurredAt": "2026-11-09T10:00:01Z", "observationId": "6730a1f2c4d5e6f708192a3b", "stationId": "1", "region": "Tunis", "type": "TEMPERATURE", "valeur": 41.5, "unite": "°C", "horodatage": "2026-11-09T10:00:00Z" }
```

**Relevé « à valider » :** si `station-service` ne répond pas, le relevé est enregistré avec le statut `A_VALIDER` et aucun événement n'est publié. Une tâche planifiée (`RevalidationJob`) le revérifie : station trouvée et active → le relevé devient `VALIDE`, la région est renseignée et l'événement part ; station inconnue ou désactivée → le relevé est supprimé ; service toujours en panne → nouvel essai au tour suivant.

**Pourquoi synchrone vers `station-service` ?** On a besoin de la réponse (la station existe-t-elle ?) avant d'accepter le relevé. **Pourquoi asynchrone vers `alert-service` ?** C'est une notification : l'enregistrement ne doit pas dépendre de la disponibilité des autres services, et un service arrêté recevra le message plus tard.

## Lancer en local
```bash
docker compose -f docker-compose.dev.yml up -d                       # MongoDB + RabbitMQ
mvn spring-boot:run -Dspring-boot.run.profiles=stub                  # "stub" = faux station-service
mvn test
```
Sans le profil `stub`, le service appelle le vrai `station-service` (`STATION_SERVICE_URL`, défaut `http://localhost:8081`).
Si RabbitMQ tourne déjà (compose de `station-service`), ne démarrer que MongoDB : `docker compose -f docker-compose.dev.yml up -d mongo`.
Eureka et le Config Server sont optionnels en local : le service démarre sans eux (`EUREKA_ENABLED=true` pour s'enregistrer).
Variables : `MONGODB_URI`, `RABBITMQ_HOST`, `STATION_SERVICE_URL`, `EUREKA_ENABLED`, `EUREKA_URL`, `KEYCLOAK_ISSUER_URI`.
Swagger UI : http://localhost:8084/swagger-ui.html

## Tester rapidement
```bash
curl -X POST http://localhost:8084/api/releves -H "Content-Type: application/json" -d '{"stationId":"1","horodatage":"2026-11-09T10:00:00Z","temperature":22.4,"humidite":61,"pression":1014,"vitesseVent":18,"precipitation":0.2}'
curl "http://localhost:8084/api/releves?stationId=1"
curl "http://localhost:8084/api/releves/derniers?stationId=1&limite=5"
curl "http://localhost:8084/api/releves/stats?stationId=1&date=2026-11-09&type=TEMPERATURE"
curl -X DELETE http://localhost:8084/api/releves/{id}
```
D'autres exemples sont dans `requests.http` (IntelliJ ou extension REST Client de VS Code).

## Structure du code
```
src/main/java/com/meteowatch/readings/
├── controller/   ReleveController
├── service/      ReleveService, RevalidationJob
├── repository/   ReleveRepository, ReleveQueryRepository, StatJournaliereRepository
├── model/        Releve, StatJournaliere, TypeMesure, StatutReleve
├── dto/          ReleveRequest, ReleveResponse, StatJournaliereResponse, PageResponse, ApiError
├── client/       StationClient (Feign), StationClientFallbackFactory, StubStationClient
├── event/        ObservationEventPublisher, ObservationRecordedEvent
├── config/       FeignConfig, RabbitConfig, SchedulingConfig, SecurityConfig
└── exception/    exceptions métier + GlobalExceptionHandler
```
