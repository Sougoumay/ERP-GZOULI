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

variable "cognito_user_pool_id" {
  type        = string
  description = "L'ID du User Pool Cognito — injecté comme variable d'environnement pour Spring Security (jwt.issuer-uri)"
}

variable "gzouli_s3_bucket_name" {
  type = string
  description = "Le nom du bu bucket s3 dans lequel l'application dépose les dossiers"
}

variable "private_subnet_1_id" {
  type = string
}

variable "private_subnet_2_id" {
  type = string
}

variable "gzouli_ecs_sg_id" {
  type = string
  description = "L'ID de la SG qui gère l'accès au service"
}

variable "alb_target_group_arn" {
  type = string
  description = "l'ARN de load balancer qui gère le traffic"
}
