# NomDuProjet

> Projet AWD 2026-2027 – Validation 1 (conception microservices)
> Classe : 4XXX – Groupe : GX
> Enseignant (collaborateur du dépôt) : @...

## 1. Description du sujet
_Idée générale, problème réel résolu, périmètre (3-5 lignes) – à remplir par toute l'équipe._

## 2. Équipe et responsabilités

| # | Membre | GitHub | Microservice | Base de données | Port |
|---|---|---|---|---|---|
| 1 | Nom Prénom | @... | `service-1` : ... | ... | 8081 |
| 2 | Nom Prénom | @... | `service-2` : ... | ... | 8082 |
| 3 | Nom Prénom | @... | `service-3` : ... | ... | 8083 |
| 4 | Nom Prénom | @... | `service-4` : ... | ... | 8084 |
| 5 | Nom Prénom | @... | `service-5` : ... (technologie avancée) | ... | 8085 |
| 6 | Nom Prénom | @... | `service-6` : ... | ... | 8086 |
| Tous | Toute l'équipe | | `common-service` (présenté par un seul membre) | ... | 8080 |

Renommez les dossiers `service-X` avec le nom réel du microservice.

Outil de gestion de projet : _GitHub Projects / Trello / Jira – lien_

## 3. Acteurs et fonctionnalités principales
- **Acteur 1** : ...
- **Acteur 2** : ...

## 4. Architecture
![Architecture globale](Documentation/Diagrams/architecture-globale.png)

Frontend, API Gateway, Eureka (Discovery), Config Server, Keycloak (sécurité), microservices, une base par service, Docker.

## 5. Interactions entre services

| Appelant | Appelé | Mode (OpenFeign / asynchrone) | Données | Justification |
|---|---|---|---|---|
| ... | ... | ... | ... | ... |

## 6. Structure du dépôt
```
├── Backend/            # un projet par microservice (+ common-service)
├── Frontend/           # application cliente
├── Documentation/
│   ├── Presentation/   # présentation séance 4
│   ├── Diagrams/       # architecture, classes, cas d'utilisation, séquences
│   └── CHECKLIST.md
├── CONTRIBUTING.md     # règles de travail Git (à lire en premier)
└── README.md
```

## 7. Lancement
```bash
# Prérequis : JDK 17+, Maven, Docker
cd Backend/<service> && mvn spring-boot:run
# ou, une fois le docker-compose ajouté :
docker compose up
```

## 8. Planning prévisionnel et risques
_Résumé (détails dans le dossier technique)._
