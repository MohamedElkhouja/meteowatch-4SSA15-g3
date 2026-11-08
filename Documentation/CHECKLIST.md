# Checklist – Validation 1 (séance 4, semaine du 9/11/2026)

## Dépôt Git (E5, I3) – 2 pts + 1 pt
- [ ] Dépôt public nommé `nom-du-projet-classe-groupe`
- [ ] Enseignant ajouté comme collaborateur
- [ ] Structure : Backend/, Frontend/, Documentation/Presentation, Documentation/Diagrams, README.md
- [ ] Un projet créé par microservice dans Backend/ (4 services + commun + service 5), qui démarre
- [ ] README complet (sujet, membres, architecture, lancement)
- [ ] Commits de TOUS les membres (réguliers)
- [ ] Outil de gestion de projet avec tâches visibles

## Diagrammes (Documentation/Diagrams)
- [ ] Architecture globale (Frontend, API Gateway, Eureka, Config Server, Keycloak, microservices, bases, Docker)
- [ ] Cas d'utilisation général (acteurs)
- [ ] Diagramme de classes par microservice
- [ ] Séquences : exemple synchrone (OpenFeign) et asynchrone

## Dossier technique Word/LaTeX (E1-E4)
- [ ] Description du sujet, problématique
- [ ] Acteurs et fonctionnalités
- [ ] Découpage en microservices (par contexte borné, pas par table)
- [ ] Données gérées par service, une base par service (choix justifié)
- [ ] Interactions entre services (sync/async + justification + cas de panne)
- [ ] Diagramme d'architecture, technologies
- [ ] Contrats d'API par service (endpoints, codes de réponse, événements)
- [ ] Répartition des tâches, planning, risques
- Contient TOUT le contenu de la présentation, en plus détaillé

## Présentation 10-15 min (E6)
- [ ] Partie groupe : idée, fonctionnalités, architecture, microservices/BDD, technologies, tâches, Git, planning/risques
- [ ] Partie individuelle (1 min max / étudiant) : classes + interactions + API de son microservice
- [ ] Microservice commun présenté par un seul membre
- [ ] Parole répartie équitablement
- [ ] Déposée dans Documentation/Presentation

## Individuel (I1, I2, I4)
- [ ] Conception : responsabilités, entités, modèle de données, choix de base, API REST (URI, méthodes, codes)
- [ ] Interactions : appelants/appelés, données, sync/async, comportement si service indisponible
- [ ] Maîtriser : contexte borné, base par service, API Gateway, Eureka, sync/async

## Blackboard (un seul membre)
- [ ] Lien du dépôt Git + nom du projet + membres
- [ ] Document technique
- [ ] Présentation
- [ ] Diagramme d'architecture globale

## Rappels
- Absence non justifiée = 0 ; étudiant non impliqué = 0
- Microservice non présenté par son responsable = 0 en I1-I3
- Dépôt inaccessible le jour J = 0 en E5
- Pas de logique métier attendue pour l'instant
