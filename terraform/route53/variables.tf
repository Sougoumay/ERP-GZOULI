variable "domain_name" {
  type        = string
  description = "Le sous-domaine géré par cette zone Route 53 (ex: gzouli.sougoumay.com)"
}

variable "cloudfront_certificate_arn" {
  type        = string
  description = "ARN du certificat ACM CloudFront (us-east-1)"
}

variable "alb_certificate_arn" {
  type        = string
  description = "ARN du certificat ACM ALB (eu-west-3)"
}

variable "acm_validation_options" {
  type = set(object({
    domain_name           = string
    resource_record_name  = string
    resource_record_type  = string
    resource_record_value = string
  }))
  description = "Options de validation DNS des certificats ACM"
}
