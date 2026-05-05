# SkyControl Pro - Database Architecture
## JPA-Based PostgreSQL Backend (SQL-less)

---

## 🎯 **Architecture Overview**

SkyControl Pro uses a **modern JPA (Java Persistence API)** approach with Spring Data JPA, eliminating the need for manual SQL queries. The database schema is **auto-generated from JPA entity annotations**.

### **Technology Stack**
- **ORM**: Spring Data JPA (Hibernate implementation)
- **Database**: PostgreSQL 14+ (Production) / H2 (Testing)
- **Schema Management**: JPA auto-generation (`spring.jpa.hibernate.ddl-auto=update`)
- **Query Language**: JPQL (Java Persistence Query Language) + Method Query Derivation

---

## 📊 **Entity Model (3 Core Tables)**

### **1. Drone Entity**

```java
@Entity
@Table(name = "drones")
public class Drone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String serialNumber;
    
    @Column(nullable = false)
    private String model;
    
    @Column(nullable = false)
    private String manufacturer;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DroneStatus status;  // AVAILABLE, IN_FLIGHT, MAINTENANCE, CHARGING, OFFLINE
    
    private Integer batteryLevel;
    
    private Integer flightHours;
    
    @OneToMany(mappedBy = "drone")
    private List<Mission> missions;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

**Generated PostgreSQL Table:**
```sql
CREATE TABLE drones (
    id BIGSERIAL PRIMARY KEY,
    serial_number VARCHAR(255) UNIQUE NOT NULL,
    model VARCHAR(255) NOT NULL,
    manufacturer VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    battery_level INTEGER,
    flight_hours INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

---

### **2. Mission Entity**

```java
@Entity
@Table(name = "missions")
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    private MissionType type;  // SURVEILLANCE, DELIVERY, INSPECTION, etc.
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionStatus status;  // PLANNED, IN_PROGRESS, COMPLETED, CANCELLED
    
    private LocalDateTime scheduledStartTime;
    
    private LocalDateTime scheduledEndTime;
    
    private LocalDateTime actualStartTime;
    
    private LocalDateTime actualEndTime;
    
    private String location;
    
    @ManyToOne
    @JoinColumn(name = "drone_id")
    private Drone drone;
    
    @ManyToOne
    @JoinColumn(name = "pilot_id")
    private Pilot pilot;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

**Generated PostgreSQL Table:**
```sql
CREATE TABLE missions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(50),
    status VARCHAR(50) NOT NULL,
    scheduled_start_time TIMESTAMP,
    scheduled_end_time TIMESTAMP,
    actual_start_time TIMESTAMP,
    actual_end_time TIMESTAMP,
    location VARCHAR(255),
    drone_id BIGINT REFERENCES drones(id),
    pilot_id BIGINT REFERENCES pilots(id),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

---

### **3. Pilot Entity**

```java
@Entity
@Table(name = "pilots")
public class Pilot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(unique = true, nullable = false)
    private String licenseNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PilotStatus status;  // ACTIVE, INACTIVE, ON_LEAVE
    
    private Integer totalFlightHours;
    
    @OneToMany(mappedBy = "pilot")
    private List<Mission> missions;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

**Generated PostgreSQL Table:**
```sql
CREATE TABLE pilots (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    license_number VARCHAR(255) UNIQUE NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_flight_hours INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

---

## 🔗 **Entity Relationships**

```
Drone (1) ←→ (Many) Mission
Pilot (1) ←→ (Many) Mission

Mission.drone → Drone (Many-to-One)
Mission.pilot → Pilot (Many-to-One)
Drone.missions → List<Mission> (One-to-Many)
Pilot.missions → List<Mission> (One-to-Many)
```

**Database Diagram:**
```
┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│   DRONES     │         │   MISSIONS   │         │   PILOTS     │
├──────────────┤         ├──────────────┤         ├──────────────┤
│ id (PK)      │◄───────┤ drone_id(FK) │         │ id (PK)      │
│ serial_number│         │ pilot_id(FK) ├────────►│ first_name   │
│ model        │         │ name         │         │ last_name    │
│ manufacturer │         │ type         │         │ email        │
│ status       │         │ status       │         │ license_num  │
│ battery_level│         │ location     │         │ status       │
│ flight_hours │         │ start_time   │         │ flight_hours │
└──────────────┘         └──────────────┘         └──────────────┘
```

---

## 🗄️ **Repository Layer (SQL-less Queries)**

### **Spring Data JPA Repository Pattern**

All database operations use **method name query derivation** - no SQL required!

```java
@Repository
public interface DroneRepository extends JpaRepository<Drone, Long> {
    
    // Automatically generates: SELECT * FROM drones WHERE serial_number = ?
    Optional<Drone> findBySerialNumber(String serialNumber);
    
    // Automatically generates: SELECT EXISTS(SELECT 1 FROM drones WHERE serial_number = ?)
    boolean existsBySerialNumber(String serialNumber);
    
    // Automatically generates: SELECT * FROM drones WHERE status = ?
    List<Drone> findByStatus(DroneStatus status);
    
    // Automatically generates: SELECT * FROM drones WHERE battery_level >= ?
    List<Drone> findByBatteryLevelGreaterThanEqual(Integer level);
    
    // Complex query with multiple conditions
    List<Drone> findByStatusAndBatteryLevelGreaterThan(
        DroneStatus status, Integer batteryLevel);
}

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {
    
    // Find by relationships (JOIN queries auto-generated)
    List<Mission> findByDroneId(Long droneId);
    
    List<Mission> findByPilotId(Long pilotId);
    
    List<Mission> findByStatus(MissionStatus status);
    
    // Date range queries
    List<Mission> findByScheduledStartTimeBetween(
        LocalDateTime start, LocalDateTime end);
    
    // Custom query if needed (still JPA, not raw SQL)
    @Query("SELECT m FROM Mission m WHERE m.status = :status " +
           "AND m.scheduledStartTime >= :date")
    List<Mission> findUpcomingMissions(
        @Param("status") MissionStatus status,
        @Param("date") LocalDateTime date);
}

@Repository
public interface PilotRepository extends JpaRepository<Pilot, Long> {
    
    Optional<Pilot> findByEmail(String email);
    
    Optional<Pilot> findByLicenseNumber(String licenseNumber);
    
    boolean existsByEmail(String email);
    
    boolean existsByLicenseNumber(String licenseNumber);
    
    List<Pilot> findByStatus(PilotStatus status);
    
    List<Pilot> findByTotalFlightHoursGreaterThan(Integer hours);
}
```

---

## ⚙️ **Configuration**

### **application.properties (Production)**

```properties
# PostgreSQL Database Connection
spring.datasource.url=jdbc:postgresql://localhost:5432/skycontrol
spring.datasource.username=skycontrol_user
spring.datasource.password=secure_password

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Automatically create/update schema from entities
spring.jpa.generate-ddl=true
```

### **application-test.properties (Testing)**

```properties
# H2 In-Memory Database for Tests
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver

# JPA Test Configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
```

---

## 🎯 **Benefits of SQL-less Approach**

### **Advantages:**

✅ **Type Safety**
- Compile-time checking of queries
- No SQL syntax errors at runtime
- Refactoring support in IDE

✅ **Database Independence**
- Switch between PostgreSQL/H2/MySQL without code changes
- Same code works in testing and production

✅ **Automatic Schema Management**
- Entities define the schema
- Hibernate auto-generates DDL
- No manual SQL migration scripts

✅ **Reduced Boilerplate**
- No RowMapper implementations
- No manual ResultSet processing
- No SQL string concatenation

✅ **Object-Oriented**
- Work with Java objects, not database rows
- Relationships handled automatically
- Lazy/eager loading support

### **Method Query Derivation Examples:**

```java
// Spring Data JPA automatically converts method names to SQL:

findBySerialNumber(String sn)
→ SELECT * FROM drones WHERE serial_number = ?

findByStatus(DroneStatus status)
→ SELECT * FROM drones WHERE status = ?

findByBatteryLevelGreaterThan(Integer level)
→ SELECT * FROM drones WHERE battery_level > ?

findByStatusAndBatteryLevelGreaterThanEqual(DroneStatus status, Integer level)
→ SELECT * FROM drones WHERE status = ? AND battery_level >= ?

findByDroneIdAndStatusIn(Long droneId, List<MissionStatus> statuses)
→ SELECT * FROM missions WHERE drone_id = ? AND status IN (?, ?, ?)
```

---

## 📈 **Schema Evolution**

### **How Schema Updates Work:**

With `spring.jpa.hibernate.ddl-auto=update`:

1. **Application starts**
2. **Hibernate scans** all `@Entity` classes
3. **Compares** entities to existing database tables
4. **Automatically adds** new columns/tables
5. **Never deletes** existing data

### **Example Evolution:**

**Step 1 - Initial Drone entity:**
```java
@Entity
public class Drone {
    private Long id;
    private String serialNumber;
    private String model;
}
```
**Generated:** `CREATE TABLE drones (id, serial_number, model)`

**Step 2 - Add status field:**
```java
@Entity
public class Drone {
    private Long id;
    private String serialNumber;
    private String model;
    private DroneStatus status;  // NEW FIELD
}
```
**Generated:** `ALTER TABLE drones ADD COLUMN status VARCHAR(50)`

**Step 3 - Add timestamps:**
```java
@Entity
public class Drone {
    private Long id;
    private String serialNumber;
    private String model;
    private DroneStatus status;
    @CreatedDate
    private LocalDateTime createdAt;  // NEW FIELD
    @LastModifiedDate
    private LocalDateTime updatedAt;  // NEW FIELD
}
```
**Generated:** 
```sql
ALTER TABLE drones ADD COLUMN created_at TIMESTAMP;
ALTER TABLE drones ADD COLUMN updated_at TIMESTAMP;
```

---

## 🔍 **Query Examples**

### **Service Layer - No SQL Required:**

```java
@Service
public class DroneService {
    
    @Autowired
    private DroneRepository droneRepository;
    
    // Get all available drones with battery > 50%
    public List<Drone> getReadyDrones() {
        return droneRepository.findByStatusAndBatteryLevelGreaterThan(
            DroneStatus.AVAILABLE, 50);
    }
    
    // Find drone by serial number
    public Drone findBySerial(String serialNumber) {
        return droneRepository.findBySerialNumber(serialNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Drone not found"));
    }
    
    // Check if serial number already exists
    public void validateUniqueSerial(String serialNumber) {
        if (droneRepository.existsBySerialNumber(serialNumber)) {
            throw new DuplicateResourceException("Serial number already exists");
        }
    }
}
```

---

## 🧪 **Testing Approach**

### **Unit Tests** (Mockito - No Database)
```java
@Test
void testGetAllDrones() {
    when(droneRepository.findAll()).thenReturn(List.of(testDrone));
    
    List<DroneDTO> result = droneService.getAllDrones();
    
    assertEquals(1, result.size());
    verify(droneRepository, times(1)).findAll();
}
```

### **Integration Tests** (H2 - Real Database)
```java
@SpringBootTest
@Transactional
class DroneControllerIntegrationTest {
    
    @Autowired
    private DroneRepository droneRepository;
    
    @Test
    void testDronePersistence() {
        Drone drone = new Drone();
        drone.setSerialNumber("TEST-001");
        droneRepository.save(drone);
        
        Drone found = droneRepository.findBySerialNumber("TEST-001").get();
        assertEquals("TEST-001", found.getSerialNumber());
    }
}
```

---

## 📚 **Documentation Summary**

### **What We Use:**
- ✅ Spring Data JPA (Hibernate)
- ✅ PostgreSQL (Production)
- ✅ H2 (Testing)
- ✅ Method Query Derivation
- ✅ JPA Annotations
- ✅ Auto-generated Schema

### **What We DON'T Use:**
- ❌ Manual SQL queries
- ❌ JDBC Templates
- ❌ SQL migration scripts (Flyway/Liquibase)
- ❌ Stored procedures
- ❌ Native SQL

### **Result:**
A clean, maintainable, type-safe backend with **zero SQL code** in the application layer!

---

## 🎓 **For Your Technical Report**

**Recommended Database Section:**

```markdown
## Database Architecture

SkyControl Pro implements a modern persistence layer using Spring Data JPA 
with PostgreSQL, following the SQL-less repository pattern.

### Entity Model
The system manages three core entities:
- Drones: Fleet asset tracking with status and battery monitoring
- Missions: Operation planning and execution tracking
- Pilots: Personnel management and assignment

### Repository Pattern
All database operations use Spring Data JPA's method query derivation, 
eliminating manual SQL and providing compile-time type safety. Complex 
queries are automatically generated from method names, ensuring database 
independence and reducing maintenance overhead.

### Schema Management
The database schema is auto-generated from JPA entity annotations using 
Hibernate's DDL auto-generation feature. This approach ensures the 
database structure always matches the entity model and simplifies 
development and testing workflows.

### Technology Stack
- ORM: Spring Data JPA (Hibernate)
- Production Database: PostgreSQL 14+
- Testing Database: H2 (in-memory)
- Query Strategy: Method name derivation + JPQL
```

---

**This is your ACTUAL architecture - modern, clean, and SQL-less!** 🚀

