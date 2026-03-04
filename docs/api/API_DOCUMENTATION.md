# SkyControl Pro - API Documentation

## Table of Contents
1. [Overview](#overview)
2. [Authentication](#authentication)
3. [Base URL](#base-url)
4. [Response Format](#response-format)
5. [Error Handling](#error-handling)
6. [Rate Limiting](#rate-limiting)
7. [API Endpoints](#api-endpoints)
   - [Drones](#drones-api)
   - [Missions](#missions-api)
   - [Pilots](#pilots-api)
   - [Batteries](#batteries-api)
   - [Maintenance](#maintenance-api)
   - [Pre-Flight Inspections](#pre-flight-inspections-api)
   - [Incidents](#incidents-api)
   - [Dashboard](#dashboard-api)

---

## Overview

The SkyControl Pro API is a RESTful API that allows you to manage commercial drone operations including fleet management, mission planning, pilot tracking, and regulatory compliance.

**API Version:** 1.0  
**Last Updated:** February 2024

---

## Authentication

The API uses JWT (JSON Web Token) for authentication.

### Obtaining a Token

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "yourpassword"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "user": {
    "id": 1,
    "username": "user@example.com",
    "role": "MANAGER"
  }
}
```

### Using the Token

Include the token in the `Authorization` header for all subsequent requests:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## Base URL

**Development:** `http://localhost:8080/api`  
**Production:** `https://api.skycontrol.com/api`

---

## Response Format

All API responses follow a consistent JSON format.

### Success Response

```json
{
  "droneId": 1,
  "model": "Mavic 3 Enterprise",
  "serialNumber": "DJI-M3E-001",
  "currentStatus": "available"
}
```

### Paginated Response

```json
{
  "content": [...],
  "page": 0,
  "size": 10,
  "totalElements": 45,
  "totalPages": 5,
  "last": false
}
```

---

## Error Handling

### Error Response Format

```json
{
  "status": 404,
  "message": "Drone with ID 999 not found",
  "timestamp": "2024-02-16T10:30:00"
}
```

### HTTP Status Codes

| Code | Description |
|------|-------------|
| 200 | OK - Request succeeded |
| 201 | Created - Resource created successfully |
| 204 | No Content - Request succeeded with no response body |
| 400 | Bad Request - Invalid input |
| 401 | Unauthorized - Authentication required |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 409 | Conflict - Duplicate resource |
| 500 | Internal Server Error - Server error |

### Common Error Messages

```json
// Validation Error
{
  "serialNumber": "Serial number is required",
  "maxFlightTimeMinutes": "Must be greater than 0"
}

// Not Found
{
  "status": 404,
  "message": "Drone with ID 123 not found",
  "timestamp": "2024-02-16T10:30:00"
}

// Duplicate Resource
{
  "status": 409,
  "message": "Drone with serial number DJI-M3E-001 already exists",
  "timestamp": "2024-02-16T10:30:00"
}
```

---

## Rate Limiting

API requests are rate-limited to ensure fair usage:

- **Authenticated requests:** 1000 requests per hour
- **Unauthenticated requests:** 100 requests per hour

Rate limit headers are included in every response:

```
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 995
X-RateLimit-Reset: 1708099200
```

---

## API Endpoints

---

## Drones API

### Get All Drones

Retrieve a paginated list of all drones with optional filtering.

```http
GET /api/drones
```

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| status | string | No | Filter by status (available, in_flight, maintenance, charging, retired) |
| manufacturer | string | No | Filter by manufacturer |
| model | string | No | Filter by model |
| page | integer | No | Page number (default: 0) |
| size | integer | No | Page size (default: 10) |
| sort | string | No | Sort field (e.g., "serialNumber,asc") |

**Example Request:**

```bash
curl -X GET "http://localhost:8080/api/drones?status=available&page=0&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Example Response:**

```json
{
  "content": [
    {
      "droneId": 1,
      "model": "Mavic 3 Enterprise",
      "manufacturer": "DJI",
      "serialNumber": "DJI-M3E-001",
      "registrationNumber": "FA3X5Y7Z9",
      "purchaseDate": "2023-01-15",
      "maxFlightTimeMinutes": 45,
      "maxRangeMeters": 15000,
      "maxAltitudeMeters": 6000,
      "payloadCapacityKg": 0.9,
      "currentStatus": "available",
      "totalFlightHours": 125.5,
      "flightHoursSinceMaintenance": 35.2,
      "lastMaintenanceDate": "2024-01-15",
      "nextMaintenanceDueDate": "2024-03-15",
      "createdAt": "2023-01-15T10:00:00",
      "updatedAt": "2024-02-10T14:30:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1,
  "last": true
}
```

---

### Get Drone by ID

Retrieve detailed information about a specific drone.

```http
GET /api/drones/{id}
```

**Path Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | integer | Yes | Drone ID |

**Example Request:**

```bash
curl -X GET "http://localhost:8080/api/drones/1" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Example Response:**

```json
{
  "droneId": 1,
  "model": "Mavic 3 Enterprise",
  "manufacturer": "DJI",
  "serialNumber": "DJI-M3E-001",
  "registrationNumber": "FA3X5Y7Z9",
  "purchaseDate": "2023-01-15",
  "maxFlightTimeMinutes": 45,
  "maxRangeMeters": 15000,
  "maxAltitudeMeters": 6000,
  "payloadCapacityKg": 0.9,
  "cameraSpecs": {
    "sensor": "4/3 CMOS",
    "resolution": "20MP",
    "videoResolution": "5.1K"
  },
  "currentStatus": "available",
  "totalFlightHours": 125.5,
  "flightHoursSinceMaintenance": 35.2,
  "lastMaintenanceDate": "2024-01-15",
  "nextMaintenanceDueHours": 50.0,
  "nextMaintenanceDueDate": "2024-03-15",
  "notes": "Primary inspection drone",
  "createdAt": "2023-01-15T10:00:00",
  "updatedAt": "2024-02-10T14:30:00"
}
```

**Error Responses:**

```json
// 404 Not Found
{
  "status": 404,
  "message": "Drone with ID 999 not found",
  "timestamp": "2024-02-16T10:30:00"
}
```

---

### Create Drone

Register a new drone in the fleet.

```http
POST /api/drones
```

**Request Body:**

```json
{
  "model": "Matrice 300 RTK",
  "manufacturer": "DJI",
  "serialNumber": "DJI-M300-002",
  "registrationNumber": "FA4A8B2C6",
  "purchaseDate": "2024-02-15",
  "maxFlightTimeMinutes": 55,
  "maxRangeMeters": 15000,
  "maxAltitudeMeters": 7000,
  "payloadCapacityKg": 2.7,
  "cameraSpecs": {
    "sensor": "Full Frame",
    "resolution": "45MP",
    "videoResolution": "8K"
  },
  "notes": "Heavy-lift inspection drone"
}
```

**Required Fields:**
- model (string)
- manufacturer (string)
- serialNumber (string, unique)
- purchaseDate (date)
- maxFlightTimeMinutes (integer > 0)
- maxRangeMeters (integer > 0)
- maxAltitudeMeters (integer > 0)
- payloadCapacityKg (decimal >= 0)

**Example Request:**

```bash
curl -X POST "http://localhost:8080/api/drones" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "Matrice 300 RTK",
    "manufacturer": "DJI",
    "serialNumber": "DJI-M300-002",
    "purchaseDate": "2024-02-15",
    "maxFlightTimeMinutes": 55,
    "maxRangeMeters": 15000,
    "maxAltitudeMeters": 7000,
    "payloadCapacityKg": 2.7
  }'
```

**Example Response (201 Created):**

```json
{
  "droneId": 5,
  "model": "Matrice 300 RTK",
  "manufacturer": "DJI",
  "serialNumber": "DJI-M300-002",
  "registrationNumber": "FA4A8B2C6",
  "purchaseDate": "2024-02-15",
  "maxFlightTimeMinutes": 55,
  "maxRangeMeters": 15000,
  "maxAltitudeMeters": 7000,
  "payloadCapacityKg": 2.7,
  "currentStatus": "available",
  "totalFlightHours": 0.0,
  "flightHoursSinceMaintenance": 0.0,
  "createdAt": "2024-02-16T10:30:00",
  "updatedAt": "2024-02-16T10:30:00"
}
```

**Error Responses:**

```json
// 400 Bad Request - Validation Error
{
  "serialNumber": "Serial number is required",
  "maxFlightTimeMinutes": "Must be greater than 0"
}

// 409 Conflict - Duplicate Serial Number
{
  "status": 409,
  "message": "Drone with serial number DJI-M300-002 already exists",
  "timestamp": "2024-02-16T10:30:00"
}
```

---

### Update Drone

Update an existing drone's information.

```http
PUT /api/drones/{id}
```

**Path Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | integer | Yes | Drone ID |

**Request Body:**

```json
{
  "registrationNumber": "FA4A8B2C6-UPDATED",
  "notes": "Updated maintenance notes",
  "nextMaintenanceDueHours": 50.0
}
```

**Example Response (200 OK):**

```json
{
  "droneId": 1,
  "model": "Mavic 3 Enterprise",
  "manufacturer": "DJI",
  "serialNumber": "DJI-M3E-001",
  "registrationNumber": "FA4A8B2C6-UPDATED",
  "currentStatus": "available",
  "notes": "Updated maintenance notes",
  "updatedAt": "2024-02-16T10:35:00"
}
```

---

### Update Drone Status

Change the current status of a drone.

```http
PATCH /api/drones/{id}/status?status={status}
```

**Path Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | integer | Yes | Drone ID |

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| status | string | Yes | New status (available, in_flight, maintenance, charging, retired) |

**Example Request:**

```bash
curl -X PATCH "http://localhost:8080/api/drones/1/status?status=maintenance" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Example Response (200 OK):**

```json
{
  "droneId": 1,
  "serialNumber": "DJI-M3E-001",
  "currentStatus": "maintenance",
  "updatedAt": "2024-02-16T10:40:00"
}
```

---

### Delete Drone

Retire a drone from the fleet.

```http
DELETE /api/drones/{id}
```

**Example Request:**

```bash
curl -X DELETE "http://localhost:8080/api/drones/1" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Example Response (204 No Content)**

---

### Get Drone Maintenance History

Retrieve maintenance records for a specific drone.

```http
GET /api/drones/{id}/maintenance
```

**Example Response:**

```json
[
  {
    "maintenanceId": 1,
    "droneId": 1,
    "maintenanceType": "scheduled",
    "scheduledDate": "2024-01-15",
    "completedDate": "2024-01-15",
    "technicianName": "John Smith",
    "workPerformed": "50-hour inspection, replaced propellers",
    "partsReplaced": [
      {
        "partName": "Propeller Set",
        "quantity": 1
      }
    ],
    "totalCost": 150.00,
    "status": "completed"
  }
]
```

---

### Get Drone Flight History

Retrieve flight logs for a specific drone.

```http
GET /api/drones/{id}/flights?page=0&size=10
```

**Example Response:**

```json
{
  "content": [
    {
      "logId": 1,
      "missionId": 5,
      "droneId": 1,
      "pilotId": 2,
      "takeoffTime": "2024-02-15T09:00:00",
      "landingTime": "2024-02-15T09:45:00",
      "flightDurationMinutes": 45.0,
      "maxAltitudeReached": 350,
      "distanceTraveled": 2500,
      "batteryUsedPercentage": 65
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1
}
```

---

### Get Available Drones

Retrieve all drones with 'available' status.

```http
GET /api/drones/available
```

**Example Response:**

```json
[
  {
    "droneId": 1,
    "model": "Mavic 3 Enterprise",
    "serialNumber": "DJI-M3E-001",
    "currentStatus": "available",
    "totalFlightHours": 125.5
  },
  {
    "droneId": 2,
    "model": "Matrice 300 RTK",
    "serialNumber": "DJI-M300-001",
    "currentStatus": "available",
    "totalFlightHours": 85.2
  }
]
```

---

### Get Drones Needing Maintenance

Retrieve drones that need maintenance soon or are overdue.

```http
GET /api/drones/maintenance-due
```

**Example Response:**

```json
[
  {
    "droneId": 3,
    "model": "Phantom 4 RTK",
    "serialNumber": "DJI-P4R-001",
    "flightHoursSinceMaintenance": 48.5,
    "nextMaintenanceDueHours": 50.0,
    "lastMaintenanceDate": "2023-12-01",
    "nextMaintenanceDueDate": "2024-02-01",
    "maintenanceUrgency": "DUE_SOON"
  }
]
```

---

## Missions API

### Get All Missions

```http
GET /api/missions
```

**Query Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| status | string | Filter by status |
| type | string | Filter by mission type |
| startDate | date | Filter by start date (ISO 8601) |
| endDate | date | Filter by end date (ISO 8601) |
| pilotId | integer | Filter by assigned pilot |
| droneId | integer | Filter by assigned drone |
| page | integer | Page number |
| size | integer | Page size |

**Example Request:**

```bash
curl -X GET "http://localhost:8080/api/missions?status=approved&page=0" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### Create Mission

```http
POST /api/missions
```

**Request Body:**

```json
{
  "missionName": "Cell Tower Inspection - Site B",
  "missionType": "inspection",
  "description": "Annual structural inspection of cell tower",
  "requestedBy": "TechCom Solutions",
  "flightLocation": "456 Tower Rd, Springfield",
  "latitude": 39.7817,
  "longitude": -89.6501,
  "plannedDate": "2024-02-20",
  "plannedStartTime": "09:00",
  "estimatedDurationMinutes": 45,
  "clientName": "TechCom Solutions",
  "costEstimate": 500.00,
  "maxAltitudePlanned": 400,
  "airspaceAuthorizationRequired": true
}
```

**Example Response (201 Created):**

```json
{
  "missionId": 10,
  "missionName": "Cell Tower Inspection - Site B",
  "missionType": "inspection",
  "status": "requested",
  "requestedBy": "TechCom Solutions",
  "flightLocation": "456 Tower Rd, Springfield",
  "plannedDate": "2024-02-20",
  "createdAt": "2024-02-16T11:00:00"
}
```

---

### Approve Mission

```http
PATCH /api/missions/{id}/approve?approvedBy=Manager
```

---

### Assign Mission

```http
PATCH /api/missions/{id}/assign?pilotId=2&droneId=1
```

---

## Pilots API

### Get All Pilots

```http
GET /api/pilots?status=active
```

---

### Get Available Pilots

Retrieve pilots with current certifications.

```http
GET /api/pilots/available
```

**Example Response:**

```json
[
  {
    "pilotId": 1,
    "firstName": "Sarah",
    "lastName": "Johnson",
    "email": "sarah.johnson@skycontrol.com",
    "part107CertificationNumber": "P107-123456",
    "certificationExpiryDate": "2025-01-15",
    "totalFlightHours": 450.5,
    "status": "active",
    "certificationStatus": "CURRENT"
  }
]
```

---

## Dashboard API

### Get Dashboard Overview

```http
GET /api/dashboard
```

**Example Response:**

```json
{
  "totalDrones": 15,
  "activeMissions": 3,
  "availablePilots": 8,
  "maintenanceDue": 2,
  "fleetStatus": {
    "available": 10,
    "in_flight": 3,
    "maintenance": 2,
    "charging": 0,
    "retired": 0
  },
  "upcomingMissions": [...],
  "maintenanceAlerts": [...],
  "recentFlights": [...]
}
```

---

## Swagger/OpenAPI Configuration

### Spring Boot Configuration

```java
package com.skycontrol.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI skyControlAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("SkyControl Pro API")
                .description("Comprehensive Drone Fleet Management API")
                .version("1.0.0")
                .contact(new Contact()
                    .name("SkyControl Support")
                    .email("support@skycontrol.com")
                    .url("https://skycontrol.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
            .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
            .components(new Components()
                .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .in(SecurityScheme.In.HEADER)
                    .name("Authorization")));
    }
}
```

### Access Swagger UI

Once the application is running, access the Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

Or the OpenAPI JSON specification at:

```
http://localhost:8080/v3/api-docs
```

---

## Postman Collection

Import this collection into Postman for easy API testing:

```json
{
  "info": {
    "name": "SkyControl Pro API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Authentication",
      "item": [
        {
          "name": "Login",
          "request": {
            "method": "POST",
            "header": [],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"username\": \"user@example.com\",\n  \"password\": \"password\"\n}",
              "options": {
                "raw": {
                  "language": "json"
                }
              }
            },
            "url": {
              "raw": "{{baseUrl}}/api/auth/login",
              "host": ["{{baseUrl}}"],
              "path": ["api", "auth", "login"]
            }
          }
        }
      ]
    },
    {
      "name": "Drones",
      "item": [
        {
          "name": "Get All Drones",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{token}}",
                "type": "text"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/api/drones",
              "host": ["{{baseUrl}}"],
              "path": ["api", "drones"]
            }
          }
        }
      ]
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080"
    },
    {
      "key": "token",
      "value": ""
    }
  ]
}
```

---

## Support

For questions or issues with the API:

- **Email:** support@skycontrol.com
- **Documentation:** https://docs.skycontrol.com
- **Status Page:** https://status.skycontrol.com

---

**Last Updated:** February 16, 2024  
**API Version:** 1.0.0
