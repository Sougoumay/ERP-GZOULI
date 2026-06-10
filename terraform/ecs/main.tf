resource "aws_ecs_cluster" "gzouli_ecs_cluster" {
  name = "gzouli-ecs-cluster"

  configuration {
    execute_command_configuration {
      logging = "DEFAULT"
    }
  }

  setting {
    name  = "containerInsights"
    value = "enhanced"
  }

  tags = {
    Name = "gzouli_ecs_cluster"
    Project = "gzouli"
  }
}

// TODO
resource "aws_ecs_task_definition" "gzouli_ecs_task_definition" {
  family                = "service"
  requires_compatibilities = ["FARGATE"]
  network_mode = "awsvpc"
  cpu          = var.cpu
  memory       = var.memory

  execution_role_arn = var.execution_role_arn
  task_role_arn = var.task_role_arn

  runtime_platform {
    operating_system_family = "LINUX"
    cpu_architecture        = "X86_64"
  }

  container_definitions = jsonencode([
    {
      name   = "gzouli_ecs_service"
      image  = ""
      cpu    = 1024
      memory = 2048

    }
  ])
}
