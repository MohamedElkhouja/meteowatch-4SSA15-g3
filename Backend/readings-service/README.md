# readings-service (Module 4 – Relevés météo)
**Responsable :** Ramzi Ben Hmida · **Port :** 8084 · **Techno :** Spring Boot

## Responsabilités (contexte borné : mesures observées)
- Enregistrement des relevés (température, humidité, pression, vent, pluie)
- Historique par station et par période
- Statistiques simples (moyenne, min, max par jour)

## Modèle de données
Base : **MySQL** — fort volume de lignes homogènes, requêtes d'agrégation (AVG/MIN/MAX par jour), index sur (stationId, timestamp).

| Entité | Attributs |
|---|---|
| Reading | id, stationId, timestamp, temperature, humidity, pressure, windSpeed, rain |
| DailyStats (calculée) | stationId, date, avg/min/max par mesure |

## API REST

| Méthode | URI | Description | Codes |
|---|---|---|---|
| POST | /api/readings | Enregistrer un relevé | 201, 400, 404, 503 |
| GET | /api/readings?stationId=&from=&to= | Historique par période | 200, 400 |
| GET | /api/readings/{id} | Détail | 200, 404 |
| GET | /api/stations/{stationId}/readings/stats?date= | Moyenne, min, max du jour | 200, 404 |

Événement publié : `reading.recorded`.

## Interactions
| Direction | Service | Mode | Données | Si indisponible |
|---|---|---|---|---|
| Appelle | station-service | OpenFeign (sync) | existence de la station | relevé refusé (503), l'émetteur réessaie |
| Publie | alert-service, analytics-service | async (RabbitMQ) | `reading.recorded` | file persistante |
| Appelé par | forecast-service | OpenFeign (sync) | relevés réels pour comparaison | forecast-service renvoie les prévisions seules |
| Appelé par | analytics-service | OpenFeign (sync) | historique pour entraînement | modèle précédent conservé |
