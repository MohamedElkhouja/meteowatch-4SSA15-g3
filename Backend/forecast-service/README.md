# forecast-service (Module 5 – Prévisions)
**Responsable :** Amine Arfaoui · **Port :** 8085 · **Techno :** Spring Boot

## Responsabilités (contexte borné : prévisions)
- Prévisions par ville sur 1 à 7 jours
- Récupération depuis l'API externe Open-Meteo et stockage
- Consultation et comparaison avec les relevés réels

## Modèle de données
Base : **PostgreSQL** — prévisions par (ville, date) avec contrainte d'unicité, mises à jour à chaque rafraîchissement.

| Entité | Attributs |
|---|---|
| Forecast | id, city, latitude, longitude, forecastDate, tempMin, tempMax, precipitation, windSpeed, fetchedAt |

## API REST

| Méthode | URI | Description | Codes |
|---|---|---|---|
| GET | /api/forecasts?city=&days=1..7 | Prévisions d'une ville | 200, 400, 404 |
| POST | /api/forecasts/refresh?city= | Récupérer depuis Open-Meteo et stocker | 201, 404, 502 |
| GET | /api/forecasts/{id} | Détail | 200, 404 |
| GET | /api/forecasts/compare?city=&date= | Prévision vs relevés réels | 200, 404 |

## Interactions
| Direction | Service | Mode | Données | Si indisponible |
|---|---|---|---|---|
| Appelle | Open-Meteo (externe) | HTTP (sync) | prévisions JSON | 502 + renvoi des dernières prévisions stockées |
| Appelle | station-service | OpenFeign (sync) | coordonnées de la ville | cache local |
| Appelle | readings-service | OpenFeign (sync) | relevés réels du jour | comparaison sans relevés (fallback) |
