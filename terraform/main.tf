terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

# Configure the AWS provider
provider "aws" {
  region = "us-east-1"
}

# ECR repository for backend image
resource "aws_ecr_repository" "backend" {
  name         = "protein-viewer-backend"
  force_delete = true
}

# ECR repository for frontend image
resource "aws_ecr_repository" "frontend" {
  name         = "protein-viewer-frontend"
  force_delete = true
}

# Output the repository URLs to push images to them
# Call: docker buildx build --platform linux/amd64 -t backend_repo_url:latest --push .
output "backend_repo_url" {
  value = aws_ecr_repository.backend.repository_url
}

output "frontend_repo_url" {
  value = aws_ecr_repository.frontend.repository_url
}