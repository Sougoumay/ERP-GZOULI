module "iam" {
  source = "./iam"

  region = var.region
  secret_manager_arn = module.secrets_manager.secret_manager_arn
}

module "secrets_manager" {
  source = "./secrets-manager"
}