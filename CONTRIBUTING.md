# Guide de contribution (équipe de 6)

## Règle d'or
Chaque membre travaille **uniquement dans le dossier de son microservice** (`Backend/service-X/`). Cela évite les conflits et prouve la contribution de chacun (critère I3 : commits personnels réguliers).

## Mise en route
```bash
git clone https://github.com/<org-ou-user>/<nom-du-projet-classe-groupe>.git
cd <nom-du-projet-classe-groupe>
git checkout develop
git checkout -b feature/service-X-init
```

## Branches
- `main` : version stable, rendue à l'enseignant. Ne jamais pousser directement.
- `develop` : intégration de l'équipe.
- `feature/service-X-<description>` : une branche par tâche (ex. `feature/service-2-entity-user`).

## Workflow
1. `git pull origin develop` avant de commencer.
2. Travailler sur sa branche `feature/...`.
3. Commits petits et fréquents.
4. Pousser puis ouvrir une Pull Request vers `develop`.
5. Au moins un autre membre relit et valide, puis merge.
6. `develop` est fusionnée dans `main` pour chaque jalon (ex. avant la validation).

## Messages de commit
Format : `type(service): description`
- `feat(service-1): ajout de l'entité Produit`
- `fix(service-3): correction du code de réponse 404`
- `docs: diagramme de classes service-2`
- `chore(common): configuration Eureka`

## Fichiers partagés (à modifier avec précaution, via PR)
- `README.md`, `Documentation/`, `docker-compose.yml`, `common-service`
- Documentation : chaque membre ajoute ses diagrammes dans `Documentation/Diagrams/<service-X>/`.

## À faire dans chaque microservice
- Projet initialisé (Spring Initializr ou autre techno prévue), compile et démarre.
- Compléter le `README.md` du service (responsabilités, entités, base, API, interactions).
- Enregistrement dans Eureka, configuration via Config Server.
- Une base de données dédiée (jamais partagée).

## Tâches de l'équipe entière
- Microservice commun (un seul membre le présente).
- Diagramme d'architecture globale, cas d'utilisation général.
- Dossier technique et présentation (répartir les sections équitablement).
- Chaque membre prépare 1 min de speech sur son microservice.
