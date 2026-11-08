# station-service (Module 1 – Gestion des stations météo)
**Responsable :** Maha Hammami · **Port :** 8081 · **Techno :** Spring Boot

## Responsabilités (contexte borné : le parc de stations)
- CRUD des stations (nom, ville, région, coordonnées GPS, altitude)
- Gestion des capteurs rattachés à une station (type, état actif / en panne)
- Recherche des stations par ville ou par région

## Modèle de données
Base : **PostgreSQL** — données relationnelles (Station 1—N Capteur), recherches filtrées, contraintes d'intégrité.

| Entité | Attributs |
|---|---|
| Station | id, name, city, region, latitude, longitude, altitude |
| Sensor | id, type (TEMPERATURE, HUMIDITY, PRESSURE, WIND, RAIN), status (ACTIVE, FAULTY), stationId |

Diagramme de classes : `../../Documentation/Diagrams/station-service/`

## API REST

| Méthode | URI | Description | Codes |
|---|---|---|---|
| POST | /api/stations | Créer une station | 201, 400 |
| GET | /api/stations | Lister (filtres `?city=` `?region=`) | 200 |
| GET | /api/stations/{id} | Détail | 200, 404 |
| PUT | /api/stations/{id} | Modifier | 200, 400, 404 |
| DELETE | /api/stations/{id} | Supprimer | 204, 404 |
| POST | /api/stations/{id}/sensors | Ajouter un capteur | 201, 400, 404 |
| GET | /api/stations/{id}/sensors | Capteurs d'une station | 200, 404 |
| PATCH | /api/sensors/{id}/status | Passer actif / en panne | 200, 400, 404 |

Événement publié : `sensor.status.changed` (capteur en panne).

## Interactions
| Direction | Service | Mode | Données | Si indisponible |
|---|---|---|---|---|
| Appelé par | readings-service | OpenFeign (sync) | vérifier l'existence d'une station | readings-service rejette le relevé (503) |
| Appelé par | alert-service | OpenFeign (sync) | région d'une station | alerte créée sans région, complétée plus tard |
| Appelé par | forecast-service | OpenFeign (sync) | coordonnées d'une ville | cache local des coordonnées |
| Publie | broker RabbitMQ | async | `sensor.status.changed` | file persistante |
