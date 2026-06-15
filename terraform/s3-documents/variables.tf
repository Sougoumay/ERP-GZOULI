variable "app_name" {
  type    = string
  default = "gzouli"
}

variable "environment" {
  type        = string
  description = "Le workspace actif — utilisé pour nommer les ressources et éviter les conflits"
}

variable "frontend_origins" {
  type        = list(string)
  description = "Liste des origines autorisées à appeler S3 via URL présignée (ex: Angular local ou domaine prod)"
}
