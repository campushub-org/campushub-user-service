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

### Endpoints de l'API

Voici la liste des endpoints disponibles pour ce service.

**Note importante :** Les exemples ci-dessous supposent que le `campushub-gateway-service` est en cours d'exécution sur `http://localhost:8080`. Grâce à la découverte de services, le gateway route automatiquement les requêtes avec le préfixe `/campushub-user-service` (le nom du service en minuscules) vers ce service.

---

#### 1. Inscription d'un nouvel utilisateur (Publique)

Crée un nouveau compte utilisateur. Pour des raisons de sécurité, il est recommandé de ne créer que des comptes `STUDENT` via cet endpoint.

- **Méthode :** `POST`
- **Path :** `/api/auth/register`
- **Permissions :** Publique

**Exemple `curl`:**
```bash
curl --location 'http://localhost:8080/campushub-user-service/api/auth/register' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "nouvel_etudiant",
    "password": "password123",
    "fullName": "Jean Dupont",
    "email": "jean.dupont@email.com",
    "department": "Informatique",
    "role": "STUDENT",
    "studentNumber": "E123456"
}'
```

---

#### 2. Connexion d'un utilisateur (Publique)

Authentifie un utilisateur et retourne un token JWT.

- **Méthode :** `POST`
- **Path :** `/api/auth/login`
- **Permissions :** Publique

**Exemple `curl`:**
```bash
curl --location 'http://localhost:8080/campushub-user-service/api/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "nouvel_etudiant",
    "password": "password123"
}'
```

---

#### 3. Lister tous les utilisateurs (Authentifié)

Récupère la liste de tous les utilisateurs.

- **Méthode :** `GET`
- **Path :** `/api/users`
- **Permissions :** Tout utilisateur authentifié

**Exemple `curl`:**
```bash
# Remplacez YOUR_JWT_TOKEN par un token valide
curl --location 'http://localhost:8080/campushub-user-service/api/users' \
--header 'Authorization: Bearer YOUR_JWT_TOKEN'
```

---

#### 4. Obtenir un utilisateur par ID (Propriétaire ou Admin)

Récupère les informations d'un utilisateur spécifique. Accessible uniquement par l'utilisateur lui-même ou un administrateur.

- **Méthode :** `GET`
- **Path :** `/api/users/{id}`
- **Permissions :** Propriétaire du compte ou `ADMIN`

**Exemple `curl` (en tant que propriétaire):**
```bash
# Remplacez YOUR_JWT_TOKEN par votre propre token valide
curl --location 'http://localhost:8080/campushub-user-service/api/users/1' \
--header 'Authorization: Bearer YOUR_JWT_TOKEN'
```

---

#### 5. Mettre à jour un utilisateur (Propriétaire ou Admin)

Met à jour les informations d'un utilisateur existant. Les utilisateurs non-administrateurs ne peuvent pas changer leur propre rôle.

- **Méthode :** `PUT`
- **Path :** `/api/users/{id}`
- **Permissions :** Propriétaire du compte ou `ADMIN`

**Exemple `curl` (en tant que propriétaire):**
```bash
# Remplacez YOUR_JWT_TOKEN par votre propre token valide
curl --location --request PUT 'http://localhost:8080/campushub-user-service/api/users/1' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer YOUR_JWT_TOKEN' \
--data-raw '{
    "fullName": "Jean Dupont (Modifié)",
    "email": "jean.dupont.modifie@email.com"
}'
```

---

#### 6. Supprimer un utilisateur (Propriétaire ou Admin)

Supprime un utilisateur par son ID. Accessible uniquement par l'utilisateur lui-même ou un administrateur.

- **Méthode :** `DELETE`
- **Path :** `/api/users/{id}`
- **Permissions :** Propriétaire du compte ou `ADMIN`

**Exemple `curl` (en tant que propriétaire):**
```bash
# Remplacez YOUR_JWT_TOKEN par votre propre token valide
curl --location --request DELETE 'http://localhost:8080/campushub-user-service/api/users/1' \                           
--header 'Authorization: Bearer YOUR_JWT_TOKEN'                                                                         
```

---

#### 7. Obtenir le rôle d'un utilisateur (Authentifié)

Récupère le rôle de l'utilisateur authentifié.

- **Méthode :** `GET`
- **Path :** `/api/users/role`
- **Permissions :** Tout utilisateur authentifié

**Exemple `curl`:**
```bash
curl --location 'http://localhost:8080/campushub-user-service/api/users/role' \
--header 'Authorization: Bearer YOUR_JWT_TOKEN'
```
