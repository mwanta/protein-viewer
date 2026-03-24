# Define full ECS infrastructure: a cluster, task definition (how to run both containers),
# and a service to keep one instance of the task running

# VPC and networking
data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

# Security group for EC2 instance
resource "aws_security_group" "ecs" {
  name   = "protein-viewer-ecs"
  vpc_id = data.aws_vpc.default.id

  # Allow HTTP traffic
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Allow Spring Boot port
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Allow all outbound traffic
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# ECS cluster
resource "aws_ecs_cluster" "main" {
  name = "protein-viewer"
}

# IAM role for ECS task execution
resource "aws_iam_role" "ecs_task_execution" {
  name = "protein-viewer-ecs-task-execution"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action    = "sts:AssumeRole"
      Effect    = "Allow"
      Principal = { Service = "ecs-tasks.amazonaws.com" }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution" {
  role       = aws_iam_role.ecs_task_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# ECS task definition
resource "aws_ecs_task_definition" "main" {
  family                   = "protein-viewer"
  network_mode             = "host"
  requires_compatibilities = ["EC2"]
  execution_role_arn       = aws_iam_role.ecs_task_execution.arn

  container_definitions = jsonencode([
    {
      name      = "backend"
      image     = "${aws_ecr_repository.backend.repository_url}:latest"
      memory    = 384
      cpu       = 256
      essential = true
      environment = [
        {
          name  = "SPRING_DATASOURCE_URL"
          value = "jdbc:postgresql://db.rcoviawjflafpjcoyxua.supabase.co:5432/postgres"
        },
        {
          name  = "SPRING_DATASOURCE_USERNAME"
          value = "postgres.protein_database"
        },
        {
          name  = "SPRING_DATASOURCE_PASSWORD"
          value = var.db_password
        }
      ]
      portMappings = [{
        containerPort = 8080
        hostPort      = 8080
      }]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = "/ecs/protein-viewer"
          awslogs-region        = "us-east-1"
          awslogs-stream-prefix = "backend"
        }
      }
    },
    {
      name      = "frontend"
      image     = "${aws_ecr_repository.frontend.repository_url}:latest"
      memory    = 128
      cpu       = 128
      essential = true
      environment = [{
        name  = "BACKEND_HOST"
        value = "localhost"
      }]
      portMappings = [{
        containerPort = 80
        hostPort      = 80
      }]
      dependsOn = [{
        containerName = "backend"
        condition     = "START"
      }]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = "/ecs/protein-viewer"
          awslogs-region        = "us-east-1"
          awslogs-stream-prefix = "frontend"
        }
      }
    }
  ])
}

# ECS service
resource "aws_ecs_service" "main" {
  name            = "protein-viewer"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.main.arn
  desired_count   = 1
  launch_type     = "EC2"
}

# Output the cluster name for GitHub Actions
output "ecs_cluster_name" {
  value = aws_ecs_cluster.main.name
}

output "ecs_service_name" {
  value = aws_ecs_service.main.name
}

resource "aws_cloudwatch_log_group" "ecs" {
  name              = "/ecs/protein-viewer"
  retention_in_days = 7
}