# EC2 (Elastic Compute Cloud) = virtual machine to run AWS's data centers
# AKA: remote computer to run containers 24/7

# IAM role for EC2 instance to communicate with ECS
resource "aws_iam_role" "ecs_instance" {
  name = "protein-viewer-ecs-instance"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action    = "sts:AssumeRole"
      Effect    = "Allow"
      Principal = { Service = "ec2.amazonaws.com" }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_instance" {
  role       = aws_iam_role.ecs_instance.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_instance_profile" "ecs_instance" {
  name = "protein-viewer-ecs-instance"
  role = aws_iam_role.ecs_instance.name
}

# EC2 instance (t3.small, inexpensive)
resource "aws_instance" "ecs" {
  ami                    = "ami-0605df8f00118a0df"
  instance_type          = "t3.small"
  iam_instance_profile   = aws_iam_instance_profile.ecs_instance.name
  vpc_security_group_ids = [aws_security_group.ecs.id]

  # Register this instance with the ECS cluster
  user_data = <<-EOF
    #!/bin/bash
    echo ECS_CLUSTER=protein-viewer >> /etc/ecs/ecs.config
  EOF

  tags = {
    Name = "protein-viewer-ecs"
  }
}

# Output the public IP for app access (format: http://instance_public_ip)
output "instance_public_ip" {
  value = aws_instance.ecs.public_ip
}