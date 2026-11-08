# analytics-service (Module 7 – Statistiques et tableau de bord, technologie avancée)
**Module commun : toute l'équipe** (un seul membre le présente) · **Port :** 8087 · **Techno :** Python (FastAPI + scikit-learn)

## Responsabilités (contexte borné : analyse et prédiction)
- Statistiques globales et données du tableau de bord (alertes, relevés, stations)
- Prédiction à partir des relevés (ex. température à J+1)
- Option : assistant météo basé sur l'IA

## Modèle de données
Base : **PostgreSQL** — agrégats pré-calculés et résultats de prédiction (aucune base partagée : les données source restent chez readings-service et alert-service).

| Entité | Attributs |
|---|---|
| DashboardSnapshot | id, date, nbStations, nbAlertsOpen, avgTemperature… |
| Prediction | id, stationId, targetDate, metric, predictedValue, modelVersion, createdAt |

## API REST

| Méthode | URI | Description | Codes |
|---|---|---|---|
| GET | /api/analytics/dashboard | Chiffres clés | 200 |
| GET | /api/analytics/stats?stationId=&from=&to= | Statistiques d'une période | 200, 400 |
| GET | /api/analytics/predictions?stationId=&metric= | Dernières prédictions | 200, 404 |
| POST | /api/analytics/predictions/train | Ré-entraîner le modèle | 202, 503 |
| POST | /api/analytics/assistant | Question à l'assistant IA (optionnel) | 200, 400 |

## Interactions
| Direction | Service | Mode | Données | Si indisponible |
|---|---|---|---|---|
| Consomme | readings-service | async (RabbitMQ) | `reading.recorded` | file persistante |
| Appelle | readings-service | HTTP (sync) | historique pour l'entraînement | modèle précédent conservé |
| Appelle | alert-service | HTTP (sync) | alertes ouvertes pour le dashboard | dashboard partiel |

Enregistrement dans Eureka via `py_eureka_client`. Lancement : `uvicorn app.main:app --port 8087`.
