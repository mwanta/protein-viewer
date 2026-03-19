# Protein Structure Viewer

## Summary

A full-stack web application for searching and visualizing protein structures from the RCSB Protein Data Bank.

## Features
- Search proteins by PDB ID
- Display protein metadata including name, experimental method, and resolution
- Interactive 3D structure visualization rendered in the browser
- Cache protein metadata in a PostgreSQL database to reduce redundant API calls
- Save and manage favorite proteins that persist across sessions. 

## Tech Stack

### Frontend
- Javascript (React, Vite)
- 3Dmol.js for 3D structure rendering

### Backend
- Java 21
- Spring Boot
- Spring Data JPA / Hibernate
- RCSB PDB REST API

### Database
- PostgreSQL
- Two tables: proteins (metadata cache) and favorites (user saved proteins)

### Infrastructure & DevOps
- Docker & Docker Compose
- AWS ECS/EC2 for container hosting
- AWS ECR for container image registry
- AWS RDS (PostgreSQL) for cloud database
- AWS CloudWatch for logging
- AWS S3 for Terraform remote state
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
To run all services (frontend, backend, and database) together with Docker:
```
docker compose up --build
```
The app will be available at http://localhost

## Cloud Deployment
Infrastructure is managed with Terraform and deployed to AWS ECS/EC2 with an RDS PostgreSQL database.

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
The workflow can also be triggered manually from the Actions tab in GitHub without pushing code. 


## Usage
1. Enter a PDB ID in the search box (e.g. 4HHB, 1BNA, 3PTB)
2. Click Search or press Enter
3. View the protein metadata and interact with the 3D structure
4. Click Add to Favorites to save the protein for later
5. Use the Favorites list (at the bottom of the screen) to reload or remove saved proteins

### API
The backend exposes the following endpoints:
```
GET    /api/protein/{pdbId}     — fetch protein metadata (checks cache first)
GET    /api/favorites           — list all favorited proteins
POST   /api/favorites/{pdbId}   — add a protein to favorites
DELETE /api/favorites/{pdbId}   — remove a protein from favorites
```

