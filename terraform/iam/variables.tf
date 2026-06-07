variable "region" {
  type = string
  description = "La region principale dans laquelle les ressources sont créées"
}

variable "secret_manager_arn" {
  type = string
  description = "L'Id du secret manager à laquelle peut accéder ecs-task pour récupérer les clés sensibles"
}