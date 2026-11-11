# Diagramme de cas d'utilisation

Le fichier [`use-cases.puml`](use-cases.puml) décrit les cas d'utilisation du **Microservice Alertes et Vigilance**.

## Acteurs

| Acteur | Justification |
|---|---|
| Administrateur | Gère la configuration des seuils et des niveaux de vigilance. |
| Météorologue / Opérateur | Exploite les seuils et niveaux, déclenche ou traite les alertes et consulte leur historique. |
| Utilisateur | Consulte uniquement les alertes actives, leurs détails et l'historique. |
| Service Météo / Données | Fournit les données météorologiques et déclenche l'évaluation automatique lorsqu'un seuil peut être dépassé. |
| Service de Notification | Reçoit les alertes déclenchées afin de les diffuser aux systèmes ou utilisateurs concernés. |

## Résumé des cas d'utilisation

| Cas d'utilisation | Acteurs | Description courte |
|---|---|---|
| Définir un seuil | Administrateur, Météorologue / Opérateur | Créer un seuil pour un phénomène, une zone et une valeur donnée. |
| Modifier un seuil | Administrateur, Météorologue / Opérateur | Mettre à jour la définition d'un seuil. |
| Supprimer un seuil | Administrateur, Météorologue / Opérateur | Retirer un seuil devenu inutile. |
| Consulter les seuils | Administrateur, Météorologue / Opérateur | Voir les seuils configurés. |
| Activer ou désactiver un seuil | Administrateur, Météorologue / Opérateur | Contrôler si un seuil participe aux évaluations. |
| Définir un niveau de vigilance (VERT, JAUNE, ORANGE, ROUGE) | Administrateur, Météorologue / Opérateur | Définir les niveaux et leurs règles d'interprétation. |
| Consulter les niveaux de vigilance | Administrateur, Météorologue / Opérateur | Consulter la configuration des niveaux. |
| Évaluer le niveau de vigilance | Système (inclus par le déclenchement d'une alerte) | Déterminer le niveau applicable à partir des seuils et des données. |
| Déclencher automatiquement une alerte (seuil dépassé) | Service Météo / Données | Créer une alerte après détection automatique d'un dépassement. |
| Déclencher manuellement une alerte | Météorologue / Opérateur | Créer une alerte à la demande. |
| Modifier le niveau d'une alerte | Météorologue / Opérateur | Changer le niveau d'une alerte existante. |
| Consulter les alertes actives | Utilisateur, Météorologue / Opérateur | Afficher les alertes qui ne sont pas clôturées. |
| Consulter les détails d'une alerte | Utilisateur, Météorologue / Opérateur | Voir les informations détaillées d'une alerte. |
| Clôturer une alerte | Météorologue / Opérateur | Marquer une alerte comme terminée. |
| Consulter l'historique | Utilisateur, Météorologue / Opérateur | Consulter les alertes et actions historisées. |
| Filtrer l'historique (date, phénomène, niveau, zone) | Utilisateur, Météorologue / Opérateur | Restreindre l'historique selon les critères choisis. |
| Enregistrer dans l'historique | Système (inclus par les opérations d'alerte) | Conserver la trace des déclenchements, modifications et clôtures. |
| Notifier le service de notification | Service de Notification (système externe) | Transmettre une alerte déclenchée au service externe. |

## Visualiser ou exporter

Avec PlantUML installé localement, générer le PNG depuis la racine du projet :

```powershell
java -jar plantuml.jar docs\diagrammes\use-cases.puml
```

Le fichier `use-cases.png` sera créé à côté du fichier source. Pour un rendu SVG, remplacer `-tpng` par `-tsvg` :

```powershell
java -jar plantuml.jar -tsvg docs\diagrammes\use-cases.puml
```

PlantUML peut utiliser Graphviz pour améliorer la disposition. Si `dot` est installé et accessible dans le `PATH`, il sera détecté automatiquement.
