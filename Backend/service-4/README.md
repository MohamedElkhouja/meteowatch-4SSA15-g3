# Nom du microservice
**Responsable :** Nom Prénom (@github)

## Responsabilités (contexte borné)
- ...

## Modèle de données
Base : _MySQL / PostgreSQL / MongoDB / H2_ — justification : ...

| Entité | Attributs principaux |
|---|---|
| ... | ... |

Diagramme de classes : `../../Documentation/Diagrams/<service>/classes.png`

## API REST

| Méthode | URI | Description | Codes de réponse |
|---|---|---|---|
| GET | /api/... | ... | 200, 404 |
| POST | /api/... | ... | 201, 400 |
| PUT | /api/.../{id} | ... | 200, 404 |
| DELETE | /api/.../{id} | ... | 204, 404 |

## Interactions
| Direction | Service | Mode | Données | Si indisponible |
|---|---|---|---|---|
| Appelle | ... | OpenFeign (sync) | ... | fallback / réponse dégradée |
| Appelé par | ... | ... | ... | ... |
| Publie / consomme | événement `...` | async | ... | ... |

## Lancement
```bash
mvn spring-boot:run
```
