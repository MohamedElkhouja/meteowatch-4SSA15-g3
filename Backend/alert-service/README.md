# alert-service (Module 2 – Alertes et vigilances)
**Responsable :** Linda Nebily · **Port :** 8082 · **Techno :** Spring Boot

## Responsabilités (contexte borné : règles et cycle de vie des alertes)
- Définition des seuils (ex. température > 40 °C, vent > 80 km/h)
- Niveaux de vigilance : VERT, JAUNE, ORANGE, ROUGE
- Déclenchement, consultation et clôture des alertes ; historique

## Modèle de données
Base : **PostgreSQL** — historique d'alertes à interroger par statut, niveau et période.

| Entité | Attributs |
|---|---|
| Threshold | id, metric (TEMPERATURE, WIND, RAIN…), operator (>, <), value, level, region |
| Alert | id, stationId, region, thresholdId, level, measuredValue, status (OPEN, CLOSED), triggeredAt, closedAt |

## API REST

| Méthode | URI | Description | Codes |
|---|---|---|---|
| POST | /api/thresholds | Créer un seuil | 201, 400 |
| GET | /api/thresholds | Lister les seuils | 200 |
| PUT | /api/thresholds/{id} | Modifier | 200, 400, 404 |
| DELETE | /api/thresholds/{id} | Supprimer | 204, 404 |
| GET | /api/alerts | Alertes (filtres `?status=` `?level=` `?region=`) | 200 |
| GET | /api/alerts/{id} | Détail | 200, 404 |
| PUT | /api/alerts/{id}/close | Clôturer | 200, 404, 409 |
| GET | /api/alerts/history | Historique par période | 200 |

Événements : consomme `reading.recorded` · publie `alert.triggered`.

## Interactions
| Direction | Service | Mode | Données | Si indisponible |
|---|---|---|---|---|
| Consomme | readings-service | async (RabbitMQ) | nouveau relevé | messages conservés dans la file, traités au retour |
| Appelle | station-service | OpenFeign (sync) | région de la station | alerte créée sans région (fallback) |
| Publie | notification-service | async (RabbitMQ) | `alert.triggered` (niveau, région, valeur) | file persistante, aucune alerte perdue |

Justification : le déclenchement est asynchrone pour ne pas ralentir l'enregistrement des relevés.
