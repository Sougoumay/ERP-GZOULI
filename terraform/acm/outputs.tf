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
