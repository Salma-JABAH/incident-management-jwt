# 🛡️ Gestion d'incidents informatiques – Spring Security · JWT · React

Application web permettant aux utilisateurs d'une entreprise de **déclarer et suivre des incidents informatiques ou de cybersécurité**
(malware, phishing, panne réseau, fuite de données…).
Projet réalisé pour le module *Sécurité des systèmes distribués* : authentification **JWT**, gestion des **rôles**, protection des routes
**backend et frontend**, contrôle d'accès aux données, validation, gestion d'erreurs, tableaux de bord, historique et **refresh token**.

**Technologies** : Spring Boot 3 · Spring Security 6 · JWT (jjwt) · JPA/Hibernate · H2 / MySQL · React 18 · Vite · Axios · React Router

---

## 📐 Architecture

![Architecture générale](docs/images/architecture.png)

*Figure A – Architecture générale (React → HTTP/JSON + Bearer JWT → Spring Boot → H2/MySQL)*

![Diagramme des entités](docs/images/er.png)

*Figure B – Diagramme des entités*

---

## ✅ Fonctionnalités réalisées

- [x] Inscription (`EMPLOYEE` imposé côté serveur) et connexion JWT
- [x] JWT contenant email, date de création, date d'expiration et rôle (durée de vie : 5 minutes)
- [x] **Refresh token** (rotation, révocation à la déconnexion)
- [x] Spring Security stateless, CORS, routes publiques `/auth/**` et `/h2-console/**`, routes protégées `/api/**`
- [x] Autorisations par rôle : `hasRole`, `@PreAuthorize`, `@EnableMethodSecurity`
- [x] Contrôle du propriétaire d'un incident côté backend (403 si l'incident n'appartient pas à l'utilisateur)
- [x] Mots de passe encodés avec BCrypt ; comptes désactivables (un compte désactivé ne peut plus se connecter)
- [x] Validation des données (`@NotBlank`, `@Email`, `@Size`, `@Valid`)
- [x] Gestion centralisée des erreurs (`@RestControllerAdvice`) au format `{ "status": 404, "message": "…" }`
- [x] Incidents : création, consultation, affectation à un technicien, changement de statut, clôture
- [x] **Historique** des changements (`IncidentHistory`) et **notes de traitement** (`IncidentComment`)
- [x] Profil : modification du nom/email + changement de mot de passe (vérification de l'ancien)
- [x] Dashboards adaptés aux rôles EMPLOYEE / TECHNICIAN / ADMIN
- [x] React : `ProtectedRoute`, `RoleRoute`, interceptors Axios (Bearer JWT + gestion du 401 / session expirée)
- [x] Collection Postman des tests de sécurité

---

## 📄 Rapport

Le rapport complet du devoir est disponible dans [`docs/`](docs/) :
[Word](docs/JABAH_SALMA_CCN3_Rapport_Devoir_JWT_Spring_React.docx) · [PDF](docs/JABAH_SALMA_CCN3_Rapport_Devoir_JWT_Spring_React.pdf)

---

## 🚀 Lancer le projet

### Prérequis
- JDK 17+
- Maven 3.9+
- Node.js 18+ et npm

### 1. Backend (Spring Boot)
```bash
cd backend
mvn spring-boot:run
```
API disponible sur **http://localhost:8080**. Les comptes de test sont créés au démarrage.

### 2. Frontend (React + Vite)
```bash
cd frontend
npm install
npm run dev
```
Application disponible sur **http://localhost:5173**.
(L'URL de l'API peut être changée avec `VITE_API_URL`, voir `frontend/.env.example`.)

### Ports utilisés

| Service | Port | URL |
|---|---|---|
| Backend Spring Boot | 8080 | http://localhost:8080 |
| Frontend React (Vite) | 5173 | http://localhost:5173 |
| Console H2 | 8080 | http://localhost:8080/h2-console |

Console H2 : JDBC URL `jdbc:h2:mem:incidentsdb`, utilisateur `sa`, mot de passe vide.

### Utiliser MySQL à la place de H2
Dans `backend/src/main/resources/application.properties`, commenter la section H2 et décommenter la section MySQL
(créer/adapter l'utilisateur et le mot de passe). La base `incidentsdb` est créée automatiquement.

### Configuration JWT
Le secret se définit via la variable d'environnement `JWT_SECRET` (≥ 32 caractères). Durées : `app.jwt.expiration-ms` (5 min) et
`app.jwt.refresh-expiration-ms` (7 jours).

---

## 👤 Comptes de test

| Rôle | Email | Mot de passe |
|---|---|---|
| ADMIN | `admin@example.com` | `Admin123!` |
| TECHNICIAN | `tech1@example.com` | `Tech123!` |
| TECHNICIAN | `tech2@example.com` | `Tech123!` |
| EMPLOYEE | `employee1@example.com` | `Employee123!` |
| EMPLOYEE | `employee2@example.com` | `Employee123!` |

> Les rôles `ADMIN` et `TECHNICIAN` ne peuvent pas être obtenus via `/auth/register` : ils sont créés au démarrage par `DataInitializer`.
> Toute inscription crée un `EMPLOYEE`.

## 🎭 Rôles disponibles

| Rôle | Droits principaux |
|---|---|
| `EMPLOYEE` | Gérer son profil, créer des incidents, consulter **uniquement ses propres** incidents |
| `TECHNICIAN` | Consulter les incidents **qui lui sont affectés**, changer leur statut, ajouter des notes de traitement |
| `ADMIN` | Voir tous les incidents et utilisateurs, activer/désactiver des comptes, affecter les incidents, voir les statistiques, clôturer |

---

## 🔌 Principaux endpoints

### Authentification (publics)
| Méthode | URL | Description |
|---|---|---|
| POST | `/auth/register` | Créer un compte (rôle EMPLOYEE) |
| POST | `/auth/login` | Se connecter → `token`, `refreshToken`, `role`, `fullName` |
| POST | `/auth/refresh` | Obtenir un nouvel access token à partir du refresh token |
| POST | `/auth/logout` | Révoquer le refresh token |

### Profil (authentifié)
| Méthode | URL | Description |
|---|---|---|
| GET | `/api/profile` | Consulter son profil |
| PUT | `/api/profile` | Modifier nom / email (renvoie un nouveau token) |
| PUT | `/api/profile/password` | Changer le mot de passe |

### Incidents
| Méthode | URL | Accès |
|---|---|---|
| POST | `/api/incidents` | EMPLOYEE |
| GET | `/api/incidents/my` | EMPLOYEE |
| GET | `/api/incidents/{id}` | Propriétaire, technicien affecté ou ADMIN |
| GET | `/api/incidents/assigned` | TECHNICIAN |
| PUT | `/api/incidents/{id}/status` | TECHNICIAN (si affecté) ou ADMIN |
| PUT | `/api/incidents/{id}/assign/{technicianId}` | ADMIN |
| POST | `/api/incidents/{id}/comments` | TECHNICIAN (si affecté) |
| GET | `/api/incidents` | ADMIN |

### Administration (ADMIN)
| Méthode | URL | Description |
|---|---|---|
| GET | `/api/admin/users` | Liste des utilisateurs |
| PUT | `/api/admin/users/{id}/enable` | Activer un compte |
| PUT | `/api/admin/users/{id}/disable` | Désactiver un compte |
| GET | `/api/admin/statistics` | Statistiques globales |

### Format des erreurs
```json
{ "status": 404, "message": "Incident introuvable" }
```
Les erreurs de validation ajoutent un objet `errors` (champ → message).

---

## 🔒 Règles métier de statut

- À la création : `OPEN`. À l'affectation par l'admin : `ASSIGNED`.
- Le technicien peut passer l'incident à `IN_PROGRESS` ou `RESOLVED` ; seul l'admin peut passer à `CLOSED`.
- Un incident `CLOSED` n'est plus modifiable (statut, affectation, notes).
- `OPEN` et `ASSIGNED` sont gérés automatiquement et ne peuvent pas être imposés manuellement.

---

## 🧪 Tests de sécurité (Postman)

Importer `postman/incidents.postman_collection.json` dans Postman, lancer le dossier **0. Préparation**
(connexions + création d'incidents), puis le dossier **1. Tests de sécurité obligatoires**. Chaque requête vérifie le code HTTP attendu :

| # | Test | Résultat attendu |
|---|---|---|
| 1 | API protégée sans JWT | 401 |
| 2 | JWT invalide | 401 |
| 3 | JWT expiré | 401 |
| 4 | EMPLOYEE sur une route ADMIN | 403 |
| 5 | EMPLOYEE lisant l'incident d'un autre employé | 403 |
| 6 | TECHNICIAN modifiant un incident non affecté | 403 |
| 7 | Utilisateur désactivé qui se connecte | 403 |
| 8 | Inscription avec email déjà existant | 409 |
| 9 | Création d'incident avec données invalides | 400 |

---

## 📁 Structure du dépôt

```
.
├── backend/                  # Spring Boot (config, security, entity, repository, dto, service, controller, exception)
├── frontend/                 # React (components, pages, services, context)
├── postman/                  # Collection Postman
├── docs/                     # Diagrammes, captures, rapport
└── README.md
```

---

## 🖼️ Captures de l'application

> Placer les images dans `docs/screenshots/` avec les noms ci-dessous.

![Page de connexion (Login)](docs/screenshots/01-login.png)

*Figure 1 – Page de connexion (Login)*

![Page d'inscription : le compte créé reçoit automatiquement le rôle EMPLOYEE](docs/screenshots/02-register.png)

*Figure 2 – Page d'inscription : le compte créé reçoit automatiquement le rôle EMPLOYEE*

![Tableau de bord EMPLOYEE : compteurs et derniers incidents](docs/screenshots/03-dashboard-employee.png)

*Figure 3 – Tableau de bord EMPLOYEE : compteurs et derniers incidents*

![Formulaire de déclaration d'un incident (NewIncident)](docs/screenshots/04-new-incident.png)

*Figure 4 – Formulaire de déclaration d'un incident (NewIncident)*

![Liste des incidents de l'employé (MyIncidents)](docs/screenshots/05-my-incidents.png)

*Figure 5 – Liste des incidents de l'employé (MyIncidents)*

![Détail d'un incident avec son historique et les notes de traitement](docs/screenshots/06-incident-details.png)

*Figure 6 – Détail d'un incident avec son historique et les notes de traitement*

![Tableau de bord TECHNICIAN](docs/screenshots/07-dashboard-technician.png)

*Figure 7 – Tableau de bord TECHNICIAN*

![Incidents affectés au technicien (AssignedIncidents)](docs/screenshots/08-assigned-incidents.png)

*Figure 8 – Incidents affectés au technicien (AssignedIncidents)*

![Changement de statut et ajout d'une note de traitement par le technicien](docs/screenshots/09-status-and-note.png)

*Figure 9 – Changement de statut et ajout d'une note de traitement par le technicien*

![Tableau de bord ADMIN : statistiques globales](docs/screenshots/10-dashboard-admin.png)

*Figure 10 – Tableau de bord ADMIN : statistiques globales*

![Gestion des utilisateurs : activation / désactivation (AdminUsers)](docs/screenshots/11-admin-users.png)

*Figure 11 – Gestion des utilisateurs : activation / désactivation (AdminUsers)*

![Liste de tous les incidents et affectation à un technicien (AdminIncidents)](docs/screenshots/12-admin-incidents-assign.png)

*Figure 12 – Liste de tous les incidents et affectation à un technicien (AdminIncidents)*

![Profil : modification du nom, de l'email et changement du mot de passe](docs/screenshots/13-profile.png)

*Figure 13 – Profil : modification du nom, de l'email et changement du mot de passe*

![Redirection vers Login avec le message « session expirée » après un 401](docs/screenshots/14-session-expired.png)

*Figure 14 – Redirection vers Login avec le message « session expirée » après un 401*

![RoleRoute : un EMPLOYEE qui ouvre /admin/users obtient « Accès refusé »](docs/screenshots/15-role-route-denied.png)

*Figure 15 – RoleRoute : un EMPLOYEE qui ouvre /admin/users obtient « Accès refusé »*

![Console H2 : mots de passe stockés encodés avec BCrypt](docs/screenshots/16-h2-bcrypt.png)

*Figure 16 – Console H2 : mots de passe stockés encodés avec BCrypt*


## 🖼️ Captures des tests Postman

> Placer les images dans `docs/postman-captures/` avec les noms ci-dessous.

![POST /auth/login : réponse contenant token, refreshToken, role et fullName](docs/postman-captures/pm-00-login.png)

*Figure 17 – POST /auth/login : réponse contenant token, refreshToken, role et fullName*

![Test 1 – API protégée sans JWT : 401 Unauthorized](docs/postman-captures/pm-01-no-jwt.png)

*Figure 18 – Test 1 – API protégée sans JWT : 401 Unauthorized*

![Test 2 – JWT invalide : 401 Unauthorized](docs/postman-captures/pm-02-invalid-jwt.png)

*Figure 19 – Test 2 – JWT invalide : 401 Unauthorized*

![Test 3 – JWT expiré (après 5 minutes) : 401 Unauthorized](docs/postman-captures/pm-03-expired-jwt.png)

*Figure 20 – Test 3 – JWT expiré (après 5 minutes) : 401 Unauthorized*

![Test 4 – EMPLOYEE sur /api/admin/users : 403 Forbidden](docs/postman-captures/pm-04-employee-admin-route.png)

*Figure 21 – Test 4 – EMPLOYEE sur /api/admin/users : 403 Forbidden*

![Test 5 – EMPLOYEE lisant l'incident d'un autre employé : 403 Forbidden](docs/postman-captures/pm-05-other-employee-incident.png)

*Figure 22 – Test 5 – EMPLOYEE lisant l'incident d'un autre employé : 403 Forbidden*

![Test 6 – TECHNICIAN modifiant un incident non affecté : 403 Forbidden](docs/postman-captures/pm-06-technician-not-assigned.png)

*Figure 23 – Test 6 – TECHNICIAN modifiant un incident non affecté : 403 Forbidden*

![Test 7 – Utilisateur désactivé tentant de se connecter : 403 Forbidden](docs/postman-captures/pm-07-disabled-user-login.png)

*Figure 24 – Test 7 – Utilisateur désactivé tentant de se connecter : 403 Forbidden*

![Test 8 – Inscription avec un email déjà existant : 409 Conflict](docs/postman-captures/pm-08-duplicate-email.png)

*Figure 25 – Test 8 – Inscription avec un email déjà existant : 409 Conflict*

![Test 9 – Création d'incident avec données invalides : 400 Bad Request](docs/postman-captures/pm-09-invalid-incident.png)

*Figure 26 – Test 9 – Création d'incident avec données invalides : 400 Bad Request*

![Inscription avec role=ADMIN dans le corps : le compte créé est EMPLOYEE](docs/postman-captures/pm-10-register-role-ignored.png)

*Figure 27 – Inscription avec role=ADMIN dans le corps : le compte créé est EMPLOYEE*

![POST /auth/refresh : obtention d'un nouvel access token](docs/postman-captures/pm-11-refresh.png)

*Figure 28 – POST /auth/refresh : obtention d'un nouvel access token*

![Modification d'un incident CLOSED refusée : 400 Bad Request](docs/postman-captures/pm-12-closed-incident.png)

*Figure 29 – Modification d'un incident CLOSED refusée : 400 Bad Request*


---

## 👥 Auteur

**Salma JABAH** – II-CCN (Cybersécurité et Confiance Numérique) – Département Mathématique et Informatique – ENSET Mohammedia
