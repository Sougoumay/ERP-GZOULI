output "gzouli_ecs_execution_role_arn" {
  value =  module.iam.gzouli_ecs_execution_role_arn
}

output "gzouli_ecs_execution_role_name" {
  value =  module.iam.gzouli_ecs_execution_role_name
}

output "gzouli_ecs_task_role_arn" {
  value =  module.iam.gzouli_ecs_task_role_arn
}

output "gzouli_ecs_task_role_name" {
  value =  module.iam.gzouli_ecs_task_role_name
}

# output "gzouli_secret_manager_arn" {
#   value = module.secrets_manager.secret_manager_arn
# }

output "pb_subnet1_arn" {
  value = module.networking.pb_subnet1_arn
}

output "pb_subnet2_arn" {
  value = module.networking.pb_subnet2_arn
}

output "pv_subnet1_arn" {
  value = module.networking.pv_subnet1_arn
}

output "pv_subnet2_arn" {
  value = module.networking.pv_subnet2_arn
}

output "main_vpc_arn" {
  value = module.networking.main_vpc_arn
}

output "main_vpc_id" {
  value = module.networking.main_vpc_id
}

output "gzouli_rds_sg_id" {
  value = module.sg.gzouli_rds_sg_id
}

output "gzouli_ecs_sg_id" {
  value = module.sg.gzouli_ecs_sg_id
}

output "gzouli_alb_sg_id" {
  value = module.sg.gzouli_alb_sg_id
}

output "gzouli_ecr_repo_arn" {
  value = module.ecr.gzouli_ecr_repo_arn
}

output "gzouli_ecr_register_id" {
  value = module.ecr.gzouli_ecr_register_id
}

output "gzouli_ecr_repo_url" {
  value = module.ecr.gzouli_ecr_repo_url
}

output "gzouli_ecs_cluster_arn" {
  value = module.ecs.gzouli_ecs_cluster_arn
}