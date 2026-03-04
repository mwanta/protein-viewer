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

### Requirements
- JDK 17 or 21
- Node.js (LTS)
- Maven

## To Run

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

### Usage
1. Enter a PDB ID in the search box (e.g. 4HHB, 1BNA, 3PTB)
2. Click Search
3. View the protein metadata and interact with the 3D structure

### API
The backend exposes a single endpoint:
```
GET /api/protein/{pdbId}
```
Returns metadata for the given PDB ID from the RCSB REST API.
