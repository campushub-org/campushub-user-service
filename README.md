# campushub-user-service - Service de Gestion des Utilisateurs

Ce service est responsable de la gestion des utilisateurs au sein de l'écosystème Campushub. Il gère les opérations CRUD (Create, Read, Update, Delete) pour les utilisateurs, l'authentification, l'autorisation et potentiellement d'autres fonctionnalités liées aux utilisateurs.

### Fonctionnalités

*   **Gestion des utilisateurs**: Création, lecture, mise à jour et suppression des comptes utilisateurs.
*   **Authentification & Autorisation**: Gère l'accès et les permissions des utilisateurs.
*   **Intégration Eureka**: S'enregistre auprès de `campushub-registry` et peut découvrir d'autres services.
*   **Configuration centralisée**: Obtient sa configuration de `campushub-config`.
*   **Persistance des données**: Interagit avec une base de données MySQL (`campushub-user-db`) pour stocker les informations des utilisateurs.

### Comment ça marche

`campushub-user-service` est une application Spring Boot. Au démarrage, il se connecte à `campushub-config` pour obtenir sa configuration, s'enregistre auprès de `campushub-registry`, et se connecte à sa base de données MySQL dédiée (`campushub-user-db`). Il expose ensuite des API REST pour interagir avec les données utilisateurs.

### Commandes Utiles

#### Construire le service (localement, sans Docker)

Pour construire le fichier JAR exécutable du service:

```bash
cd campushub-deployment/campushub-user-service
./mvnw clean install -DskipTests -Dspring.cloud.config.uri=http://localhost:8888
```
*(Note: L'option `-Dspring.cloud.config.uri=http://localhost:8888` est nécessaire pour que les tests et la construction locale puissent se connecter au service `campushub-config` si celui-ci est démarré via Docker sur votre machine locale. `-DskipTests` est utilisé car les tests requièrent une base de données MySQL et d'autres services qui peuvent ne pas être disponibles localement.)*

#### Exécuter le service (localement, sans Docker)

Assurez-vous d'avoir construit le JAR au préalable.

```bash
cd campushub-deployment/campushub-user-service
java -jar target/campushub-user-service.jar
```

Le service sera accessible sur `http://localhost:8081`.

#### Construire et exécuter avec Docker Compose

Dans le répertoire `campushub-deployment`, ce service est défini dans le fichier `docker-compose.yml`.

Pour construire l'image Docker (cela inclut la construction du JAR si ce n'est pas déjà fait):

```bash
cd campushub-deployment
docker-compose build campushub-user-service
```

Pour démarrer le conteneur du service:

```bash
cd campushub-deployment
docker-compose up -d campushub-user-service
```

Pour vérifier les logs du service une fois démarré:

```bash
cd campushub-deployment
docker-compose logs campushub-user-service
```
