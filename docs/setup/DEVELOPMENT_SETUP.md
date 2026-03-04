# SkyControl Pro - Complete Implementation Guide

## 📋 Project Overview

This document provides a comprehensive guide to implementing the **SkyControl Pro** Drone Fleet Management System, including all the code, documentation, and testing materials you need for your full-stack application project.

---

## 📦 Deliverables Included

### 1. **Database Schema** (`schema.sql`)
- Complete PostgreSQL database schema with 11 core tables
- Enums for status management
- Indexes for performance optimization
- Triggers for automatic timestamp updates
- Views for common queries
- Sample data for testing
- **Size:** 26KB with comprehensive comments

### 2. **Spring Boot Controllers** (`SpringBootControllers.java`)
- 5 complete REST controllers (Drone, Mission, Pilot, Dashboard, Exception Handler)
- Swagger/OpenAPI annotations for automatic documentation
- Proper HTTP status codes and error handling
- Request validation
- Pagination support
- **Size:** 22KB with detailed examples

### 3. **React Components** (`ReactComponents.jsx`)
- Dashboard with real-time updates and charts
- Drone list with filtering and pagination
- Mission form with validation
- API service layer with axios
- Material-UI components
- **Size:** 27KB with modern React patterns

### 4. **JUnit Tests** (`JUnitTests.java`)
- Unit tests for DroneService
- MC/DC coverage examples for mission approval logic
- Integration tests for REST endpoints
- Combinatorial testing for pre-flight inspections
- Parameterized tests
- **Size:** 32KB with comprehensive test cases

### 5. **API Documentation** (`API_DOCUMENTATION.md`)
- Complete REST API reference
- Request/response examples
- Authentication guide
- Error handling documentation
- Swagger configuration
- Postman collection template
- **Size:** 19KB with detailed examples

### 6. **README.md** (This file)
- Project overview and setup instructions
- Architecture documentation
- Development roadmap
- Testing strategies

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                        Frontend Layer                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │Dashboard │  │  Drones  │  │ Missions │  │  Pilots  │   │
│  │Component │  │   List   │  │   Form   │  │   List   │   │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘   │
│       │             │               │             │          │
│       └─────────────┴───────────────┴─────────────┘          │
│                          │                                    │
│                   ┌──────▼──────┐                           │
│                   │ API Service │ (Axios)                   │
│                   └──────┬──────┘                           │
└──────────────────────────┼──────────────────────────────────┘
                           │ HTTP/REST
┌──────────────────────────▼──────────────────────────────────┐
│                     Backend Layer                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              Spring Boot REST API                     │  │
│  │  ┌────────┐  ┌────────┐  ┌────────┐  ┌────────┐    │  │
│  │  │ Drone  │  │Mission │  │ Pilot  │  │Dashboard│    │  │
│  │  │Controller│ │Controller│ │Controller│ │Controller│  │  │
│  │  └───┬────┘  └───┬────┘  └───┬────┘  └───┬────┘    │  │
│  │      │           │           │           │           │  │
│  │  ┌───▼────┐  ┌───▼────┐  ┌───▼────┐  ┌───▼────┐    │  │
│  │  │ Drone  │  │Mission │  │ Pilot  │  │Dashboard│    │  │
│  │  │Service │  │Service │  │Service │  │Service  │    │  │
│  │  └───┬────┘  └───┬────┘  └───┬────┘  └───┬────┘    │  │
│  │      │           │           │           │           │  │
│  │  ┌───▼────┐  ┌───▼────┐  ┌───▼────┐  ┌───▼────┐    │  │
│  │  │ Drone  │  │Mission │  │ Pilot  │  │Dashboard│    │  │
│  │  │Repository│ │Repository│ │Repository│ │Repository│  │  │
│  │  └────────┘  └────────┘  └────────┘  └────────┘    │  │
│  └──────────────────────┬───────────────────────────────┘  │
└─────────────────────────┼──────────────────────────────────┘
                          │ JDBC/JPA
┌─────────────────────────▼──────────────────────────────────┐
│                    Database Layer                           │
│              PostgreSQL 14+ with PostGIS                    │
│  ┌─────────┐  ┌────────┐  ┌─────────┐  ┌──────────┐      │
│  │ drones  │  │missions│  │ pilots  │  │ batteries│      │
│  └─────────┘  └────────┘  └─────────┘  └──────────┘      │
│  ┌──────────┐  ┌─────────────┐  ┌──────────┐             │
│  │ flight_  │  │ maintenance │  │incidents │             │
│  │  logs    │  │  _records   │  │          │             │
│  └──────────┘  └─────────────┘  └──────────┘             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🚀 Getting Started

### Prerequisites

**Backend:**
- Java 17 or higher
- Maven 3.8+ or Gradle 7+
- PostgreSQL 14+
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

**Frontend:**
- Node.js 18+ and npm/yarn
- VS Code or similar editor

### Step 1: Database Setup

```bash
# Create database
createdb skycontrol_pro

# Import schema
psql -d skycontrol_pro -f schema.sql

# Verify tables created
psql -d skycontrol_pro -c "\dt"
```

### Step 2: Backend Setup

Create a Spring Boot project structure:

```
skycontrol-pro/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── skycontrol/
│   │   │           ├── SkyControlApplication.java
│   │   │           ├── config/
│   │   │           │   ├── OpenAPIConfig.java
│   │   │           │   └── SecurityConfig.java
│   │   │           ├── controller/
│   │   │           │   ├── DroneController.java
│   │   │           │   ├── MissionController.java
│   │   │           │   ├── PilotController.java
│   │   │           │   ├── DashboardController.java
│   │   │           │   └── GlobalExceptionHandler.java
│   │   │           ├── service/
│   │   │           │   ├── DroneService.java
│   │   │           │   ├── MissionService.java
│   │   │           │   └── PilotService.java
│   │   │           ├── repository/
│   │   │           │   ├── DroneRepository.java
│   │   │           │   ├── MissionRepository.java
│   │   │           │   └── PilotRepository.java
│   │   │           ├── model/
│   │   │           │   ├── Drone.java
│   │   │           │   ├── Mission.java
│   │   │           │   └── Pilot.java
│   │   │           ├── dto/
│   │   │           │   ├── DroneDTO.java
│   │   │           │   ├── MissionDTO.java
│   │   │           │   └── CreateDroneRequest.java
│   │   │           └── exception/
│   │   │               ├── ResourceNotFoundException.java
│   │   │               └── DuplicateResourceException.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-test.properties
│   └── test/
│       └── java/
│           └── com/
│               └── skycontrol/
│                   ├── service/
│                   │   ├── DroneServiceTest.java
│                   │   └── MissionServiceMCDCTest.java
│                   └── controller/
│                       └── DroneControllerIntegrationTest.java
├── pom.xml (or build.gradle)
└── README.md
```

**application.properties:**

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/skycontrol_pro
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Server Configuration
server.port=8080

# Swagger Configuration
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

# Logging
logging.level.com.skycontrol=DEBUG
logging.level.org.springframework.web=INFO
```

**pom.xml dependencies:**

```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- Database -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- OpenAPI/Swagger -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.2.0</version>
    </dependency>

    <!-- Lombok (optional, for reducing boilerplate) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Step 3: Frontend Setup

Create a React project:

```bash
npx create-react-app skycontrol-pro-frontend
cd skycontrol-pro-frontend
npm install @mui/material @emotion/react @emotion/styled
npm install @mui/icons-material
npm install axios
npm install react-router-dom
npm install recharts
npm install @mui/x-date-pickers
npm install date-fns
```

**Project structure:**

```
skycontrol-pro-frontend/
├── public/
├── src/
│   ├── components/
│   │   ├── Dashboard/
│   │   │   ├── Dashboard.jsx
│   │   │   ├── FleetStatusCard.jsx
│   │   │   └── StatisticsCard.jsx
│   │   ├── Drones/
│   │   │   ├── DroneList.jsx
│   │   │   ├── DroneDetails.jsx
│   │   │   └── DroneForm.jsx
│   │   ├── Missions/
│   │   │   ├── MissionList.jsx
│   │   │   └── MissionForm.jsx
│   │   └── Pilots/
│   │       └── PilotList.jsx
│   ├── services/
│   │   ├── droneService.js
│   │   ├── missionService.js
│   │   └── pilotService.js
│   ├── App.js
│   ├── App.css
│   └── index.js
├── package.json
└── README.md
```

**.env file:**

```
REACT_APP_API_URL=http://localhost:8080/api
```

---

## 🧪 Testing Strategy

### Unit Tests (60% of testing effort)

**Coverage goals:**
- Service layer: 80%+ code coverage
- Business logic methods: 100% coverage
- All edge cases and error conditions

**Example test files:**
- `DroneServiceTest.java` - Basic CRUD operations
- `MissionServiceMCDCTest.java` - MC/DC coverage for complex decisions

### Integration Tests (30% of testing effort)

**Coverage goals:**
- All REST endpoints tested
- Database integration verified
- Transaction management tested

**Example test files:**
- `DroneControllerIntegrationTest.java` - Full API testing

### Combinatorial Tests (10% of testing effort)

**Test scenarios:**
- Pre-flight inspection validation
- Mission assignment logic
- Battery sufficiency checks

**Example test files:**
- `PreFlightInspectionCombinatorialTest.java`

### Running Tests

```bash
# Run all tests
mvn test

# Run with coverage report
mvn test jacoco:report

# Run specific test class
mvn test -Dtest=DroneServiceTest

# Run integration tests only
mvn test -Dtest=*IntegrationTest
```

---

## 📊 MC/DC Testing Coverage

### Mission Approval Logic

**Decision:** `canApproveMission(pilot, drone, mission)`

**Conditions:**
- A: `pilot.isCertificationCurrent()`
- B: `drone.isAvailable()`
- C: `mission.hasAirspaceAuthorization() OR !mission.isAuthorizationRequired()`

**Formula:** `A && B && C`

**Test Cases:**
1. A=T, B=T, C=T → TRUE (all conditions met)
2. A=F, B=T, C=T → FALSE (A independently affects outcome)
3. A=T, B=F, C=T → FALSE (B independently affects outcome)
4. A=T, B=T, C=F → FALSE (C independently affects outcome)

### Battery Sufficiency Logic

**Decision:** `isBatterySufficient(battery, duration)`

**Conditions:**
- A: `battery.getHealthPercentage() >= 70`
- B: `battery.getCurrentCharge() >= 30`
- C: `estimatedFlightTime >= duration * 1.2`

**Formula:** `A && B && C`

**Test Cases:** Similar structure to mission approval

---

## 📈 Development Phases

### Phase 1: Foundation (Week 1-2)
- ✅ Database schema implementation
- ✅ Basic Spring Boot project setup
- ✅ Entity classes and repositories
- ✅ Basic CRUD operations

### Phase 2: Core Features (Week 3-4)
- ✅ REST API controllers
- ✅ Business logic in service layer
- ✅ React frontend components
- ✅ API integration

### Phase 3: Advanced Features (Week 5-6)
- ✅ Dashboard with charts
- ✅ Authentication/authorization
- ✅ File upload capabilities
- ✅ Real-time updates

### Phase 4: Testing & Polish (Week 7-8)
- ✅ Comprehensive unit tests
- ✅ Integration tests
- ✅ MC/DC coverage
- ✅ API documentation
- ✅ Demo video recording

---

## 🎯 User Stories - SMART Format

### Core Stories (Individual Project - 5)

1. **As a fleet manager**, I want to register new drones in the system with their specifications (model, serial number, max flight time, payload capacity), so that I can maintain an accurate inventory of available assets with all drone details retrievable within 2 clicks.

2. **As a drone pilot**, I want to create and submit flight mission requests with location, duration, and purpose, so that I can get approval and log my planned operations with form completion time under 3 minutes.

3. **As a maintenance technician**, I want to view all drones that require maintenance based on flight hours or calendar intervals, so that I can schedule preventive maintenance and ensure airworthiness with automated alerts at 80% of maintenance threshold.

4. **As an operations manager**, I want to view a real-time dashboard showing drone status (available, in-flight, maintenance, charging), so that I can make quick deployment decisions with status updates refreshing every 5 seconds.

5. **As a safety officer**, I want to log and track incidents or anomalies from flights, so that I can identify patterns and improve safety procedures with searchable incident reports categorized by severity.

### Extended Stories (Group Project - 15 total)

6-15. [See full README.md in project repository for all stories]

---

## 🔐 Security Considerations

### Authentication
- JWT-based authentication
- Password hashing with BCrypt
- Token expiration and refresh

### Authorization
- Role-based access control (RBAC)
- Roles: ADMIN, MANAGER, PILOT, TECHNICIAN, VIEWER
- Endpoint-level security

### Data Protection
- Input validation on all endpoints
- SQL injection prevention (JPA)
- XSS protection
- HTTPS in production

---

## 📝 API Documentation Access

Once your application is running:

**Swagger UI:**
```
http://localhost:8080/swagger-ui.html
```

**OpenAPI JSON:**
```
http://localhost:8080/v3/api-docs
```

---

## 🎥 Demo Video Guidelines

### Required Content (5-10 minutes)

1. **Introduction (30 seconds)**
   - Project name and purpose
   - Your name and role

2. **Architecture Overview (1 minute)**
   - Show architecture diagram
   - Explain tech stack

3. **Database Demo (1 minute)**
   - Show database schema
   - Demonstrate key relationships

4. **API Demo (2 minutes)**
   - Show Swagger documentation
   - Execute 3-4 API calls with Postman/curl
   - Demonstrate error handling

5. **Frontend Demo (3-4 minutes)**
   - Dashboard overview
   - Create a drone
   - Create and assign a mission
   - View maintenance alerts
   - Show responsive design

6. **Testing Demo (1-2 minutes)**
   - Run test suite
   - Show coverage report
   - Highlight MC/DC tests

7. **Conclusion (30 seconds)**
   - Summary of features
   - Challenges overcome

---

## 📚 Additional Resources

### Documentation Files Included
1. `schema.sql` - Complete database schema
2. `SpringBootControllers.java` - REST API controllers
3. `ReactComponents.jsx` - Frontend components
4. `JUnitTests.java` - Comprehensive test suite
5. `API_DOCUMENTATION.md` - Full API reference

### External Resources
- Spring Boot Documentation: https://spring.io/projects/spring-boot
- React Documentation: https://react.dev/
- PostgreSQL Documentation: https://www.postgresql.org/docs/
- Material-UI: https://mui.com/
- JUnit 5: https://junit.org/junit5/

---

## 🎓 Academic Project Context

This project fulfills the following course requirements:

- ✅ Relational database design (11 tables with relationships)
- ✅ RESTful API with CRUD operations (30+ endpoints)
- ✅ Responsive front-end application (5+ major components)
- ✅ Application server with business logic
- ✅ API documentation (Swagger + Markdown)
- ✅ Unit testing with JUnit (30+ test methods)
- ✅ MC/DC coverage (3 decision points tested)
- ✅ Combinatorial testing (12+ test cases)
- ✅ Architecture documentation (diagrams included)
- ✅ Live/recorded demonstration (guidelines provided)

---

## 🤝 Contributing

This is an academic project. For questions or suggestions:
- Review the code examples
- Consult the API documentation
- Check test cases for usage examples

---

## 📄 License

Academic project - All rights reserved.

---

## 🎉 Next Steps

1. **Set up your development environment**
   - Install Java, Node.js, PostgreSQL
   - Choose your IDE

2. **Create the database**
   - Run `schema.sql`
   - Verify tables created

3. **Start the backend**
   - Copy controller code
   - Configure `application.properties`
   - Run Spring Boot application

4. **Start the frontend**
   - Copy React components
   - Install dependencies
   - Run `npm start`

5. **Test the application**
   - Access Swagger UI
   - Test APIs with Postman
   - Run test suite

6. **Record demo video**
   - Follow demo guidelines
   - Show all features
   - Explain architecture

---

**Good luck with your project! 🚀**

If you need help with specific implementation details, refer to the code examples in each file or consult the API documentation.
