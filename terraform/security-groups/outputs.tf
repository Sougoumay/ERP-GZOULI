output "gzouli_rds_sg_id" {
  value = aws_security_group.gzouli_rds_sg.id
}

output "gzouli_ecs_sg_id" {
  value = aws_security_group.gzouli_ecs_sg.id
}

output "gzouli_alb_sg_id" {
  value = aws_security_group.gzouli_alb_sg.id
}