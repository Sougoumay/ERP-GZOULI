output "local_dev_user_name" {
  description = "Nom du user IAM — crée la clé d'accès manuellement dans la console AWS puis ajoute-la dans ~/.aws/credentials sous [gzouli-local]"
  value       = aws_iam_user.gzouli_local_dev.name
}

output "local_dev_user_arn" {
  value = aws_iam_user.gzouli_local_dev.arn
}
