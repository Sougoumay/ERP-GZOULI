variable "s3_bucket_regional_domain_name" {
  type        = string
  description = "Domain name régional du bucket S3 frontend (ex: gzouli-frontend-prod.s3.eu-west-3.amazonaws.com)"
}

variable "acm_certificate_arn" {
  type        = string
  description = "ARN du certificat ACM us-east-1 pour CloudFront"
}

variable "domain_name" {
  type        = string
  description = "Le sous-domaine servi par CloudFront (ex: gzouli.sougoumay.com)"
}

variable "environment" {
  type        = string
  description = "Workspace actif — utilisé pour nommer les ressources"
}

variable "alb_dns_name" {
  type        = string
  description = "DNS name de l'ALB — utilisé comme origine CloudFront pour /api/*"
}

variable "route53_zone_id" {
  type        = string
  description = "ID de la zone Route 53 — pour créer l'alias A record vers cette distribution"
}
