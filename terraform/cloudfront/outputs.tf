output "distribution_arn" {
  description = "ARN de la distribution — à passer au module iam pour activer la bucket policy OAC"
  value       = aws_cloudfront_distribution.gzouli_frontend.arn
}

output "distribution_domain_name" {
  description = "Domain name CloudFront à configurer comme CNAME dans OVH"
  value       = aws_cloudfront_distribution.gzouli_frontend.domain_name
}

output "distribution_id" {
  description = "ID de la distribution — à utiliser pour les invalidations de cache en CI/CD"
  value       = aws_cloudfront_distribution.gzouli_frontend.id
}
