output "rds_secret_arn" {
  value = aws_db_instance.gzouli_rds.master_user_secret[0].secret_arn
}

output "rds_endpoint" {
  value = aws_db_instance.gzouli_rds.endpoint
}

