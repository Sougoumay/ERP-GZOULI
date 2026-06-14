variable "app_name" {
  type    = string
  default = "gzouli"
}

variable "environment" {
  type        = string
  description = "Le workspace actif — utilisé pour nommer les ressources et éviter les conflits"
}
