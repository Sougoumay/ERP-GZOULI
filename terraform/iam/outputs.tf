output "gzouli_ecs_execution_role_arn" {
  value =  aws_iam_role.gzouli_ecs_execution_role.arn
}

output "gzouli_ecs_execution_role_name" {
  value =  aws_iam_role.gzouli_ecs_execution_role.name
}

output "gzouli_ecs_task_role_arn" {
  value =  aws_iam_role.gzouli_ecs_task_role.arn
}

output "gzouli_ecs_task_role_name" {
  value =  aws_iam_role.gzouli_ecs_task_role.name
}