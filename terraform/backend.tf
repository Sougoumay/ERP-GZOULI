terraform {
  backend "s3" {
    bucket         = "infra-training-terraform-state"
    key            = "gzouli/terraform.tfstate"
    region         = "eu-west-3"


    use_lockfile = true
    encrypt        = true
  }
}