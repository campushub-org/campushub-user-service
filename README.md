# 👤 CampusHub - User Service

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-6.2-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)](https://spring.io/projects/spring-security)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)

> Le **User Service** est le garant de la sécurité et de l'intégrité des identités au sein de l'écosystème CampusHub. Il gère l'authentification, les autorisations basées sur les rôles (RBAC) et les profils utilisateurs enrichis.

---

## 🚀 Fonctionnalités Clés

- **Authentification Stateless** : Implémentation complète de JWT (JSON Web Tokens).
- **Modèle de Données Hérité** : Gestion fine des types d'utilisateurs via une stratégie `JOINED` (Admin, Doyen, Enseignant, Étudiant).
- **RBAC (Role-Based Access Control)** : Sécurisation granulaire des endpoints par annotations `@PreAuthorize`.
- **Intégration Cloud** : Configuration centralisée et enregistrement automatique sur Eureka.
- **Initialisation Automatique** : Système de seeder intégré pour les environnements de test.

---

## 🛠️ Stack Technique

- **Core :** Spring Boot 3.2.5
- **Sécurité :** Spring Security + JJWT
- **Persistence :** Spring Data JPA + Hibernate
- **Base de données :** MySQL 8.0
- **Discovery :** Eureka Client

---

## 📡 API Endpoints Principaux

| Méthode | Path | Description | Accès |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/users/register` | Inscription d'un nouvel utilisateur | Public |
| `POST` | `/api/users/login` | Authentification et génération du JWT | Public |
| `GET` | `/api/users/:id` | Récupération du profil détaillé | Propriétaire / Admin |
| `PUT` | `/api/users/:id` | Mise à jour des informations de profil | Propriétaire / Admin |
| `DELETE` | `/api/users/:id` | Suppression d'un compte utilisateur | Admin |

---

## ⚙️ Configuration & Installation

### Build du package (Crucial)
Comme le `Dockerfile` utilise le fichier `.jar` généré localement, vous devez compiler le projet avant de build l'image Docker :

```bash
# Générer le JAR en sautant les tests
./mvnw clean package -DskipTests
```

### Lancement Local
```bash
# Lancer via Maven
./mvnw spring-boot:run
```

### Déploiement Docker
```bash
docker build -t campushub-user-service .
```

---
<p align="center">Sûreté et Confidentialité au service de l'éducation</p>
