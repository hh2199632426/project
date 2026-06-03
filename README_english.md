# Racing Quiz PK Game

A two-player split-screen racing quiz game built with Vue 3, Java Spring Boot, and MySQL.

Players answer questions while racing against each other on the same screen. Correct answers help the car speed up, while wrong answers slow it down, creating a simple but exciting quiz competition experience.

## Project Structure

- `frontend/`: Vue 3 frontend project
- `backend-java/`: Java backend service built with Spring Boot, MyBatis-Plus, and MySQL question database
- `backend/`: Legacy Node.js backend, no longer used as the main backend

## Quick Start

### 1. Initialize the Database

Run the following SQL script to create the database tables and insert the initial quiz questions:

```bash
mysql -u root -p < backend-java/sql/schema.sql
```

### 2. Configure Backend Connection Settings

The backend connection settings are already configured in:

```text
backend-java/src/main/resources/application.yml
```

To change the database host, username, password, or other connection details, edit this file directly.

### 3. Start the Backend Service

```bash
cd backend-java
mvn spring-boot:run
```

After startup, the backend service will run at:

```text
http://localhost:3000
```

### 4. Start the Frontend Project

```bash
cd frontend
npm install
npm run dev
```

After startup, the frontend project will run at:

```text
http://localhost:5173
```

## Features

- **Two-Player Split-Screen PK**  
  Two players can compete on the same screen using a left-and-right split layout.

- **Racing and Quiz Interaction**  
  Correct answers increase the car's speed, while wrong answers reduce the speed.

- **Dynamic Racing Effects**  
  Cars move forward during the race, and background trees move backward to create a sense of speed.

- **Database-Based Questions**  
  Quiz questions are loaded from the MySQL database through the `/api/questions` API.

## Tech Stack

### Frontend

- Vue 3
- Pinia
- Axios
- Vite

### Backend

- Java 21
- Spring Boot 3
- MyBatis-Plus
- MySQL
