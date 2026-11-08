# user-service (Module 3 – Utilisateurs et abonnements)
**Responsable :** Chaima Amri · **Port :** 8083 · **Techno :** Spring Boot

## Responsabilités (contexte borné : profil et préférences)
- Profil utilisateur lié au compte Keycloak (aucun mot de passe stocké ici)
- Villes favorites
- Abonnement aux alertes par région et choix du canal (email, push)

## Modèle de données
Base : **MongoDB** — document utilisateur autonome avec listes imbriquées (favoris, abonnements), schéma flexible.

```json
User {
  "_id": "...", "keycloakId": "uuid", "email": "...", "fullName": "...",
  "favoriteCities": ["Tunis", "Sfax"],
  "subscriptions": [ { "region": "Nord", "channels": ["EMAIL", "PUSH"] } ]
}
```

## API REST

| Méthode | URI | Description | Codes |
|---|---|---|---|
| GET | /api/users/me | Mon profil | 200, 401 |
| PUT | /api/users/me | Modifier mon profil | 200, 400, 401 |
| POST | /api/users/me/favorites | Ajouter une ville favorite | 201, 400, 409 |
| DELETE | /api/users/me/favorites/{city} | Retirer | 204, 404 |
| POST | /api/users/me/subscriptions | S'abonner (région + canaux) | 201, 400 |
| DELETE | /api/users/me/subscriptions/{region} | Se désabonner | 204, 404 |
| GET | /api/users/subscribers?region= | Abonnés d'une région (usage interne) | 200 |

## Interactions
| Direction | Service | Mode | Données | Si indisponible |
|---|---|---|---|---|
| Appelé par | notification-service | OpenFeign (sync) | abonnés d'une région + canaux | notification-service réessaie (retry) puis met en attente |
| Dépend de | Keycloak | JWT | identité (sub, email) | accès refusé (401) |
