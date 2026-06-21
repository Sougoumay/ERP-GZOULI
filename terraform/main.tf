################################################################
## Workspace → Environnement
##
## terraform workspace select local  →  cognito + s3 + iam-local
## terraform workspace select prod   →  tout
################################################################
locals {
  is_prod  = terraform.workspace == "prod"
  is_local = terraform.workspace == "local"
}

################################################################
## Ressources communes — créées dans TOUS les workspaces
################################################################
module "cognito" {
  source      = "./cognito"
  app_name    = "gzouli"
  region      = var.region
  environment = terraform.workspace
}

module "s3" {
  source           = "./s3-documents"
  app_name         = "gzouli"
  environment      = terraform.workspace
  frontend_origins = var.frontend_origins
}

################################################################
## Ressources prod uniquement
##
## count = 0 → le module n'est pas créé du tout
## try(one(module.xxx).output, "") → renvoie "" si le module
##   n'existe pas, évite les erreurs de référence croisée
################################################################
module "secrets_manager" {
  count  = local.is_prod ? 1 : 0
  source = "./secrets-manager"
}

module "iam" {
  count  = local.is_prod ? 1 : 0
  source = "./iam"

  secret_manager_arn = try(one(module.secrets_manager).secret_manager_arn, "")
  rds_secret_arn     = try(one(module.rds).rds_secret_arn, "")

  cloudfront_distribution_arn = try(one(module.cloudfront).distribution_arn, null)

  gzouli_frontend_arn = one(module.s3_frontend).bucket_arn
  gzouli_frontend_id  = one(module.s3_frontend).bucket_id
}

module "networking" {
  count  = local.is_prod ? 1 : 0
  source = "./networking"

  az1                   = var.az1
  az2                   = var.az2
  main_cidr             = var.main_cidr
  pb_subnet_1_cidr      = var.pb_subnet_1_cidr
  pb_subnet_2_cidr      = var.pb_subnet_2_cidr
  private_subnet_1_cidr = var.private_subnet_1_cidr
  private_subnet_2_cidr = var.private_subnet_2_cidr
  region                = var.region
}

module "sg" {
  count  = local.is_prod ? 1 : 0
  source = "./security-groups"

  main_vpc_id = try(one(module.networking).main_vpc_id, "")
}

module "ecr" {
  count  = local.is_prod ? 1 : 0
  source = "./ecr"
}

module "cloudwatch" {
  count  = local.is_prod ? 1 : 0
  source = "./cloudwatch"
}

module "rds" {
  count  = local.is_prod ? 1 : 0
  source = "./rds"

  private_subnet_1_id = try(one(module.networking).private_subnet_1_id, "")
  private_subnet_2_id = try(one(module.networking).private_subnet_2_id, "")
  gzouli_rds_sg_id    = try(one(module.sg).gzouli_rds_sg_id, "")
}

module "alb" {
  count  = local.is_prod ? 1 : 0
  source = "./alb"

  alb_security_group_id = try(one(module.sg).gzouli_alb_sg_id, "")
  public_subnet_ids = [
    try(one(module.networking).pb_subnet_1_id, ""),
    try(one(module.networking).pb_subnet_2_id, ""),
  ]
  vpc_id              = try(one(module.networking).main_vpc_id, "")
  acm_certificate_arn = try(one(module.route53).alb_validated_certificate_arn, "")
}

module "ecs" {
  count  = local.is_prod ? 1 : 0
  source = "./ecs"

  region                = var.region
  cpu                   = 512
  memory                = 1024
  execution_role_arn    = try(one(module.iam).gzouli_ecs_execution_role_arn, "")
  task_role_arn         = try(one(module.iam).gzouli_ecs_task_role_arn, "")
  backend_ecr_image_uri = "${try(one(module.ecr).gzouli_ecr_repo_url, "")}:${var.image_tag}"
  cognito_user_pool_id  = module.cognito.user_pool_id
  rds_endpoint          = try(one(module.rds).rds_endpoint, "")
  db_credentials_arn    = try(one(module.rds).rds_secret_arn, "")
  gzouli_s3_bucket_name = module.s3.bucket_name
  alb_target_group_arn  = try(one(module.alb).target_group_arn, "")
  gzouli_ecs_sg_id      = try(one(module.sg).gzouli_ecs_sg_id, "")
  private_subnet_1_id   = try(one(module.networking).private_subnet_1_id, "")
  private_subnet_2_id   = try(one(module.networking).private_subnet_2_id, "")
}

module "s3_frontend" {
  count       = local.is_prod ? 1 : 0
  source      = "./s3-frontend"
  app_name    = "gzouli"
  environment = terraform.workspace
}

module "cloudfront" {
  count  = local.is_prod ? 1 : 0
  source = "./cloudfront"

  s3_bucket_regional_domain_name = try(one(module.s3_frontend).bucket_regional_domain_name, "")
  acm_certificate_arn            = try(one(module.route53).cloudfront_validated_certificate_arn, "")
  alb_dns_name                   = try(one(module.alb).alb_dns_name, "")
  domain_name                    = var.domain_name
  environment                    = terraform.workspace
  route53_zone_id                = try(one(module.route53).hosted_zone_id, "")
}

module "route53" {
  count  = local.is_prod ? 1 : 0
  source = "./route53"

  providers = {
    aws           = aws
    aws.us_east_1 = aws.us_east_1
  }

  domain_name = var.domain_name
}

################################################################
## Ressources local uniquement
################################################################
module "iam_local" {
  count  = local.is_local ? 1 : 0
  source = "./iam-local"
}
