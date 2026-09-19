# Infrastructure — ERP GZOULI

Infrastructure AWS provisionnée via Terraform. Région : `eu-west-3` (Paris).

---

## Architecture

```
Internet
    │
    ▼
Route 53 (gzouli.sougoumay.com)
    │  Délégation NS depuis OVH
    ▼
CloudFront (HTTPS, certificat ACM us-east-1)
    ├── /* ──────────── S3 gzouli-frontend-prod  (OAC — pas d'accès public direct)
    └── /api/* ─────── ALB gzouli-alb (HTTP/80, forward uniquement)
                            │
                   VPC eu-west-3 (10.0.0.0/16)
                   ┌─────────────────────────────┐
                   │  Subnets publics             │
                   │  ├── pb-subnet-1 (eu-west-3a)│  ALB + NAT Gateway
                   │  └── pb-subnet-2 (eu-west-3b)│  ALB
                   │                             │
                   │  Subnets privés              │
                   │  ├── priv-subnet-1 (3a) ──── ECS Fargate + RDS primaire
                   │  └── priv-subnet-2 (3b) ──── ECS Fargate + RDS standby
                   └─────────────────────────────┘
```

**Chaîne de sécurité :** Internet → ALB (80/443) → ECS (8080) → RDS (5432). Aucun composant n'est exposé directement.

---

## Modules Terraform

| Module | Ressource principale | Description |
|--------|---------------------|-------------|
| `networking` | VPC, subnets, IGW, NAT GW, route tables | Réseau VPC 2 AZ |
| `security-groups` | SG ALB, ECS, RDS | Règles d'accès entre composants |
| `ecr` | ECR repository | Registry Docker pour l'image backend |
| `ecs` | ECS cluster, task definition, service | Exécution du backend en Fargate |
| `rds` | RDS PostgreSQL 16 Multi-AZ | Base de données (subnets privés) |
| `alb` | ALB, listener, target group | Point d'entrée HTTP vers ECS |
| `s3-frontend` | S3 bucket + OAC policy | Hébergement des assets Angular |
| `s3-documents` | S3 bucket | Stockage des documents applicatifs |
| `cloudfront` | Distribution CloudFront | CDN + HTTPS + multi-origin routing |
| `route53` | Hosted zone, A record (alias CF), certificat ACM | DNS + TLS |
| `cognito` | User Pool, App Client | Authentification des utilisateurs |
| `iam` | Rôles ECS execution + task, rôle CI/CD OIDC | Permissions AWS |
| `secrets-manager` | Secret applicatif | Secrets additionnels |
| `cloudwatch` | Log group `/ecs/gzouli/backend` | Logs du backend |
| `iam-local` | Rôle dev local | Workspace `local` uniquement |

---

## Workspaces

```
terraform workspace select prod   # déploiement complet (toute l'infra)
terraform workspace select local  # Cognito + S3 + iam-local uniquement (dev local)
```

Le fichier `main.tf` conditionne les modules avec `local.is_prod` et `local.is_local`.

---

## Prérequis

- Terraform ≥ 1.10
- AWS CLI configuré (ou rôle IAM valide)
- Accès en écriture sur le bucket S3 du backend Terraform (`terraform/backend.tf`)
- Délégation NS OVH → Route 53 déjà configurée

---

## Premier déploiement (prod)

Le déploiement initial nécessite deux `apply` à cause de la dépendance circulaire entre CloudFront (OAC) et la bucket policy S3.

```bash
cd terraform

# 1. Sélectionner le workspace prod
terraform workspace select prod

# 2. Initialiser les providers et modules
terraform init

# 3. Premier apply — crée tout sauf la bucket policy OAC
terraform apply

# 4. Récupérer l'ARN de la distribution CloudFront dans les outputs
terraform output

# 5. Renseigner cloudfront_distribution_arn dans main.tf (module iam)
#    puis second apply pour appliquer la bucket policy
terraform apply
```

> **Note ECR :** L'image backend doit être poussée manuellement (ou via CI) après le premier apply. ECS ne démarre pas sans une image valide dans ECR.

---

## Déploiements suivants

```bash
terraform plan    # Visualiser les changements
terraform apply   # Appliquer
```

Le pipeline CI Terraform (`ci-terraform.yml`) exécute automatiquement `fmt -check` + `plan` sur chaque push/PR sur la branche `infra`. Le job `apply` est commenté et requiert une approbation manuelle via un GitHub Environment `production`.

---

## Configuration

Les variables sont dans `variables.tf`. Les valeurs non secrètes sont dans `terraform.tfvars` (versionné).

Les secrets (credentials AWS, etc.) ne sont jamais dans le dépôt — ils sont injectés via le rôle IAM OIDC en CI ou via `~/.aws` en local.

### Variables principales

| Variable | Description |
|----------|-------------|
| `region` | Région AWS (`eu-west-3`) |
| `az1`, `az2` | Zones de disponibilité |
| `main_cidr` | CIDR du VPC |
| `frontend_origins` | Origins autorisées pour CORS S3 |

---

## Informations prod

| Ressource | Valeur |
|-----------|--------|
| ECR repo | `gzouli-ecr-repository` |
| ECS cluster | `gzouli-ecs-cluster` |
| ECS service | `gzouli-service` |
| S3 frontend | `gzouli-frontend-prod` |
| Domaine | `gzouli.sougoumay.com` |
| Workspace actif | `prod` |

### Stratégie de tags ECR

Le repo ECR est configuré en `IMMUTABLE_WITH_EXCLUSION` — seul le tag `latest*` est mutable. Le CI pousse avec `latest` et `<sha_du_commit>`.

### Variables d'environnement ECS (injectées par Terraform)

| Variable | Source |
|----------|--------|
| `SPRING_PROFILES_ACTIVE=prod` | Terraform |
| `AWS_REGION=eu-west-3` | Terraform |
| `DB_HOST` | Endpoint RDS |
| `DB_PORT=5432` | Terraform |
| `DB_NAME=gzouli_db` | Terraform |
| `AWS_COGNITO_USER_POOL_ID` | Module Cognito |
| `AWS_S3_BUCKET_NAME` | Module S3 documents |
| `DB_USER`, `DB_PASSWORD` | Secrets Manager (secret RDS `rds!...`) |
