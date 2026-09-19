# Backend — ERP GZOULI

API REST Spring Boot qui expose les ressources métier de l'ERP.

---

## Stack technique

| Technologie | Version |
|-------------|---------|
| Java | 17 |
| Spring Boot | 3.5.x |
| Build tool | Maven |
| Base de données | PostgreSQL 16 (prod) / H2 (tests) |
| Authentification | AWS Cognito — OAuth2 Resource Server (JWT) |
| Stockage documents | AWS SDK S3 |
| Export rapports | Apache POI (Excel) |
| Gestionnaire secrets | AWS Secrets Manager |

---

## Modules fonctionnels

| Controller | Route base | Rôle |
|------------|-----------|------|
| `ProjectController` | `/api/projects` | Création et suivi des projets |
| `ProjectTaskController` | `/api/projects/{id}/tasks` | Tâches d'un projet |
| `ProjectEquipmentController` | `/api/projects/{id}/equipment` | Affectation d'équipements |
| `ProjectMissionOrderController` | `/api/projects/{id}/mission-orders` | Ordres de mission |
| `SiteJournalController` | `/api/projects/{id}/journal` | Journal de chantier + photos |
| `EmployeeController` | `/api/employees` | Gestion des employés |
| `CarController` | `/api/cars` | Flotte de véhicules et affectations |
| `InventoryController` | `/api/inventory` | Équipements et stocks |
| `ExpenseController` | `/api/expenses` | Notes de frais |
| `InvoiceController` | `/api/invoices` | Factures |
| `ReportController` | `/api/reports` | Export Excel (Apache POI) |
| `S3Controller` | `/api/s3` | URLs pré-signées upload/download |
| `DashboardController` | `/api/dashboard` | KPIs agrégés |

Toutes les routes sont protégées par JWT Cognito. La sécurité est configurée dans `SecurityConfig.java`.

---

## Prérequis

- Java 17
- Maven 3.8+ (ou utiliser le wrapper `./mvnw`)
- PostgreSQL 16 accessible (ou utiliser Docker Compose)
- AWS CLI configuré avec un profil `gzouli-local` (pour Cognito et S3 en dev)

---

## Profils Spring Boot

| Profil | Déclenchement | BDD | Variables attendues |
|--------|---------------|-----|---------------------|
| *(défaut)* | `application.yaml` | H2 in-memory | Aucune (autonome) |
| `dev` | `SPRING_PROFILES_ACTIVE=dev` | PostgreSQL | `GZOULI_DB_HOST/PORT/NAME/USER/PASSWORD`, `AWS_*` |
| `docker` | `SPRING_PROFILES_ACTIVE=docker` | PostgreSQL | `DB_HOST/PORT/NAME/USER/PASSWORD`, `AWS_*` |
| `prod` | `SPRING_PROFILES_ACTIVE=prod` | PostgreSQL | `DB_HOST/NAME/USER/PASSWORD`, `AWS_*` |

---

## Lancement en local

### Option 1 — Docker Compose (recommandée)

Lance le backend, le frontend et PostgreSQL en un seul appel depuis la racine du dépôt :

```bash
cd docker
docker compose up --build
```

Le backend est accessible sur http://localhost:8080.

### Option 2 — Backend seul (profil dev)

Nécessite un PostgreSQL local (ou le démarrer via `docker/docker-compose.db.yml`).

```bash
# Démarrer uniquement la base de données
cd docker && docker compose -f docker-compose.db.yml up -d

# Lancer le backend
cd backend
export SPRING_PROFILES_ACTIVE=dev
export GZOULI_DB_HOST=localhost
export GZOULI_DB_PORT=5434
export GZOULI_DB_NAME=gzouli_db
export GZOULI_DB_USER=<user>
export GZOULI_DB_PASSWORD=<password>
export AWS_REGION=eu-west-3
export AWS_COGNITO_USER_POOL_ID=<user_pool_id>
export AWS_S3_BUCKET_NAME=<bucket_documents>

./mvnw spring-boot:run
```

### Healthcheck

```
GET http://localhost:8080/actuator/health
```

---

## Build

```bash
# Build + tests
./mvnw clean package

# Build sans tests
./mvnw clean package -DskipTests
```

L'artefact généré est `target/*.jar`.

### Dockerfile

Build multi-stage : compilation avec `eclipse-temurin:17-jdk` → exécution avec `eclipse-temurin:17-jre-alpine`. Les packages Alpine sont mis à jour au build pour éliminer les CVEs.

```bash
docker build -t gzouli-backend .
docker run -p 8080:8080 --env-file .env gzouli-backend
```

---

## Tests

```bash
./mvnw test
```

Les tests utilisent H2 in-memory (profil `test`, `application-test.yml`). L'authentification Cognito est mockée via `TestSecurityConfig.java`.

---

## Structure des sources

```
src/main/java/com/gzouli/ERP/
├── config/          # SecurityConfig, AwsConfig
├── controller/      # Endpoints REST
├── entity/          # Entités JPA (Project, Employee, Car, Task, ...)
├── dao/             # Repositories Spring Data JPA
├── dto/             # Objets de transfert (requête/réponse)
├── enums/           # Role, ExpenseType, MissionOrderType
├── exception/       # GlobalExceptionHandler, exceptions métier
├── aspect/          # ControllerLoggingAspect (logs entrants/sortants)
└── ErpApplication.java
```

---

## Variables d'environnement (référence complète)

| Variable | Profil | Description |
|----------|--------|-------------|
| `SPRING_PROFILES_ACTIVE` | tous | `dev`, `docker` ou `prod` |
| `DB_HOST` / `GZOULI_DB_HOST` | docker / dev | Hôte PostgreSQL |
| `DB_PORT` / `GZOULI_DB_PORT` | docker / dev | Port PostgreSQL |
| `DB_NAME` / `GZOULI_DB_NAME` | docker / dev | Nom de la base |
| `DB_USER` / `GZOULI_DB_USER` | docker / dev | Utilisateur DB |
| `DB_PASSWORD` / `GZOULI_DB_PASSWORD` | docker / dev | Mot de passe DB |
| `AWS_REGION` | tous | Région AWS (`eu-west-3`) |
| `AWS_COGNITO_USER_POOL_ID` | tous | User Pool Cognito |
| `AWS_S3_BUCKET_NAME` | tous | Bucket documents S3 |

En prod, `DB_USER` et `DB_PASSWORD` sont injectés par ECS depuis Secrets Manager (secret RDS `rds!...`).
