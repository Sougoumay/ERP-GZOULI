output "bucket_name" {
  description = "Nom du bucket — à injecter comme AWS_S3_BUCKET_NAME dans ECS"
  value       = aws_s3_bucket.gzouli_frontend.bucket
}

output "bucket_arn" {
  description = "ARN du bucket — utilisé dans la bucket policy OAC du module IAM"
  value       = aws_s3_bucket.gzouli_frontend.arn
}

output "bucket_id" {
  description = "ID du bucket — utilisé comme cible de la bucket policy dans le module IAM"
  value       = aws_s3_bucket.gzouli_frontend.id
}

# CloudFront doit utiliser ce domain name (regional) comme origin, PAS l'endpoint website.
# L'endpoint website (*.s3-website.*.amazonaws.com) ne supporte pas OAC.
output "bucket_regional_domain_name" {
  description = "Domain name régional à renseigner dans l'origin CloudFront (ex: gzouli-frontend-prod.s3.eu-west-3.amazonaws.com)"
  value       = aws_s3_bucket.gzouli_frontend.bucket_regional_domain_name
}


