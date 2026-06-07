resource "aws_secretsmanager_secret" "gzouli_secret_manager" {
  name = "gzouli_prod_db_credentials"


  tags = {
    Name = "gzouli_secret_manager"
    Project = "gzouli"
  }
}