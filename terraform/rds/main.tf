resource "aws_db_subnet_group" "social_media_rds_subnets" {
  name       = "gzouli-rds-subnet-group"
  subnet_ids = [var.private_subnet_1_id, var.private_subnet_2_id]

  tags = {
    Name    = "gzouli-rds-subnet-group"
    Project = "gzouli"
  }
}

resource "aws_db_instance" "gzouli_rds" {
  identifier              = "gzouli-db"
  allocated_storage       = 20
  max_allocated_storage   = 100
  engine                  = "postgres"
  engine_version          = "16"
  instance_class          = "db.t3.micro"
  db_name                 = "gzouli_db"
  username                = "gzouli_db_admin"
  manage_master_user_password = true
  db_subnet_group_name    = aws_db_subnet_group.social_media_rds_subnets.name
  vpc_security_group_ids  = [var.gzouli_rds_sg_id]
  multi_az                = true
  publicly_accessible     = false
  skip_final_snapshot     = false
  final_snapshot_identifier = "gzouli-final-snapshot"
  deletion_protection     = false // Si on met true, je ne pourrai faire destroy, true en prod

  backup_retention_period     = 7
  storage_encrypted       = true
  storage_type            = "gp3"
  allow_major_version_upgrade = false  # Risqué en prod

  timeouts {
    create = "3h"
    delete = "3h"
    update = "3h"
  }

  tags = {
    Name    = "gzouli-rds"
    Project = "gzouli"
  }
}