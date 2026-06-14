output "bucket_name" {
  description = "Le nom du bucket S3 — à injecter comme AWS_S3_BUCKET_NAME dans ECS et Docker"
  value       = aws_s3_bucket.gzouli_bucket.bucket
}

output "bucket_arn" {
  description = "L'ARN du bucket S3 — à utiliser pour restreindre les policies IAM"
  value       = aws_s3_bucket.gzouli_bucket.arn
}
