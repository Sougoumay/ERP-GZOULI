resource "aws_security_group" "gzouli_alb_sg" {
  name        = "gzouli-alb-sg"
  description = "Allow HTTP and TLS inbound traffic and outbound traffic to ecs-sg"
  vpc_id      = var.main_vpc_id

  tags = {
    Name    = "gzouli-alb-sg"
    Project = "gzouli"
  }
}

resource "aws_vpc_security_group_egress_rule" "gzouli_alb_sg_egress_ecs" {
  security_group_id            = aws_security_group.gzouli_alb_sg.id
  referenced_security_group_id = aws_security_group.gzouli_ecs_sg.id
  ip_protocol                  = "tcp"
  from_port                    = 8080
  to_port                      = 8080
  description                  = "Allow outbound traffic to ecs-sg"
}

resource "aws_vpc_security_group_ingress_rule" "gzouli_alb_sg_ingress_https" {
  security_group_id = aws_security_group.gzouli_alb_sg.id
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "tcp"
  from_port         = 443
  to_port           = 443
  description       = "Allow inbound HTTPS traffic"
}

resource "aws_vpc_security_group_ingress_rule" "gzouli_alb_sg_ingress_http" {
  security_group_id = aws_security_group.gzouli_alb_sg.id
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "tcp"
  from_port         = 80
  to_port           = 80
  description       = "Allow inbound HTTP traffic"
}

resource "aws_security_group" "gzouli_ecs_sg" {
  name        = "gzouli-ecs-sg"
  description = "Allow inbound traffic from gzouli-alb-sg and outbound traffic to gzouli-rds-sg and internet"
  vpc_id      = var.main_vpc_id

  tags = {
    Name    = "gzouli-ecs-sg"
    Project = "gzouli"
  }
}

resource "aws_vpc_security_group_ingress_rule" "gzouli_ecs_sg_ingress_alb" {
  security_group_id            = aws_security_group.gzouli_ecs_sg.id
  referenced_security_group_id = aws_security_group.gzouli_alb_sg.id
  ip_protocol                  = "tcp"
  from_port                    = 8080
  to_port                      = 8080
  description                  = "Allow inbound traffic from alb-sg on port 8080"
}

resource "aws_vpc_security_group_egress_rule" "gzouli_ecs_sg_egress_rds" {
  security_group_id            = aws_security_group.gzouli_ecs_sg.id
  referenced_security_group_id = aws_security_group.gzouli_rds_sg.id
  ip_protocol                  = "tcp"
  from_port                    = 5432
  to_port                      = 5432
  description                  = "Allow outbound traffic to rds-sg"
}

resource "aws_vpc_security_group_egress_rule" "gzouli_ecs_sg_egress_internet" {
  security_group_id = aws_security_group.gzouli_ecs_sg.id
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "tcp"
  from_port         = 443
  to_port           = 443
  description       = "Allow outbound HTTPS traffic to internet"
}

resource "aws_security_group" "gzouli_rds_sg" {
  name        = "gzouli-rds-sg"
  description = "Allow inbound traffic from gzouli-ecs-sg"
  vpc_id      = var.main_vpc_id

  tags = {
    Name    = "gzouli-rds-sg"
    Project = "gzouli"
  }
}

resource "aws_vpc_security_group_ingress_rule" "gzouli_rds_sg_ingress_ecs" {
  security_group_id            = aws_security_group.gzouli_rds_sg.id
  referenced_security_group_id = aws_security_group.gzouli_ecs_sg.id
  ip_protocol                  = "tcp"
  from_port                    = 5432
  to_port                      = 5432
  description                  = "Allow inbound traffic from ecs-sg"
}
