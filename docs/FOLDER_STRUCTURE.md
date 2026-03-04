# SkyControl Pro - GitHub Repository Structure

## 📁 Complete Folder Structure

This is the recommended folder structure for your GitHub repository. It's organized as a **monorepo** with both backend and frontend.

```
skycontrol-pro/
│
├── backend/                              # Spring Boot Backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/skycontrol/
│   │   │   │   ├── SkyControlApplication.java
│   │   │   │   ├── config/
│   │   │   │   │   ├── OpenAPIConfig.java
│   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   └── CorsConfig.java
│   │   │   │   ├── controller/
│   │   │   │   │   ├── DroneController.java
│   │   │   │   │   ├── MissionController.java
│   │   │   │   │   ├── PilotController.java
│   │   │   │   │   ├── DashboardController.java
│   │   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── DroneService.java
│   │   │   │   │   ├── MissionService.java
│   │   │   │   │   └── PilotService.java
│   │   │   │   ├── repository/
│   │   │   │   │   ├── DroneRepository.java
│   │   │   │   │   ├── MissionRepository.java
│   │   │   │   │   └── PilotRepository.java
│   │   │   │   ├── model/
│   │   │   │   │   ├── Drone.java
│   │   │   │   │   ├── Mission.java
│   │   │   │   │   ├── Pilot.java
│   │   │   │   │   └── enums/
│   │   │   │   │       ├── DroneStatus.java
│   │   │   │   │       └── MissionStatus.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/
│   │   │   │   │   │   ├── CreateDroneRequest.java
│   │   │   │   │   │   └── CreateMissionRequest.java
│   │   │   │   │   └── response/
│   │   │   │   │       ├── DroneDTO.java
│   │   │   │   │       └── MissionDTO.java
│   │   │   │   ├── exception/
│   │   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   │   └── DuplicateResourceException.java
│   │   │   │   └── security/
│   │   │   │       └── JwtTokenProvider.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── application-dev.properties
│   │   │       └── application-test.properties
│   │   └── test/
│   │       └── java/com/skycontrol/
│   │           ├── controller/
│   │           │   └── DroneControllerIntegrationTest.java
│   │           └── service/
│   │               ├── DroneServiceTest.java
│   │               └── MissionServiceMCDCTest.java
│   ├── pom.xml
│   └── README.md
│
├── frontend/                             # React Frontend
│   ├── public/
│   │   ├── index.html
│   │   └── favicon.ico
│   ├── src/
│   │   ├── components/
│   │   │   ├── common/
│   │   │   │   ├── Header.jsx
│   │   │   │   └── LoadingSpinner.jsx
│   │   │   ├── dashboard/
│   │   │   │   ├── Dashboard.jsx
│   │   │   │   ├── FleetStatusCard.jsx
│   │   │   │   └── StatisticsCard.jsx
│   │   │   ├── drones/
│   │   │   │   ├── DroneList.jsx
│   │   │   │   ├── DroneDetails.jsx
│   │   │   │   └── DroneForm.jsx
│   │   │   ├── missions/
│   │   │   │   ├── MissionList.jsx
│   │   │   │   └── MissionForm.jsx
│   │   │   └── pilots/
│   │   │       └── PilotList.jsx
│   │   ├── services/
│   │   │   ├── api.js
│   │   │   ├── droneService.js
│   │   │   ├── missionService.js
│   │   │   └── pilotService.js
│   │   ├── hooks/
│   │   │   └── useAuth.js
│   │   ├── utils/
│   │   │   └── constants.js
│   │   ├── App.js
│   │   ├── App.css
│   │   └── index.js
│   ├── .env.example
│   ├── package.json
│   └── README.md
│
├── database/                             # Database Scripts
│   ├── schema/
│   │   ├── 01_create_enums.sql
│   │   ├── 02_create_tables.sql
│   │   ├── 03_create_indexes.sql
│   │   └── 04_create_views.sql
│   ├── sample-data/
│   │   ├── insert_drones.sql
│   │   └── insert_pilots.sql
│   └── README.md
│
├── docs/                                 # Documentation
│   ├── api/
│   │   └── API_DOCUMENTATION.md
│   ├── architecture/
│   │   ├── ARCHITECTURE.md
│   │   └── diagrams/
│   │       └── system-architecture.png
│   ├── testing/
│   │   ├── TEST_PLAN.md
│   │   └── MCDC_COVERAGE.md
│   └── setup/
│       └── DEVELOPMENT_SETUP.md
│
├── .gitignore
├── README.md
└── LICENSE
```

---

## 📂 Where Your Generated Files Go

### From schema.sql → Database Folder
```
database/
├── schema/
│   └── 02_create_tables.sql  ← Your complete schema goes here
└── sample-data/
    ├── insert_drones.sql      ← Sample drone data
    └── insert_pilots.sql      ← Sample pilot data
```

### From SpringBootControllers.java → Backend Controllers
```
backend/src/main/java/com/skycontrol/
├── controller/
│   ├── DroneController.java      ← Extract from your file
│   ├── MissionController.java    ← Extract from your file
│   ├── PilotController.java      ← Extract from your file
│   ├── DashboardController.java  ← Extract from your file
│   └── GlobalExceptionHandler.java ← Extract from your file
```

### From ReactComponents.jsx → Frontend Components
```
frontend/src/
├── components/
│   ├── dashboard/
│   │   └── Dashboard.jsx         ← Extract from your file
│   └── drones/
│       └── DroneList.jsx          ← Extract from your file
└── services/
    └── droneService.js            ← Extract from your file
```

### From JUnitTests.java → Test Files
```
backend/src/test/java/com/skycontrol/
├── service/
│   ├── DroneServiceTest.java           ← Extract from your file
│   └── MissionServiceMCDCTest.java     ← Extract from your file
└── controller/
    └── DroneControllerIntegrationTest.java ← Extract from your file
```

### From API_DOCUMENTATION.md → Docs Folder
```
docs/
└── api/
    └── API_DOCUMENTATION.md ← Your complete API docs
```

---

## 🔧 Essential Configuration Files

### Root README.md
```markdown
# SkyControl Pro - Drone Fleet Management System

Professional drone fleet management system for commercial operations.

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- PostgreSQL 14+

### Setup
1. Clone repository: `git clone https://github.com/yourusername/skycontrol-pro.git`
2. Setup database: `cd database && psql -f schema/02_create_tables.sql`
3. Start backend: `cd backend && ./mvnw spring-boot:run`
4. Start frontend: `cd frontend && npm install && npm start`

## 📚 Documentation
- [API Documentation](docs/api/API_DOCUMENTATION.md)
- [Setup Guide](docs/setup/DEVELOPMENT_SETUP.md)
- [Architecture](docs/architecture/ARCHITECTURE.md)

## 🧪 Testing
- Backend: `cd backend && mvn test`
- Frontend: `cd frontend && npm test`

## 📁 Project Structure
- `/backend` - Spring Boot REST API
- `/frontend` - React application
- `/database` - Database schemas
- `/docs` - Documentation
```

### backend/pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <groupId>com.skycontrol</groupId>
    <artifactId>skycontrol-pro-backend</artifactId>
    <version>1.0.0</version>
    <name>SkyControl Pro Backend</name>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.2.0</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

### frontend/package.json
```json
{
  "name": "skycontrol-pro-frontend",
  "version": "1.0.0",
  "private": true,
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.20.0",
    "@mui/material": "^5.14.0",
    "@mui/icons-material": "^5.14.0",
    "axios": "^1.6.0",
    "recharts": "^2.10.0"
  },
  "scripts": {
    "start": "react-scripts start",
    "build": "react-scripts build",
    "test": "react-scripts test"
  }
}
```

### .gitignore (Root)
```gitignore
# IDE
.idea/
.vscode/
*.iml

# OS
.DS_Store
Thumbs.db

# Logs
*.log

# Environment
.env
.env.local
```

### backend/.gitignore
```gitignore
target/
*.class

# IDE
.idea/
.settings/

# Application
application-local.properties
```

### frontend/.gitignore
```gitignore
# Dependencies
node_modules/

# Production
/build

# Testing
/coverage

# Environment
.env.local
```

---

## 🎯 Recommended Development Workflow

### 1. Initial Repository Setup
```bash
# Create repository on GitHub
# Clone locally
git clone https://github.com/yourusername/skycontrol-pro.git
cd skycontrol-pro

# Create folder structure
mkdir -p backend/src/{main,test}/java/com/skycontrol
mkdir -p frontend/src/{components,services}
mkdir -p database/{schema,sample-data}
mkdir -p docs/{api,architecture,testing}
```

### 2. Add Your Files
```bash
# Add database schema
cp schema.sql database/schema/02_create_tables.sql

# Extract and add Spring Boot controllers
# (Manually split SpringBootControllers.java into separate files)

# Extract and add React components  
# (Manually split ReactComponents.jsx into separate files)

# Add tests
# (Manually split JUnitTests.java into separate test files)

# Add documentation
cp API_DOCUMENTATION.md docs/api/
```

### 3. Commit and Push
```bash
git add .
git commit -m "Initial project structure with complete implementation"
git push origin main
```

---

## 📊 Branch Strategy (Recommended)

```
main                    # Production-ready code
├── develop            # Development branch
    ├── feature/drones     # Drone management features
    ├── feature/missions   # Mission management features
    └── feature/dashboard  # Dashboard features
```

---

## ✅ Checklist for GitHub

Before pushing to GitHub:

- [ ] README.md with clear instructions
- [ ] .gitignore files (root, backend, frontend)
- [ ] LICENSE file
- [ ] All code properly organized
- [ ] Documentation complete
- [ ] Tests passing
- [ ] No sensitive data (passwords, keys)
- [ ] .env.example files (not .env)

---

## 🚀 Quick Commands Reference

```bash
# Clone and setup
git clone <your-repo-url>
cd skycontrol-pro

# Setup database
createdb skycontrol_pro
psql skycontrol_pro < database/schema/02_create_tables.sql

# Start backend
cd backend
./mvnw spring-boot:run

# Start frontend (new terminal)
cd frontend
npm install
npm start

# Run tests
cd backend && mvn test
cd frontend && npm test
```

---

This structure keeps your project professional, organized, and easy to navigate!
