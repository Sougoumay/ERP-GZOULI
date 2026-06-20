resource "aws_ecr_repository" "gzouli_ecr_repository" {

  name = "gzouli-ecr-repository"
  image_tag_mutability = "IMMUTABLE_WITH_EXCLUSION"
  force_delete = true

  image_tag_mutability_exclusion_filter {
    filter      = "latest*"
    filter_type = "WILDCARD"
  }

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name = "gzouli_ecr_repository"
    Project = "gzouli"
  }
}