# notification-service (Module 6 – Notifications)
**Responsable :** Mohamed Taher Elkhouja · **Port :** 8086 · **Techno :** Spring Boot

## Responsabilités (contexte borné : diffusion des messages)
- Envoi des notifications suite aux alertes (email, push)
- Historique par utilisateur (lue / non lue)
- Modèles de messages

## Modèle de données
Base : **MySQL** — historique transactionnel (statut d'envoi, lu / non lu) et modèles structurés.

| Entité | Attributs |
|---|---|
| Notification | id, userId, alertId, channel (EMAIL, PUSH), subject, body, status (SENT, FAILED, PENDING), read, createdAt |
| MessageTemplate | id, code, channel, subject, body (avec variables {{region}}, {{level}}) |

## API REST

| Méthode | URI | Description | Codes |
|---|---|---|---|
| GET | /api/notifications/me | Mes notifications (`?read=false`) | 200, 401 |
| PATCH | /api/notifications/{id}/read | Marquer comme lue | 200, 404 |
| GET | /api/notifications/me/unread-count | Nombre de non lues | 200 |
| POST | /api/templates | Créer un modèle | 201, 400 |
| GET | /api/templates | Lister | 200 |
| PUT | /api/templates/{id} | Modifier | 200, 404 |
| DELETE | /api/templates/{id} | Supprimer | 204, 404 |

Événement consommé : `alert.triggered`.

## Interactions
| Direction | Service | Mode | Données | Si indisponible |
|---|---|---|---|---|
| Consomme | alert-service | async (RabbitMQ) | `alert.triggered` | message conservé dans la file |
| Appelle | user-service | OpenFeign (sync) | abonnés + canaux de la région | retry puis notification PENDING rejouée |
| Appelle | serveur SMTP / push | sync | envoi | statut FAILED, nouvelle tentative |
