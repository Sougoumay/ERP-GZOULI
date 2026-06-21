# Analyse de l'infrastructure Terraform — ERP GZOULI

---

## Ce qui est bon

### RDS (`rds/main.tf`)

| Attribut | Statut | Pourquoi |
|---|---|---|
| `multi_az = true` | ✅ | Réplique synchrone dans la 2e AZ, failover automatique |
| `publicly_accessible = false` | ✅ | La DB n'est accessible que depuis le VPC |
| `backup_retention_period = 7` | ✅ | 7 jours de snapshots automatiques, permet le point-in-time recovery |
| `db_subnet_group` sur 2 AZ | ✅ | Indispensable pour que `multi_az` fonctionne |
| `engine_version = "16"` | ✅ | Correspond à `postgres:16` dans docker-compose |
| `vpc_security_group_ids` câblé | ✅ | SG correctement associé |

### Networking (`networking/main.tf`)

- Architecture solide : 2 subnets publics + 2 subnets privés répartis sur 2 AZ
- NAT Gateway présent : les containers dans les subnets privés peuvent appeler ECR, Secrets Manager, etc.
- Route tables correctement câblées (publique → IGW, privée → NAT)

### Security Groups (`security-groups/main.tf`)

Chaîne de sécurité bien pensée, rien n'est exposé directement :

```
Internet → ALB (80/443) → ECS (8080) → RDS (5432)
```

### IAM (`iam/main.tf`)

- Séparation `execution_role` (infrastructure) / `task_role` (application) — c'est la bonne pratique AWS
- `secretsmanager:GetSecretValue` déjà présent sur les deux rôles — la fondation pour injecter les secrets est prête

---

## Ce qui manque ou est problématique

### 1. RDS — mot de passe hardcodé (critique)

```hcl
# ❌ Ne pas faire ça
password = "ChangeMe123!"
```

La bonne approche : laisser AWS gérer le mot de passe avec `manage_master_user_password = true`.
AWS crée automatiquement un secret dans Secrets Manager et peut le faire tourner.

```hcl
resource "aws_db_instance" "gzouli_rds" {
  # Supprimer password = ...
  manage_master_user_password = true   # AWS crée et stocke le secret
  # ...
}
```

L'ARN du secret est ensuite disponible via :
```hcl
aws_db_instance.gzouli_rds.master_user_secret[0].secret_arn
```

### 2. RDS — autres corrections à apporter

```hcl
# Actuel → Corrigé

max_allocated_storage   = 20     # ❌ → 100  (sinon l'autoscaling du stockage est bloqué)
skip_final_snapshot     = true   # ❌ → false (true = perte de données si destroy accidentel)
deletion_protection     = false  # ⚠️  → true en production

# Attributs manquants à ajouter
final_snapshot_identifier = "gzouli-final-snapshot"
storage_encrypted         = true   # Chiffrement au repos — bonne pratique
storage_type              = "gp3"  # Meilleur que gp2 (par défaut), même prix
allow_major_version_upgrade = false  # Risqué en production
```

> **À propos de `identifier`** (ton TODO) : c'est simplement le nom affiché dans la console AWS et utilisé dans l'endpoint DNS de la DB. Sans lui AWS génère un nom aléatoire.

### 3. RDS — module non câblé dans `main.tf`

Le module `rds/` n'existe pas dans `main.tf` et n'a pas de `variables.tf`. À ajouter :

```hcl
# terraform/main.tf
module "rds" {
  source              = "./rds"
  private_subnet_1_id = module.networking.private_subnet_1_id
  private_subnet_2_id = module.networking.private_subnet_2_id
  gzouli_rds_sg_id    = module.sg.gzouli_rds_sg_id
}
```

```hcl
# terraform/rds/variables.tf
variable "private_subnet_1_id" { type = string }
variable "private_subnet_2_id" { type = string }
variable "gzouli_rds_sg_id"    { type = string }
```

### 4. Secrets Manager — le secret est vide

Tu crées le `aws_secretsmanager_secret` mais sans contenu ni version.
Avec `manage_master_user_password = true` sur RDS, AWS peuple le secret automatiquement — tu n'as plus besoin de ton secret `gzouli_prod_db_credentials_2` pour les credentials DB. Il faut juste passer l'ARN généré par RDS à ECS.

```hcl
# terraform/secrets-manager/outputs.tf — ou directement depuis le module rds
output "rds_secret_arn" {
  value = module.rds.master_user_secret_arn
}
```

### 5. ECS — service vide et secrets commentés

**a) Décommenter et compléter `environment` + `secrets` dans la task definition :**

```hcl
environment = [
  { name = "SPRING_PROFILES_ACTIVE", value = "prod" },
  { name = "AWS_REGION",             value = var.region },
  { name = "DB_HOST",                value = var.rds_endpoint },
  { name = "DB_PORT",                value = "5432" },
  { name = "DB_NAME",                value = "gzouli_db" }
]

secrets = [
  # Format : ARN_du_secret:clé_json::
  { name = "DB_USER",     valueFrom = "${var.db_credentials_arn}:username::" },
  { name = "DB_PASSWORD", valueFrom = "${var.db_credentials_arn}:password::" }
]
```

ECS appelle Secrets Manager **au démarrage du container** et injecte les valeurs comme variables d'environnement. C'est l'`execution_role` qui fait cet appel (pas le container lui-même) — il a déjà la permission `secretsmanager:GetSecretValue`.

**b) Compléter `aws_ecs_service` :**

```hcl
resource "aws_ecs_service" "gzouli_ecs_service" {
  name            = "gzouli-service"
  cluster         = aws_ecs_cluster.gzouli_ecs_cluster.id
  task_definition = aws_ecs_task_definition.gzouli_ecs_task_definition.arn
  desired_count   = 2
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = [var.private_subnet_1_id, var.private_subnet_2_id]
    security_groups  = [var.gzouli_ecs_sg_id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = var.alb_target_group_arn
    container_name   = "gzouli-backend"
    container_port   = 8080
  }
}
```

### 6. Modules entièrement manquants

| Module manquant | Pourquoi c'est bloquant |
|---|---|
| **ALB** (`aws_lb`, `aws_lb_listener`, `aws_lb_target_group`) | ECS ne peut pas recevoir de trafic externe sans ALB |
| **CloudWatch Log Group** | ECS plante au démarrage si `/ecs/gzouli/backend` n'existe pas |
| **S3 Bucket** | Référencé dans IAM task policy, variable vide dans `main.tf` |
| **Cognito** | Référencé dans IAM task policy, variable vide dans `main.tf` |

```hcl
# À ajouter dans ecs/main.tf ou un module logs/ dédié
resource "aws_cloudwatch_log_group" "gzouli_backend_logs" {
  name              = "/ecs/gzouli/backend"
  retention_in_days = 30
}
```

---

## Comment fonctionnent les AZ

```
Region eu-west-3 (Paris)
├── AZ: eu-west-3a
│   ├── private_subnet_1  →  RDS instance primaire
│   └── pb_subnet_1       →  NAT Gateway + nœud ALB
└── AZ: eu-west-3b
    ├── private_subnet_2  →  RDS standby (réplique synchrone)
    └── pb_subnet_2       →  nœud ALB
```

Avec `multi_az = true`, AWS maintient une **réplique synchrone** dans la 2e AZ.
En cas de panne de l'AZ primaire :
- Le failover est **automatique** en ~60-120 secondes
- L'endpoint DNS de la DB **ne change pas** — ton application se reconnecte sans modification de config
- Le `db_subnet_group` qui couvre les 2 AZ est exactement ce qui rend ça possible

---

## Flux complet : rotation de mot de passe

```
terraform apply
  │
  ▼
aws_db_instance avec manage_master_user_password = true
  │
  ▼
AWS génère un mot de passe fort et crée automatiquement un secret Secrets Manager :
  { "username": "admin", "password": "<généré>", "host": "...", "port": 5432 }
  │
  ▼
Tu passes l'ARN du secret à ECS via var.db_credentials_arn
  │
  ▼
ECS task démarre → execution_role appelle secretsmanager:GetSecretValue
  │
  ▼
DB_USER et DB_PASSWORD injectés dans le container comme variables d'environnement
  │
  ▼
Spring Boot lit DB_HOST, DB_PORT, DB_USER, DB_PASSWORD → connexion établie
```

### Rotation automatique (optionnel)

AWS fournit une Lambda native pour RDS :

```hcl
resource "aws_secretsmanager_secret_rotation" "rds_rotation" {
  secret_id           = module.rds.master_user_secret_arn
  rotation_lambda_arn = "arn:aws:lambda:${var.region}:${data.aws_caller_identity.current.account_id}:function:SecretsManagerRDSPostgreSQLRotationSingleUser"

  rotation_rules {
    automatically_after_days = 30
  }
}
```

Cette Lambda change le mot de passe dans RDS et met à jour le secret en même temps. ECS récupère le nouveau mot de passe au prochain redémarrage du container.

---

## Récapitulatif — priorité pour débloquer le déploiement

| Priorité | Action |
|---|---|
| 1 | Créer `rds/variables.tf` et câbler le module dans `main.tf` |
| 2 | Passer `manage_master_user_password = true` sur RDS, supprimer le mot de passe hardcodé |
| 3 | Créer le module ALB — sans lui le service ECS n'est pas routable |
| 4 | Créer le `aws_cloudwatch_log_group` — sinon ECS plante au démarrage |
| 5 | Décommenter et câbler `environment` + `secrets` dans la task definition |
| 6 | Compléter `aws_ecs_service` avec `network_configuration` et `load_balancer` |
| 7 | Créer S3 bucket et Cognito user pool (ou mettre les ARN en dur temporairement) |
