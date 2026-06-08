variable "region" {
  type = string
  description = "La region principale dans laquelle les ressources sont créées"
}

################################################################
## Lezs valeurs d'entrées du module networking
################################################################
variable "main_cidr" {
  type = string
  description = "Le CIDR du reseau principal"
}

variable "private_subnet_1_cidr" {
  type = string
  description = "La plage d'adresse IP du subnet privée de az1"
}

variable "az1" {
  type = string
  description = "L'availability zone 1"
}


variable "private_subnet_2_cidr" {
  type = string
  description = "La plage d'adresse IP du subnet privée de az2"
}
variable "az2" {
  type = string
  description = "Zone 2"
}


variable "pb_subnet_1_cidr" {
  type = string
  description = "La plage d'adresse IP du subnet public de az1"
}

variable "pb_subnet_2_cidr" {
  type = string
  description = "La plage d'adresse IP du subnet public de az2"
}