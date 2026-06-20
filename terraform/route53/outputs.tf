output "hosted_zone_id" {
  description = "ID de la zone Route 53"
  value       = aws_route53_zone.gzouli.zone_id
}

output "name_servers" {
  description = "Les 4 NS records à ajouter dans OVH pour déléguer gzouli.sougoumay.com vers Route 53"
  value       = aws_route53_zone.gzouli.name_servers
}
