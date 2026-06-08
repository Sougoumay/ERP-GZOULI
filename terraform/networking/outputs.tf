output "private_subnet_1_arn" {
  value = aws_subnet.gzouli_private_subnet_1.arn
}

output "private_subnet_2_arn" {
  value = aws_subnet.gzouli_private_subnet_2.arn
}

output "pb_subnet_1_arn" {
  value = aws_subnet.gzouli_pb_subnet_1.arn
}

output "pb_subnet_2_arn" {
  value = aws_subnet.gzouli_pb_subnet_2.arn
}

output "main_vpc_arn" {
  value = aws_vpc.gzouli_vpc.arn
}