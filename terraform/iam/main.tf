########################################################################################
### Le role et permissions assumer par l'agent ECS
########################################################################################


# Création du role utilisé par l'agent ECS pour éxecuter les tasks nécessaires
resource "aws_iam_role" "gzouli_ecs_execution_role" {
  name = "gzouli-ecs-execution-role"
  path = "/project/gzouli/"

  assume_role_policy  = jsonencode({
    Version           = "2012-10-17"
    Statement         = [
      {
        Sid           = "AmazonECSTaskExecutionRolePolicy"
        Action        = "sts:AssumeRole"        # Autoriser ECS à assumer ce role
        Effect        = "Allow"
        Principal     = {
          Service     = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })

  tags = {
    Name    = "gzouli_ecs_execution_role"
    Project = "gzouli"
  }
}

# Attachement de la policy managée par AWS (AmazonECSTaskExecutionRolePolicy)
resource "aws_iam_role_policy_attachment" "amazon_ecs_execution_policy_attachment" {
  role       = aws_iam_role.gzouli_ecs_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# Policy personnalisée contenant des permissions spécifiques
resource "aws_iam_policy" "gzouli_ecs_execution_policy" {
  name = "gzouli-ecs-execution-policy"
  path = "/project/gzouli/"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid      = "AllowECSGetSecrets"
        Effect   = "Allow"
        Action   = ["secretsmanager:GetSecretValue"]
        Resource = var.secret_manager_arn
      },

      # Permettre à ECS de s'authentifier auprès de ECR pour récupérer les images docker
      {
        Sid      = "AllowECRAuthToken"
        Effect   = "Allow"
        Action   = ["ecr:GetAuthorizationToken"]
        Resource = "*"
      },

      # Autoriser à ECS de créer le stream et envoyer les logs vers cloudwatch
      {
        Sid    = "AllowECSCloudWatchLogs"
        Effect = "Allow"
        Action = [
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ]
        Resource = "*" # À restreindre avec l'ARN du log group // TODO
      }
    ]
  })

  tags = {
    Name    = "gzouli_ecs_execution_policy"
    Project = "gzouli"
  }
}

# Attachement de la policy personnalisée (pour Secrets Manager et ECR au role d'éxécution des tasks)
resource "aws_iam_role_policy_attachment" "gzouli_custom_policy_attachment" {
  role       = aws_iam_role.gzouli_ecs_execution_role.name
  policy_arn = aws_iam_policy.gzouli_ecs_execution_policy.arn
}


########################################################################################
### Le role et permissions assumer par les tasks ECS : utilisé par l'App Spring Boot
########################################################################################
resource "aws_iam_role" "gzouli_ecs_task_role" {
  name = "gzouli-ecs-task-role"
  path = "/project/gzouli/"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid = "GzouliECSTaskRoleAssumption"
        Effect = "Allow"
        Action = "sts:AssumeRole"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_policy" "gzouli_ecs_task_policy" {
  name = "gzouli-ecs-task-policy"
  path = "/project/gzouli/"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [

      # Permettre à l'application de lire, ajouter et supprimer des documents dans le bucket S3
      {
        Sid      = "AllowECSActWithAppBucket"
        Effect   = "Allow"
        Action   = ["s3:PutObject", "s3:GetObject", "s3:DeleteObject"]
        Resource = "*" # À restreindre avec l'ARN du bucket // TODO
      },

      # Permettre à l'application d'interagir avec cognito pour la gestion des utilisateurs
      {
        Sid    = "AllowECSActWithCognito"
        Effect = "Allow"
        Action = [
          "cognito-idp:AdminCreateUser",       # createCognitoUser()
          "cognito-idp:AdminAddUserToGroup",   # createCognitoUser() — ajout au groupe ROLE
          "cognito-idp:AdminDisableUser",      # disableUser()
          "cognito-idp:AdminEnableUser"        # enableUser()
        ]
        Resource = "*" # À restreindre avec l'ARN de User Pool Cognito // TODO
      },

      # Autoriser l'application Spring Boot à envoyer des logs customisés vers cloudwatch
      {
        Sid    = "AllowECSCloudWatchLogs"
        Effect = "Allow"
        Action = [
          "logs:PutLogEvents"
        ]
        Resource = "*" # À restreindre avec l'ARN du log group // TODO
      },

      # Autoriser l'application à récupérer des clés sensibles dans secrets manager à l'exécution
      {
        Sid      = "AllowECSGetSecrets"
        Effect   = "Allow"
        Action   = ["secretsmanager:GetSecretValue"]
        Resource = var.secret_manager_arn
      }
    ]
  })

  tags = {
    Name    = "gzouli_ecs_task_policy"
    Project = "gzouli"
  }
}

resource "aws_iam_role_policy_attachment" "gzouli_ecs_task_custom_policy_attachment" {
  role       = aws_iam_role.gzouli_ecs_task_role.name
  policy_arn = aws_iam_policy.gzouli_ecs_task_policy.arn
}