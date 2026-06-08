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