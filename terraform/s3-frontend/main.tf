# gzouli-frontend-local / gzouli-frontend-prod
resource "aws_s3_bucket" "gzouli_frontend" {
  bucket = "${var.app_name}-frontend-${var.environment}"

  tags = {
    Name    = "${var.app_name}-frontend-${var.environment}"
    Project = var.app_name
    Env     = var.environment
  }
}

# Le bucket reste privé : CloudFront est le seul point d'entrée public via OAC.
# Ne pas activer le static website hosting — l'origin CloudFront doit pointer sur
# le regional domain name S3 (pas sur l'endpoint website) pour que OAC fonctionne.
resource "aws_s3_bucket_public_access_block" "gzouli_frontend_access" {
  bucket = aws_s3_bucket.gzouli_frontend.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}


