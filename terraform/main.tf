module "iam" {
  source = "./iam"

  region = var.region
  secret_manager_arn = module.secrets_manager.secret_manager_arn
}

module "secrets_manager" {
  source = "./secrets-manager"
}

module "networking" {
  source                = "./networking"
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
  source = "./security-groups"
  main_vpc_id = module.networking.main_vpc_id
}

module "ecr" {
  source                = "./ecr"
}

module "ecs" {
  source             = "./ecs"
  region             = var.region
  cpu                = 512
  memory             = 1024
  execution_role_arn = module.iam.gzouli_ecs_execution_role_arn
  task_role_arn      = module.iam.gzouli_ecs_task_role_arn
  backend_ecr_image_uri = ""


  // TODO : resource à créer
  cognito_arn           = ""
  rds_endpoint          = module.rds.rds_endpoint
  db_credentials_arn    = module.rds.rds_secret_arn
  gzouli_s3_bucket_name = ""
  alb_target_group_arn  = ""
  gzouli_ecs_sg_id      = module.sg.gzouli_ecs_sg_id
  private_subnet_1_id   = module.networking.private_subnet_1_id
  private_subnet_2_id   = module.networking.private_subnet_2_id
}

module "rds" {
  source             = "./rds"
  private_subnet_1_id = module.networking.private_subnet_1_id
  private_subnet_2_id = module.networking.private_subnet_2_id
  gzouli_rds_sg_id   = module.sg.gzouli_rds_sg_id
}