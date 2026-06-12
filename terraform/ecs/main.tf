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
      name      = "gzouli-backend"
      image     = var.backend_ecr_image_uri
      cpu          = var.cpu
      memory       = var.memory
      essential = true

      portMappings = [
        {
          containerPort = 8080
          hostPort      = 80
          protocol      = "tcp"
        }
      ]

      environment = [
        { name = "SPRING_PROFILES_ACTIVE", value = "prod" },
        { name = "AWS_REGION",             value = var.region },
        { name = "DB_HOST",                value = var.rds_endpoint },
        { name = "DB_PORT",                value = "5432" },
        { name = "DB_NAME",                value = "gzouli" }
      ]

      secrets = [ // TODO : cognito à créer au préalable
        { name = "DB_USER",     valueFrom = "${var.db_credentials_arn}:username::" },
        { name = "DB_PASSWORD", valueFrom = "${var.db_credentials_arn}:password::" },
        # { name = "AWS_COGNITO_USER_POOL_ID",  valueFrom = "${var.cognito_arn}:user_pool_id::" },
        # { name = "AWS_COGNITO_APP_CLIENT_ID", valueFrom = "${var.cognito_arn}:client_id::" }
      ]

      healthCheck = {
        command     = ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
        interval    = 10
        retries     = 5
        startPeriod = 20
        timeout     = 5
      }

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = "/ecs/gzouli/backend"  // TODO : cloud watch avec le log group à créer
          "awslogs-region"        = var.region
          "awslogs-stream-prefix" = "backend"
        }
      }
    }
    ])
}

resource "aws_ecs_service" "gzouli_ecs_service" {
  name            = "gzouli-service"
  cluster         = aws_ecs_cluster.gzouli_ecs_cluster.id
  task_definition = aws_ecs_task_definition.gzouli_ecs_task_definition.arn
  desired_count   = 2   # 2 tasks pour la HA
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = [var.private_subnet_1_id, var.private_subnet_2_id]
    security_groups  = [var.gzouli_ecs_sg_id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = var.alb_target_group_arn
    container_name   = "gzouli-backend"
    container_port   = 8080
  }
}