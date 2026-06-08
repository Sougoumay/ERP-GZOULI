resource "aws_vpc" "gzouli_vpc" {
  cidr_block       = var.main_cidr
  enable_dns_hostnames = true
  enable_dns_support = true

  tags = {
    Name = "gzouli-vpc"
    Project = "gzouli"
  }
}



resource "aws_subnet" "gzouli_private_subnet_1" {
  vpc_id = aws_vpc.gzouli_vpc.id
  cidr_block = var.private_subnet_1_cidr
  availability_zone = var.az1
  map_public_ip_on_launch = false

  tags = {
    Name = "gzouli_private_subnet_1"
    Project = "gzouli"
  }
}

resource "aws_subnet" "gzouli_private_subnet_2" {
  vpc_id = aws_vpc.gzouli_vpc.id
  cidr_block = var.private_subnet_2_cidr
  availability_zone = var.az2
  map_public_ip_on_launch = false

  tags = {
    Name = "gzouli_private_subnet_2"
    Project = "gzouli"
  }
}

resource "aws_subnet" "gzouli_pb_subnet_1" {
  vpc_id = aws_vpc.gzouli_vpc.id
  cidr_block = var.pb_subnet_1_cidr
  availability_zone = var.az1
  map_public_ip_on_launch = true

  tags = {
    Name = "gzouli_pb_subnet_1"
    Project = "gzouli"
  }
}


resource "aws_subnet" "gzouli_pb_subnet_2" {
  vpc_id = aws_vpc.gzouli_vpc.id
  cidr_block = var.pb_subnet_2_cidr
  availability_zone = var.az2
  map_public_ip_on_launch = true

  tags = {
    Name = "gzouli_pb_subnet_2"
    Project = "gzouli"
  }
}