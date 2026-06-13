output "user_pool_id" {
  description = "L'ID du User Pool — injecté comme variable d'environnement dans ECS (aws.cognito.userPoolId)"
  value       = aws_cognito_user_pool.gzouli_user_pool.id
}

output "user_pool_arn" {
  description = "L'ARN du User Pool Cognito"
  value       = aws_cognito_user_pool.gzouli_user_pool.arn
}

# L'issuer URI permet à Spring Security de télécharger automatiquement les JWKS
# et de valider la signature des tokens sans aucun appel manuel
output "issuer_uri" {
  description = "L'issuer URI OIDC — à configurer dans spring.security.oauth2.resourceserver.jwt.issuer-uri"
  value       = "https://cognito-idp.${var.region}.amazonaws.com/${aws_cognito_user_pool.gzouli_user_pool.id}"
}

output "user_pool_client_id" {
  description = "L'ID du client public — à mettre dans environment.cognitoAppClientId (Angular)"
  value       = aws_cognito_user_pool_client.gzouli_app_client.id
}
