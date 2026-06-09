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

resource "aws_internet_gateway" "gzouli_igw" {
  vpc_id = aws_vpc.gzouli_vpc.id

  tags = {
    Name = "gzouli_igw"
    Project = "gzouli"
  }
}

resource "aws_route_table" "gzouli_public_route_table" {
  vpc_id = aws_vpc.gzouli_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.gzouli_igw.id
  }

  tags = {
    Name = "gzouli_public_route_table"
    Project = "gzouli"
  }
}

resource "aws_route_table_association" "gzouli_route_table_association_pb_subnet_1" {
  route_table_id = aws_route_table.gzouli_public_route_table.id
  subnet_id = aws_subnet.gzouli_pb_subnet_1.id
}

resource "aws_route_table_association" "gzouli_route_table_association_pb_subnet_2" {
  route_table_id = aws_route_table.gzouli_public_route_table.id
  subnet_id = aws_subnet.gzouli_pb_subnet_2.id
}

resource "aws_eip" "gzouli_eip" {
  domain = "vpc"

  tags = {
    Name = "gzouli_eip"
    Project = "gzouli"
  }
}

resource "aws_nat_gateway" "gzouli_nat" {
  allocation_id = aws_eip.gzouli_eip.id
  subnet_id = aws_subnet.gzouli_pb_subnet_1.id

  tags = {
    Name = "gzouli_nat"
    Project = "gzouli"
  }

  depends_on = [aws_internet_gateway.gzouli_igw]
}

resource "aws_route_table" "gzouli_private_route_table" {
  vpc_id = aws_vpc.gzouli_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.gzouli_nat.id
  }

  tags = {
    Name = "gzouli_private_route_table"
    Project = "gzouli"
  }
}

resource "aws_route_table_association" "gzouli_route_table_association_private_subnet_1" {
  route_table_id = aws_route_table.gzouli_private_route_table.id
  subnet_id = aws_subnet.gzouli_private_subnet_1.id
}

resource "aws_route_table_association" "gzouli_route_table_association_private_subnet_2" {
  route_table_id = aws_route_table.gzouli_private_route_table.id
  subnet_id = aws_subnet.gzouli_private_subnet_2.id
}

