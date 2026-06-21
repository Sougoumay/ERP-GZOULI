terraform {
  required_providers {
    aws = {
      source                = "hashicorp/aws"
      configuration_aliases = [aws.us_east_1]
    }
  }
}

# Zone hébergée pour gzouli.sougoumay.com
resource "aws_route53_zone" "gzouli" {
  name = var.domain_name
}

# CNAMEs de validation ACM — identiques pour les deux certificats (même domaine)
resource "aws_route53_record" "acm_validation" {
  for_each = {
    for dvo in var.acm_validation_options : dvo.domain_name => {
      name   = dvo.resource_record_name
      record = dvo.resource_record_value
      type   = dvo.resource_record_type
    }
  }

  allow_overwrite = true
  name            = each.value.name
  records         = [each.value.record]
  ttl             = 60
  type            = each.value.type
  zone_id         = aws_route53_zone.gzouli.zone_id
}

# Déclenche la validation du certificat ALB (eu-west-3)
resource "aws_acm_certificate_validation" "alb" {
  certificate_arn         = var.alb_certificate_arn
  validation_record_fqdns = [for record in aws_route53_record.acm_validation : record.fqdn]
}

# Déclenche la validation du certificat CloudFront (us-east-1)
resource "aws_acm_certificate_validation" "cloudfront" {
  provider                = aws.us_east_1
  certificate_arn         = var.cloudfront_certificate_arn
  validation_record_fqdns = [for record in aws_route53_record.acm_validation : record.fqdn]
}
