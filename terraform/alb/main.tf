resource "aws_lb" "gzouli_alb" {
  name               = "gzouli-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [var.alb_security_group_id]
  subnets            = var.public_subnet_ids
  preserve_host_header = true
  ip_address_type = "ipv4"

  enable_deletion_protection = false

  tags = {
    Name    = "gzouli_alb"
    Project = "gzouli"
  }
}

resource "aws_lb_listener" "gzouli_lb_listener_http" {
  load_balancer_arn = aws_lb.gzouli_alb.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type = "redirect"

    redirect {
      port        = "443"
      protocol    = "HTTPS"
      status_code = "HTTP_301"
    }
  }

  tags = {
    Name    = "gzouli_alb_http"
    Project = "gzouli"
  }
}

resource "aws_lb_listener" "gzouli_lb_listener_https" {
  load_balancer_arn = aws_lb.gzouli_alb.arn
  port              = "443"
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-TLS13-1-2-2021-06"
  certificate_arn   = var.acm_certificate_arn

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.gzouli_target_group.arn
  }

  tags = {
    Name    = "gzouli_alb_https"
    Project = "gzouli"
  }
}

resource "aws_lb_target_group" "gzouli_target_group" {
  name             = "gzouli-target-group"
  port             = 8080
  protocol         = "HTTP"
  protocol_version = "HTTP1"
  target_type      = "ip"
  ip_address_type  = "ipv4"
  vpc_id           = var.vpc_id

  health_check {
    protocol            = "HTTP"
    path                = "/actuator/health"
    healthy_threshold   = 5
    unhealthy_threshold = 5
    timeout             = 10
    interval            = 30
  }

  tags = {
    Name    = "gzouli_alb"
    Project = "gzouli"
  }
}
