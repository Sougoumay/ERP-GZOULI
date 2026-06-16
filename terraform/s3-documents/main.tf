# Le nom inclut l'environnement car les noms S3 sont globalement uniques
# local → gzouli-documents-local
# prod  → gzouli-documents-prod
resource "aws_s3_bucket" "gzouli_bucket" {
  bucket = "${var.app_name}-documents-${var.environment}"

  tags = {
    Name    = "${var.app_name}-documents-${var.environment}"
    Project = var.app_name
    Env     = var.environment
  }
}

resource "aws_s3_bucket_public_access_block" "gzouli_bucket_access" {
  bucket = aws_s3_bucket.gzouli_bucket.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_versioning" "gzouli_bucket_versioning" {
  bucket = aws_s3_bucket.gzouli_bucket.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "gzouli_bucket_encryption" {
  bucket = aws_s3_bucket.gzouli_bucket.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

# Autorise le navigateur à faire des requêtes directes vers S3 avec une URL présignée.
resource "aws_s3_bucket_cors_configuration" "gzouli_bucket_cors" {
  bucket = aws_s3_bucket.gzouli_bucket.id

  cors_rule {
    allowed_methods = ["GET", "HEAD"]
    allowed_origins = var.frontend_origins
    allowed_headers = ["*"]
    expose_headers  = ["ETag", "Content-Disposition", "Content-Length", "Content-Type"]
    # Cache le résultat du preflight 1h pour éviter une requête OPTIONS avant chaque téléchargement
    max_age_seconds = 3600
  }
}