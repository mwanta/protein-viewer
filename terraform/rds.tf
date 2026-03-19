# Security group for RDS
resource "aws_security_group" "rds" {
  name   = "protein-viewer-rds"
  vpc_id = data.aws_vpc.default.id

  # Allow PostgreSQL traffic from ECS security group
  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# RDS subnet group
resource "aws_db_subnet_group" "main" {
  name       = "protein-viewer"
  subnet_ids = data.aws_subnets.default.ids
}

# RDS PostgreSQL instance
resource "aws_db_instance" "main" {
  identifier        = "protein-viewer"
  engine            = "postgres"
  engine_version    = "17"
  instance_class    = "db.t3.micro"
  allocated_storage = 20

  db_name  = "proteinviewer"
  username = "postgres"
  password = "postgres"

  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rds.id]

  skip_final_snapshot = true
  publicly_accessible = false
}

# Output the RDS endpoint
output "rds_endpoint" {
  value = aws_db_instance.main.endpoint
}