output "alb_certificate_arn" {
  description = "ARN du certificat ACM pour l'ALB (eu-west-3)"
  value       = aws_acm_certificate.alb.arn
}

output "cloudfront_certificate_arn" {
  description = "ARN du certificat ACM pour CloudFront (us-east-1)"
  value       = aws_acm_certificate.cloudfront.arn
}

output "cname_validation_records" {
  description = "Enregistrements CNAME à ajouter manuellement dans OVH"
  value       = aws_acm_certificate.alb.domain_validation_options
}

output "alb_domain_validation_options" {
  description = "Options de validation DNS du certificat ALB — passées au module Route 53"
  value       = aws_acm_certificate.alb.domain_validation_options
}
