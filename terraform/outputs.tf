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

output "gzouli_secret_manager_arn" {
  value = module.secrets_manager.secret_manager_arn
}