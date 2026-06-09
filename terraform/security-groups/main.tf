resource "aws_security_group" "gzouli_alb_sg" {
  name = "gzouli-alb-sg"
  description = "Allow HTTP and TLS inbound traffic and outbound traffic to ecs-sg"
  vpc_id      = var.main_vpc_id

  tags = {
    Name = "gzouli_alb_sg"
    Project = "gzouli"
  }
}

resource "aws_vpc_security_group_ingress_rule" "gzouli_alb_sg_ingress_rule_https" {
  security_group_id = aws_security_group.gzouli_alb_sg.id
  cidr_ipv4 = "0.0.0.0/0"
  ip_protocol       = "tcp"
  from_port = 443
  to_port = 443
}

resource "aws_vpc_security_group_ingress_rule" "gzouli_alb_sg_ingress_rule_http" {
  security_group_id = aws_security_group.gzouli_alb_sg.id
  cidr_ipv4 = "0.0.0.0/0"
  ip_protocol       = "tcp"
  from_port = 80
  to_port = 80
}

resource "aws_security_group" "gzouli_ecs_sg" {
  name = "gzouli_ecs-sg"
  description = "Allow inbound traffic from gzouli-alb-sg and outbound traffic to gzouli-rds-sg and internet"
  vpc_id = var.main_vpc_id

  ingress {
    protocol = "tcp"
    from_port = 8080
    to_port = 8080
    description = "allow inbound traffic from alg-sb in the port 8080"
    security_groups = [aws_security_group.gzouli_alb_sg.id]
  }

  egress {
    protocol = "tcp"
    from_port = 5432
    to_port = 5432
    description = "Allow output traffic to rds-sg"
    security_groups = [] //TODO
  }

  egress {
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    from_port = 443
    to_port = 443
    description = "Allow outbound traffic into internet"
  }

  tags = {
    Name = "gzouli_ecs_sg"
    Project = "gzouli"
  }
}