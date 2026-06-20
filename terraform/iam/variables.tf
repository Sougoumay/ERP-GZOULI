variable "secret_manager_arn" {
  type        = string
  description = "L'ARN du Secret Manager auquel les tasks ECS peuvent accéder pour récupérer les secrets applicatifs"
}

variable "gzouli_frontend_arn" {
  type = string
  description = "L'ARN du bucket qui héberge le code statique du front"
}

variable "gzouli_frontend_id" {
  type = string
  description = "L'Id du bucket qui héberge le code statique du front"
}

variable "cloudfront_distribution_arn" {
  type        = string
  default     = null
  description = "ARN de la distribution CloudFront — active la bucket policy OAC une fois renseigné"
}

variable "rds_secret_arn" {
  type        = string
  description = "ARN du secret RDS managé (rds!...) — nécessaire pour que ECS puisse lire les credentials DB"
}