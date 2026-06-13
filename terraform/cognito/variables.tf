variable "app_name" {
  type        = string
  description = "Le nom de l'application, utilisé pour nommer les ressources Cognito"
  default     = "gzouli"
}

variable "region" {
  type        = string
  description = "La région AWS dans laquelle Cognito est déployé"
}

variable "access_token_validity_hours" {
  type        = number
  description = "Durée de validité de l'access token en heures"
  default     = 1
}

variable "refresh_token_validity_days" {
  type        = number
  description = "Durée de validité du refresh token en jours"
  default     = 30
}
