output "target_group_arn" {
  value = aws_lb_target_group.gzouli_target_group.arn
}

output "alb_arn" {
  value = aws_lb.gzouli_alb.arn
}

output "alb_dns_name" {
  value = aws_lb.gzouli_alb.dns_name
}

output "alb_zone_id" {
  value = aws_lb.gzouli_alb.zone_id
}
