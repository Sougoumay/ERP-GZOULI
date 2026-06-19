variable "alb_security_group_id" {
  type        = string
  description = "L'ID du security group de l'ALB"
}

variable "public_subnet_ids" {
  type        = list(string)
  description = "Liste des IDs des subnets publics (min 2 AZ) dans lesquels déployer l'ALB"
}

variable "vpc_id" {
  type        = string
  description = "L'ID du VPC principal"
}

variable "acm_certificate_arn" {
  type        = string
  description = "L'ARN du certificat ACM (eu-west-3) attaché au listener HTTPS de l'ALB"
}
