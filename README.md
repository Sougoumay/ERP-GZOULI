# ERP GZOULI

Application de gestion d'entreprise (ERP) couvrant les projets, les employés, la flotte de véhicules, l'inventaire, les dépenses, les factures et la génération de rapports.

**Production :** https://gzouli.sougoumay.com

---

## Stack technique

| Couche | Technologie |
|--------|-------------|
| Frontend | Angular 21 + Angular Material |
| Backend | Spring Boot 3.5 — Java 17 |
| Base de données | PostgreSQL 16 |
| Authentification | AWS Cognito (OAuth2 / JWT) |
| Stockage documents | AWS S3 |
| Hébergement | AWS — CloudFront + ECS Fargate + RDS Multi-AZ |
| IaC | Terraform 1.10 |
| CI/CD | GitHub Actions |

---

## Architecture de production

```
Utilisateur
    │
    ▼
CloudFront (gzouli.sougoumay.com)
    ├── /* ──────────── S3 gzouli-frontend-prod  (Angular, OAC)
    └── /api/* ─────── ALB gzouli-alb
                            │
                            ▼
                       ECS Fargate (2 tasks, subnets privés)
                            │  Spring Boot :8080
                            ├── AWS Cognito  (auth)
                            ├── AWS S3       (documents)
                            └── RDS PostgreSQL Multi-AZ (subnets privés)
```

Région : `eu-west-3` (Paris). Pas d'accès public direct à ECS ni à RDS.

---

## Structure du dépôt

```
ERP-GZOULI/
├── backend/          # API Spring Boot (branche main)
├── frontend/         # Application Angular (branche main)
├── terraform/        # Infrastructure AWS (branche infra)
├── docker/           # Docker Compose pour le dev local (branche main)
└── .github/workflows/
    ├── ci-backend.yml    # Build → Scan → Push ECR → Deploy ECS
    ├── ci-frontend.yml   # Build → Scan → Deploy S3 + invalidation CloudFront
    └── ci-terraform.yml  # Lint → Plan (branche infra)
```

### Branches

| Branche | Contenu |
|---------|---------|
| `main` | Code applicatif (backend + frontend) + CI/CD applicatif |
| `infra` | Infrastructure Terraform + CI Terraform |

---

## Modules fonctionnels

- **Projets** — création, suivi des tâches, ordres de mission, journal de chantier
- **Employés** — gestion des employés, avances sur salaire
- **Véhicules** — flotte et affectations
- **Inventaire** — équipements et affectations projet
- **Dépenses** — suivi des notes de frais
- **Factures** — émission et suivi
- **Documents** — upload/download via URLs pré-signées S3
- **Rapports** — export Excel (Apache POI)
- **Dashboard** — KPIs agrégés

---

## Démarrage en local

### Prérequis

- Docker et Docker Compose
- AWS CLI configuré avec le profil `gzouli-local` (pour Cognito + S3)
- Fichier `.env` à la racine de `docker/` (voir ci-dessous)

### Variables d'environnement

Créer `docker/.env` à partir du modèle :

```env
GZOULI_DB_USER=gzouli
GZOULI_DB_PASSWORD=<mot_de_passe_local>
GZOULI_DB_NAME=gzouli_db
AWS_REGION=eu-west-3
AWS_COGNITO_USER_POOL_ID=<user_pool_id>
AWS_COGNITO_APP_CLIENT_ID=<app_client_id>
AWS_S3_BUCKET_NAME=<bucket_documents>
NVD_API_KEY=<clé_optionnelle_pour_OWASP_scan>
```

### Lancer la stack complète

```bash
cd docker
docker compose up --build
```

- Frontend : http://localhost:80
- Backend : http://localhost:8080
- BDD : localhost:5434 (PostgreSQL)

Le backend attend que la BDD soit `healthy` avant de démarrer (`depends_on` + healthcheck).

---

## CI/CD

Les pipelines se déclenchent automatiquement sur la branche `main` lors de modifications dans les dossiers respectifs.

| Pipeline | Déclencheur | Jobs |
|----------|-------------|------|
| Backend | push/PR sur `main` — `backend/**` | Build → Scan Trivy → Push ECR → Deploy ECS |
| Frontend | push/PR sur `main` — `frontend/**` | Build → Scan Trivy → Deploy S3 → Invalidation CloudFront |
| Terraform | push/PR sur `infra` — `terraform/**` | Lint (`fmt`) → Plan |

### Secrets GitHub requis

| Secret | Utilisation |
|--------|-------------|
| `AWS_ROLE_ARN` | Rôle IAM OIDC pour les pipelines backend + frontend |
| `AWS_ROLE_ARN_TERRAFORM` | Rôle IAM OIDC pour le pipeline Terraform |
| `CLOUDFRONT_DISTRIBUTION_ID` | ID de la distribution pour l'invalidation cache |

L'authentification AWS utilise OIDC (pas de clés d'accès statiques).

---

## Infrastructure

Voir [terraform/README.md](terraform/README.md) pour le détail complet de l'infrastructure, les modules Terraform et les instructions de déploiement.
