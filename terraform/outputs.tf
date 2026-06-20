################################################################
## Outputs communs (local + prod)
################################################################
output "cognito_user_pool_id" {
  value = module.cognito.user_pool_id
}

output "cognito_user_pool_client_id" {
  value = module.cognito.user_pool_client_id
}

output "cognito_issuer_uri" {
  value = module.cognito.issuer_uri
}

output "s3_bucket_name" {
  value = module.s3.bucket_name
}

################################################################
## Outputs prod uniquement
## try(..., null) → renvoie null si le module n'existe pas (workspace local)
################################################################
output "gzouli_ecs_execution_role_arn" {
  value = try(one(module.iam).gzouli_ecs_execution_role_arn, null)
}

output "gzouli_ecs_task_role_arn" {
  value = try(one(module.iam).gzouli_ecs_task_role_arn, null)
}

output "gzouli_secret_manager_arn" {
  value = try(one(module.secrets_manager).secret_manager_arn, null)
}

output "main_vpc_id" {
  value = try(one(module.networking).main_vpc_id, null)
}

output "pb_subnet1_arn" {
  value = try(one(module.networking).pb_subnet1_arn, null)
}

output "pb_subnet2_arn" {
  value = try(one(module.networking).pb_subnet2_arn, null)
}

output "pv_subnet1_arn" {
  value = try(one(module.networking).pv_subnet1_arn, null)
}

output "pv_subnet2_arn" {
  value = try(one(module.networking).pv_subnet2_arn, null)
}

output "main_vpc_arn" {
  value = try(one(module.networking).main_vpc_arn, null)
}

output "gzouli_rds_sg_id" {
  value = try(one(module.sg).gzouli_rds_sg_id, null)
}

output "gzouli_ecs_sg_id" {
  value = try(one(module.sg).gzouli_ecs_sg_id, null)
}

output "gzouli_alb_sg_id" {
  value = try(one(module.sg).gzouli_alb_sg_id, null)
}

output "gzouli_ecr_repo_url" {
  value = try(one(module.ecr).gzouli_ecr_repo_url, null)
}

output "gzouli_ecs_cluster_arn" {
  value = try(one(module.ecs).gzouli_ecs_cluster_arn, null)
}

output "alb_certificate_arn" {
  description = "ARN du certificat ACM pour l'ALB (eu-west-3)"
  value       = try(one(module.acm).alb_certificate_arn, null)
}

output "cloudfront_certificate_arn" {
  description = "ARN du certificat ACM pour CloudFront (us-east-1)"
  value       = try(one(module.acm).cloudfront_certificate_arn, null)
}

# À renseigner comme origin_domain_name dans la distribution CloudFront
output "frontend_bucket_regional_domain_name" {
  description = "Domain name S3 régional à utiliser comme origin CloudFront (OAC)"
  value       = try(one(module.s3_frontend).bucket_regional_domain_name, null)
}

output "frontend_bucket_arn" {
  description = "ARN du bucket frontend — nécessaire pour la bucket policy OAC"
  value       = try(one(module.s3_frontend).bucket_arn, null)
}

output "cname_validation_records" {
  description = "Enregistrements CNAME à ajouter manuellement dans OVH"
  value       = try(one(module.acm).cname_validation_records, null)
}

output "cloudfront_distribution_domain_name" {
  description = "Domain name CloudFront à configurer comme CNAME dans OVH (pointe vers gzouli.sougoumay.com)"
  value       = try(one(module.cloudfront).distribution_domain_name, null)
}

output "cloudfront_distribution_id" {
  description = "ID de la distribution — à injecter dans le CI/CD pour les invalidations de cache"
  value       = try(one(module.cloudfront).distribution_id, null)
}

output "route53_name_servers" {
  description = "Les 4 NS records à ajouter dans OVH pour déléguer gzouli.sougoumay.com vers Route 53"
  value       = try(one(module.route53).name_servers, null)
}


################################################################
## Outputs local uniquement
################################################################
output "local_dev_user_name" {
  description = "Nom du user IAM local — crée ses clés d'accès dans la console AWS"
  value       = try(one(module.iam_local).local_dev_user_name, null)
}



