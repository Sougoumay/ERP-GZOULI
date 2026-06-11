variable "cpu" {
  type = number
  description = "Le nbre de CPU du container"
}

variable "memory" {
  type = number
  description = "La taille du mémoire RAM"
}

variable "execution_role_arn" {
  type = string
  description = "Le role IAM qu'utilise l'infrastructure pour créer les task"
}

variable "task_role_arn" {
  type = string
  description = "Le role IAM qu'utilise les tâches pour interagir avec les autres services"
}


variable "backend_ecr_image_uri" {
  type = string
  description = "L'URI de l'image backend'"
}

variable "region" {
  type = string
  description = "La region dans laquelle le cluster et les task définition sont lancées"
}

variable "rds_endpoint" {
  type = string
  description = "La taille du mémoire RAM"
}

variable "db_credentials_arn" {
  type = string
  description = "L'arn de la BDD"
}

variable "cognito_arn" {
  type = string
  description = "L'arn de l'instance cognito utilisé pour l'authentification"
}

variable "gzouli_s3_bucket_name" {
  type = string
  description = "Le nom du bu bucket s3 dans lequel l'application dépose les dossiers"
}