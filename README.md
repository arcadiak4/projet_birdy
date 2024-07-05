# Projet Birdy
Implémentation de services web en Java d'un réseau social de type Twitter.

## Technologies utilisées :
- Java pour le développement des services web :
  - Servlets : composants essentiels pour la gestion des requêtes et des réponses HTTP.
  - Authentification des utilisateurs : mise en place de l'authentification des utilisateurs avec des sessions pour gérer les connexions et maintenir la sécurité des données utilisateur.
  - Gestion des posts et commentaires :
      - Récupération des posts ou commentaires : fournir une méthode pour obtenir des détails sur un post spécifique et ses commentaires.
      - Ajout de posts : permettre aux utilisateurs de créer de nouveaux posts ou de commenter des posts.
      - Suppression de posts : offrir la possibilité de supprimer des posts ou commentaires existants (suppression récursive).
  - Gestion des likes :
    - Récupération des likes : fournir une méthode pour obtenir la liste de likes d'un post ou d'un commentaire spécifique.
    - Ajout de likes : permettre aux utilisateurs de liker des posts ou des commentaires.
    - Suppression de likes : offrir la possibilité de retirer un like d'un post ou d'un commentaire.
  - Abonnements aux comptes :
    - Suivre des comptes : permettre aux utilisateurs de suivre d'autres comptes.
    - Gestion des abonnements : fournir des fonctionnalités pour gérer la liste des abonnements et abonnés.
- Systèmes de gestion de base de données :
  - MySQL avec phpMyAdmin : utilisé pour la gestion des données relationnelles telles que les utilisateurs, les abonnements et les clés de session, avec l'API JDBC.
  - MongoDB : utilisé pour la gestion des données non relationnelles telles que les posts et les commentaires, avec le driver MongoDB pour Java.
  - Postman : utilisé pour tester les API afin de vérifier le bon fonctionnement des endpoints.
