variable "private_subnet_1_id" {
  type = string
  description = "L'DI du sous reseau 2 à utiliser ppur HA (AZ)"
}

variable "private_subnet_2_id" {
  type = string
  description = "L'DI du sous reseau 2 à utiliser ppur HA (AZ)"
}

variable "gzouli_rds_sg_id" {
  type = string
  description = "L'ID de la SG qui gères les accès à la BDD"
}
