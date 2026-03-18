# Protein Structure Viewer

## Summary

A full-stack web application for searching and visualizing protein structures from the RCSB Protein Data Bank.

## Features
- Search proteins by PDB ID
- Display protein metadata including name, experimental method, and resolution
- Interactive 3D structure visualization rendered in the browser

## Tech Stack

### Frontend
- Javascript (React, Vite)
- 3Dmol.js for 3D structure rendering

### Backend
- Java 21
- Spring Boot
- RCSB PDB REST API

### Infrastructure & DevOps
- Docker & Docker Compose
- AWS ECS/EC2 for container hosting
- AWS ECR for container image registry
- AWS CloudWatch for logging
- Terraform for infrastructure as code
- GitHub Actions for CI/CD

### Requirements
- JDK 17 or 21
- Node.js (LTS)
- Maven
- Docker Desktop
- AWS CLI
- Terraform

## To Run Locally

### Backend

From the project root:
```
./mvnw spring-boot:run
```
the backend will start on http://localhost:8080

### Frontend 
From the client directory:
```
npm install
npm run dev
```
The frontend will start on http://localhost:5173

### Docker Compose
To run both services together with Docker:
```
docker compose up --build
```
The app will be available at http://localhost

## Cloud Deployment
Infrastructure is managed with Terraform and deployed to AWS ECS/EC2

### Spin Up Infrastructure
```
cd terraform
terraform init
terraform apply
```

### Tear Down Infrastructure
```
terraform destory
```

CI/CD is managed by GitHub Actions, a non-trivial push to main automatically   
builds and pushes Docker images to ECR and deploys to ECS.  
The workflow can also be triggered manually from the Actions tab in GitHub. 


## Usage
1. Enter a PDB ID in the search box (e.g. 4HHB, 1BNA, 3PTB)
2. Click Search
3. View the protein metadata and interact with the 3D structure

### API
The backend exposes a single endpoint:
```
GET /api/protein/{pdbId}
```
Returns metadata for the given PDB ID from the RCSB REST API.
