region                = "eu-west-3"
az1                   = "eu-west-3a"
az2                   = "eu-west-3b"
main_cidr             = "10.0.1.0/24"
pb_subnet_1_cidr      = "10.0.1.0/26"
pb_subnet_2_cidr      = "10.0.1.64/26"
private_subnet_1_cidr = "10.0.1.128/26"
private_subnet_2_cidr = "10.0.1.192/26"
domain_name           = "gzouli.sougoumay.com"

# local : Angular tourne sur localhost:4200
# prod  : remplace par l'URL CloudFront ou ton domaine front (ex: "https://app.gzouli.com")
frontend_origins = ["http://localhost:4200", "https://gzouli.sougoumay.com"]