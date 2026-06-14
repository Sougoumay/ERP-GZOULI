################################################################
## IAM User dédié au développement local
## Périmètre identique au Task Role ECS mais sous forme
## d'un utilisateur avec clés d'accès (pas un rôle)
##
## Les clés d'accès ne sont PAS gérées ici pour éviter
## de les stocker dans le tfstate.
## → Crée-les manuellement dans la console AWS après apply,
##   puis ajoute-les dans ~/.aws/credentials sous [gzouli-local]
################################################################
resource "aws_iam_user" "gzouli_local_dev" {
  name = "gzouli-local-dev"
  path = "/project/gzouli/"

  tags = {
    Name    = "gzouli-local-dev"
    Project = "gzouli"
    Usage   = "local-development"
  }
}

resource "aws_iam_policy" "gzouli_local_dev_policy" {
  name        = "gzouli-local-dev-policy"
  path        = "/project/gzouli/"
  description = "Permissions applicatives pour le développement local (miroir du Task Role ECS)"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "AllowCognito"
        Effect = "Allow"
        Action = [
          "cognito-idp:AdminCreateUser",
          "cognito-idp:AdminAddUserToGroup",
          "cognito-idp:AdminDisableUser",
          "cognito-idp:AdminEnableUser"
        ]
        Resource = "*"
      },
      {
        Sid      = "AllowS3"
        Effect   = "Allow"
        Action   = ["s3:PutObject", "s3:GetObject", "s3:DeleteObject"]
        Resource = "*"
      }
    ]
  })

  tags = {
    Name    = "gzouli-local-dev-policy"
    Project = "gzouli"
  }
}

resource "aws_iam_user_policy_attachment" "gzouli_local_dev_attachment" {
  user       = aws_iam_user.gzouli_local_dev.name
  policy_arn = aws_iam_policy.gzouli_local_dev_policy.arn
}
