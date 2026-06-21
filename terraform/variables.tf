variable "region" {
  type        = string
  description = "La region principale dans laquelle les ressources sont créées"
}

################################################################
## Lezs valeurs d'entrées du module networking
################################################################
variable "main_cidr" {
  type        = string
  description = "Le CIDR du reseau principal"
}

variable "private_subnet_1_cidr" {
  type        = string
  description = "La plage d'adresse IP du subnet privée de az1"
}

variable "az1" {
  type        = string
  description = "L'availability zone 1"
}


variable "private_subnet_2_cidr" {
  type        = string
  description = "La plage d'adresse IP du subnet privée de az2"
}
variable "az2" {
  type        = string
  description = "Zone 2"
}


variable "pb_subnet_1_cidr" {
  type        = string
  description = "La plage d'adresse IP du subnet public de az1"
}

variable "pb_subnet_2_cidr" {
  type        = string
  description = "La plage d'adresse IP du subnet public de az2"
}

variable "domain_name" {
  type        = string
  description = "Le sous-domaine de l'application (ex: gzouli.sougoumay.com)"
}

variable "frontend_origins" {
  type        = list(string)
  description = "Origines Angular autorisées à accéder au bucket S3 via URL présignée"
}

variable "image_tag" {
  type        = string
  description = "Le tag de l'image backend dans ECR (ex: latest, ou SHA du commit en CI/CD)"
  default     = "latest"
}