output "gzouli_ecr_repo_arn" {
  value = aws_ecr_repository.gzouli_ecr_repository.arn
}

output "gzouli_ecr_register_id" {
  value = aws_ecr_repository.gzouli_ecr_repository.registry_id
}

output "gzouli_ecr_repo_url" {
  value = aws_ecr_repository.gzouli_ecr_repository.repository_url
}