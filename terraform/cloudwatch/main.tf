resource "aws_cloudwatch_log_group" "gzouli_cloudwatch_log_group" {
  name = "/ecs/gzouli/backend"
  retention_in_days = 7

  tags = {
    Name = "gzouli_cloud_watch_log_group"
    Project = "gzouli"
  }
}