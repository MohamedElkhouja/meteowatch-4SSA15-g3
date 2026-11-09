# station-service (Microservice 1 – Gestion des stations météo)

**Responsable :** Maha Hammami · **Port :** 8081 · **Techno :** Spring Boot 3 · **Base :** PostgreSQL (`station_db`) · **Nom Eureka :** `station-service`

## Responsabilités (contexte borné : le parc de stations)

- Créer, consulter, modifier et supprimer les stations météo
- Ajouter des capteurs à une station et changer leur état (actif / en panne)
- Rechercher les stations par ville

Le service est la **référence** sur les stations : les autres microservices l'interrogent pour vérifier qu'une station existe ou obtenir ses coordonnées.

## Entités (diagramme de classes)

Diagramme : `../../Documentation/Diagrams/station-service/station-class-diagram.png`

| Entité | Attributs |
|---|---|
| Station | id, nom, ville, latitude, longitude, altitude, dateInstallation |
| Capteur | id, type (TEMPERATURE, HUMIDITE, PRESSION, VENT, PLUIE), unité, état (ACTIF / EN_PANNE) |

Relation : une Station possède plusieurs Capteurs (1..*).

**Pourquoi PostgreSQL ?** Données structurées et liées (station ↔ capteurs), intégrité référentielle (clé étrangère `station_id`) et transactions.

## Contrat d'API REST

| Méthode | URI | Description | Codes |
|---|---|---|---|
| GET | /api/stations | Lister les stations | 200 |
| GET | /api/stations/{id} | Détail d'une station (avec ses capteurs) | 200, 404 |
| GET | /api/stations?ville=Tunis | Stations d'une ville | 200 |
| POST | /api/stations | Créer une station | 201, 400 |
| PUT | /api/stations/{id} | Modifier une station | 200, 400, 404 |
| DELETE | /api/stations/{id} | Supprimer (désactiver) une station | 204, 404 |
| POST | /api/stations/{id}/capteurs | Ajouter un capteur | 201, 400, 404 |
| PATCH | /api/capteurs/{id}/etat | Changer l'état d'un capteur | 200, 400, 404 |

**Exemple – POST /api/stations**

```json
{ "nom": "Tunis-Carthage", "ville": "Tunis", "latitude": 36.851, "longitude": 10.227, "altitude": 4, "dateInstallation": "2024-03-15" }
```

Réponse `201 Created` (en-tête `Location: /api/stations/1`) :

```json
{ "id": 1, "nom": "Tunis-Carthage", "ville": "Tunis", "latitude": 36.851, "longitude": 10.227, "altitude": 4.0, "dateInstallation": "2024-03-15", "active": true, "capteurs": [] }
```

**Exemple – POST /api/stations/1/capteurs** : `{ "type": "TEMPERATURE", "unite": "°C" }`
**Exemple – PATCH /api/capteurs/1/etat** : `{ "etat": "EN_PANNE" }`

**Format d'erreur (400 / 404)**

```json
{ "timestamp": "2026-11-09T10:00:00Z", "status": 404, "erreur": "Not Found", "message": "Station 99 introuvable" }
```

**Sécurité (API Gateway + Keycloak)** : lecture ouverte aux rôles `USER`, `METEOROLOGUE` et `ADMIN` ; création, modification, suppression et gestion des capteurs réservées à `ADMIN`.

**Suppression logique :** `DELETE` ne supprime pas la ligne en base, il passe la station à `active = false`. Les relevés et prévisions qui font référence à cette station gardent ainsi leur historique.

## Interactions

Diagramme de séquence : `../../Documentation/Diagrams/station-service/station-interactions.png`

| Direction | Service | Mode | Données | Si indisponible |
|---|---|---|---|---|
| Appelé par | Relevés | OpenFeign (synchrone) | `GET /api/stations/{id}` : vérifier qu'une station existe avant d'enregistrer un relevé | circuit breaker (Resilience4j) + fallback côté Relevés |
| Appelé par | Prévisions | OpenFeign (synchrone) | `GET /api/stations/{id}` : récupérer latitude et longitude | circuit breaker + fallback côté Prévisions |
| Publie | RabbitMQ | asynchrone | `StationDeactivated` à la suppression d'une station | si RabbitMQ est hors service, la suppression reste enregistrée en base et l'erreur est journalisée |

**Événement publié :** `StationDeactivated` sur l'exchange topic `meteowatch.events`, clé de routage `station.deactivated`. Contenu : stationId, nom, ville, dateDesactivation.

**Pourquoi synchrone pour les lectures ?** Les appelants ont besoin de la réponse tout de suite pour continuer leur traitement.
**Pourquoi asynchrone pour la suppression ?** C'est une notification : station-service n'attend pas les autres services, et un service momentanément arrêté recevra le message plus tard.

## Lancer en local

```bash
docker compose -f docker-compose.dev.yml up -d   # PostgreSQL + RabbitMQ
mvn spring-boot:run
```

Eureka et le Config Server sont optionnels en local : le service démarre sans eux.

## Tester rapidement

```bash
curl -X POST http://localhost:8081/api/stations -H "Content-Type: application/json" -d '{"nom":"Tunis-Carthage","ville":"Tunis","latitude":36.851,"longitude":10.227,"altitude":4}'
curl "http://localhost:8081/api/stations?ville=Tunis"
curl -X POST http://localhost:8081/api/stations/1/capteurs -H "Content-Type: application/json" -d '{"type":"TEMPERATURE","unite":"°C"}'
curl -X PATCH http://localhost:8081/api/capteurs/1/etat -H "Content-Type: application/json" -d '{"etat":"EN_PANNE"}'
```
