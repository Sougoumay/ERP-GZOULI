################################################################
## User Pool
################################################################
resource "aws_cognito_user_pool" "gzouli_user_pool" {
  # local → gzouli-user-pool-local / prod → gzouli-user-pool-prod
  name = "${var.app_name}-user-pool-${var.environment}"

  # Les utilisateurs se connectent avec leur email (pas un username arbitraire)
  username_attributes      = ["email"]
  auto_verified_attributes = ["email"]

  # L'inscription libre est désactivée : seul l'admin crée les comptes (ERP)
  admin_create_user_config {
    allow_admin_create_user_only = true

    invite_message_template {
      email_subject = "Votre accès à l'ERP Gzouli"
      email_message = "Bonjour,\n\nVotre compte a été créé. Voici vos identifiants temporaires :\nEmail : {username}\nMot de passe : {####}\n\nVeuillez vous connecter et modifier votre mot de passe.\n\nCordialement,\nL'équipe Gzouli"
      sms_message   = "Votre identifiant : {username} — Mot de passe temporaire : {####}"
    }
  }

  # Politique de mot de passe stricte (ERP avec données sensibles)
  password_policy {
    minimum_length                   = 12
    require_lowercase                = true
    require_uppercase                = true
    require_numbers                  = true
    require_symbols                  = true
    temporary_password_validity_days = 7
  }

  # MFA optionnel (TOTP via application d'authentification)
  mfa_configuration = "OPTIONAL"

  software_token_mfa_configuration {
    enabled = true
  }

  # Récupération de compte uniquement par email
  account_recovery_setting {
    recovery_mechanism {
      name     = "verified_email"
      priority = 1
    }
  }

  # Configuration email (SES en prod, Cognito par défaut pour le dev)
  email_configuration {
    email_sending_account = "COGNITO_DEFAULT"
  }

  email_verification_subject = "Vérification de votre compte Gzouli"
  email_verification_message = "Votre code de vérification Gzouli est : {####}"

  user_pool_add_ons {
    advanced_security_mode = "ENFORCED"
  }

  tags = {
    Name    = "${var.app_name}-user-pool"
    Project = var.app_name
  }
}

################################################################
## App Client — client public pour le frontend Angular
################################################################
resource "aws_cognito_user_pool_client" "gzouli_app_client" {
  name         = "${var.app_name}-frontend-client"
  user_pool_id = aws_cognito_user_pool.gzouli_user_pool.id

  generate_secret = false # Client public — frontend navigateur

  explicit_auth_flows = [
    "ALLOW_USER_SRP_AUTH",      # Auth SRP (recommandée, mot de passe jamais en clair)
    "ALLOW_USER_PASSWORD_AUTH", # Auth directe email/mdp (fallback)
    "ALLOW_REFRESH_TOKEN_AUTH"  # Renouvellement silencieux du token
  ]

  # Durées de vie des tokens
  token_validity_units {
    access_token  = "hours"
    id_token      = "hours"
    refresh_token = "days"
  }

  access_token_validity  = var.access_token_validity_hours
  id_token_validity      = var.access_token_validity_hours
  refresh_token_validity = var.refresh_token_validity_days

  prevent_user_existence_errors = "ENABLED"

  read_attributes = [
    "email",
    "email_verified",
    "given_name",
    "family_name"
  ]

  write_attributes = [
    "email",
    "given_name",
    "family_name"
  ]
}

################################################################
## Groupes Cognito = Rôles de l'ERP
## Utilisés par AdminAddUserToGroup dans CognitoServiceImpl
## et lus via le claim "cognito:groups" dans SecurityConfig
################################################################
resource "aws_cognito_user_group" "admin" {
  name         = "ADMIN"
  user_pool_id = aws_cognito_user_pool.gzouli_user_pool.id
  description  = "Administrateurs de l'ERP — accès complet"
  precedence   = 1
}

resource "aws_cognito_user_group" "ingenieur" {
  name         = "INGENIEUR"
  user_pool_id = aws_cognito_user_pool.gzouli_user_pool.id
  description  = "Ingénieurs — gestion de projets et supervision"
  precedence   = 2
}

resource "aws_cognito_user_group" "technicien" {
  name         = "TECHNICIEN"
  user_pool_id = aws_cognito_user_pool.gzouli_user_pool.id
  description  = "Techniciens — exécution des tâches terrain"
  precedence   = 3
}
