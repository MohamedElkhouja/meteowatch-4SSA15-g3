# MeteoWatch – Plateforme météo à base de microservices

*Projet AWD 2026-2027 – Validation 1 (conception microservices)*
Classe : 4SSA15 – Groupe : G3
Enseignant (collaborateur du dépôt) : @...

## 1. Description du sujet

MeteoWatch collecte les relevés des stations météo, les compare aux prévisions, déclenche des alertes de vigilance (vert, jaune, orange, rouge) quand des seuils sont dépassés, et notifie les utilisateurs abonnés à leur région. Un service commun de statistiques et de tableau de bord, avec une technologie avancée (prédiction), complète la plateforme.

**Problématique :** les alertes météo arrivent souvent trop tard, dispersées entre plusieurs sources, et ne sont pas adaptées à la région de chaque citoyen. MeteoWatch centralise les relevés, les prévisions et les alertes, puis prévient automatiquement les personnes concernées.

**Acteurs :**

- **Administrateur** : gère les stations et les capteurs, les règles d'alerte et les comptes utilisateurs (Keycloak).
- **Météorologue** : saisit et valide les relevés, consulte les prévisions, déclenche et clôture les vigilances.
- **Utilisateur (citoyen)** : consulte la météo de ses villes favorites, s'abonne aux alertes de sa région et reçoit les notifications.
- **Systèmes externes** : API Open-Meteo (prévisions), serveur email / push.

## 2. Équipe et microservices

Consigne : 3 microservices sur PostgreSQL, 3 sur MongoDB, **une base par microservice, aucune base partagée**.

| Module | Membre | Microservice | Base de données | Port |
|---|---|---|---|---|
| 1 – Gestion des stations météo | Maha Hammami | station-service | PostgreSQL (station_db) | 8081 |
| 2 – Gestion des alertes et vigilances | Linda Nebily | alert-service | PostgreSQL (alert_db) | 8082 |
| 3 – Gestion des utilisateurs et abonnements | Chaima Amri | user-service | PostgreSQL (subscription_db) | 8083 |
| 4 – Gestion des relevés météo | Ramzi Ben Hmida | readings-service | MongoDB (observation_db) | 8084 |
| 5 – Gestion des prévisions | Amine Arfaoui | forecast-service | MongoDB (forecast_db) | 8085 |
| 6 – Gestion des notifications | Mohamed Taher Elkhouja | notification-service | MongoDB (notification_db) | 8086 |
| 7 – Statistiques & tableau de bord (commun, techno avancée) | Toute l'équipe (présenté par Linda Nebily) | analytics-service | stats_db (à confirmer) | 8087 |

**Pourquoi PostgreSQL** pour Stations, Alertes et Abonnements : données structurées et liées, intégrité référentielle, transactions.
**Pourquoi MongoDB** pour Relevés, Prévisions et Notifications : gros volume, données semi-structurées et séries temporelles, schéma flexible.

Chaque README dans `Backend/<service>/` détaille les entités, le contrat d'API et les interactions du service.

**Tâches transverses :**

| Membre | Tâche transverse |
|---|---|
| Maha Hammami | Squelette du Frontend |
| Linda Nebily | Frontend et maquettes, présentation du service commun |
| Chaima Amri | Keycloak (rôles) |
| Ramzi Ben Hmida | API Gateway + Eureka |
| Amine Arfaoui | Config Server + Docker Compose |
| Mohamed Taher Elkhouja | README + document technique |

Outil de gestion de projet : **Jira** (sprints) – lien : ...

## 3. Fonctionnalités principales

- Gestion des stations et de leurs capteurs, recherche par ville
- Enregistrement et historique des relevés, statistiques journalières
- Règles de seuils, niveaux de vigilance, cycle de vie des alertes (ouverture, clôture)
- Profil utilisateur, villes favorites, abonnements aux alertes par région
- Prévisions (Open-Meteo) et comparaison avec les relevés réels
- Notifications (email, push) avec historique lu / non lu
- Tableau de bord, tendances et prédiction (service commun)

## 4. Architecture globale

Composants : Frontend Angular → **API Gateway** (Spring Cloud Gateway, sécurisée par **Keycloak** / JWT) → 7 microservices. **Eureka** pour la découverte des services, **Config Server** pour la configuration centralisée, **RabbitMQ** pour la communication asynchrone, une base par microservice, déploiement avec **Docker Compose**.

Diagramme : `Documentation/Diagrams/architecture-globale.png`

## 5. Interactions entre services

**Synchrone (OpenFeign)** – l'appelant a besoin de la réponse immédiatement :

| Appelant | Appelé | Données | Justification |
|---|---|---|---|
| readings-service | station-service | existence de la station (`GET /api/stations/{id}`) | valider le relevé avant de l'enregistrer |
| forecast-service | station-service | coordonnées GPS de la station | interroger l'API Open-Meteo |
| forecast-service | Open-Meteo (HTTP) | prévisions | source externe |
| notification-service | user-service | abonnés d'une région | savoir à qui envoyer l'alerte |

**Asynchrone (RabbitMQ)** – notifier sans bloquer l'émetteur :

| Événement | Publié par | Consommé par | Rôle |
|---|---|---|---|
| ObservationRecorded | readings-service | alert-service, analytics-service | alert-service vérifie les seuils ; analytics-service met à jour les statistiques |
| AlertTriggered | alert-service | notification-service, analytics-service | notification-service prévient les abonnés de la région |
| StationDeactivated | station-service | services intéressés | signaler qu'une station est désactivée |

**En cas de panne :**

- Appels synchrones : circuit breaker **Resilience4j** avec fallback côté appelant (exemple : forecast-service renvoie la dernière prévision en cache).
- Asynchrone : files RabbitMQ persistantes ; notification-service applique un retry puis une dead-letter queue.

## 6. Technologies

Spring Boot, Spring Cloud (Gateway, Eureka, Config Server, OpenFeign), Resilience4j, Keycloak, RabbitMQ, PostgreSQL, MongoDB, Angular, Docker / Docker Compose. Service commun : technologie avancée (Python FastAPI ou Spring AI, à confirmer avec l'enseignant). Git (GitHub) et Jira.

## 7. Structure du dépôt

```
meteowatch-4SSA15-g3/
├── Backend/
│   ├── station-service/   alert-service/      user-service/
│   ├── readings-service/  forecast-service/   notification-service/
│   └── analytics-service/                     # service commun
├── Frontend/
├── Documentation/
│   ├── Presentation/
│   ├── Diagrams/
│   └── CHECKLIST.md
├── CONTRIBUTING.md                            # règles Git (à lire en premier)
└── README.md
```

## 8. Lancement

```bash
# Prérequis : JDK 17+, Maven, Docker
cd Backend/<service> && mvn spring-boot:run      # services Spring Boot
docker compose up                                # une fois le compose ajouté
```

## 9. Planning prévisionnel et risques

**Planning :** à compléter (sprints Jira, jalons jusqu'à la validation finale).

**Risques principaux et réponses prévues :**

| Risque | Réponse |
|---|---|
| Intégration entre services (Feign, RabbitMQ) plus difficile que prévu | contrats d'API figés tôt, tests d'intégration dès le premier sprint |
| Configuration de Keycloak | rôles définis dès le début (ADMIN, METEOROLOGUE, USER), un responsable dédié |
| Quota ou indisponibilité de l'API Open-Meteo | cache des dernières prévisions |
| Un service en panne qui bloque les autres | circuit breaker avec fallback, files persistantes |
| Coordination à 6 personnes | une branche par service, Pull Requests vers develop, suivi sur Jira |
| Apprentissage de la technologie avancée du service commun | prototype tôt, travail en binôme |
