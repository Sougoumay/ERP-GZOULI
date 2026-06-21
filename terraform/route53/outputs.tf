output "hosted_zone_id" {
  description = "ID de la zone Route 53"
  value       = aws_route53_zone.gzouli.zone_id
}

output "name_servers" {
  description = "Les 4 NS records à ajouter dans OVH pour déléguer gzouli.sougoumay.com vers Route 53"
  value       = aws_route53_zone.gzouli.name_servers
}

output "alb_validated_certificate_arn" {
  description = "ARN du certificat ALB (eu-west-3) après validation DNS — à utiliser dans module.alb"
  value       = aws_acm_certificate_validation.alb.certificate_arn
}

output "cloudfront_validated_certificate_arn" {
  description = "ARN du certificat CloudFront (us-east-1) après validation DNS — à utiliser dans module.cloudfront"
  value       = aws_acm_certificate_validation.cloudfront.certificate_arn
}
