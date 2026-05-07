# Festival Pulse — System Documentation

## Table of Contents

1. [Overview](#overview)
2. [Tech Stack](#tech-stack)
3. [System Architecture](#system-architecture)
4. [Backend](#backend)
   - [Project Structure](#backend-project-structure)
   - [Domain Model](#domain-model)
   - [Database Schema](#database-schema)
   - [REST API Endpoints](#rest-api-endpoints)
   - [Business Rules](#business-rules)
   - [Validation & Error Handling](#validation--error-handling)
   - [Configuration](#configuration)
5. [Frontend](#frontend)
   - [Project Structure](#frontend-project-structure)
   - [Components](#components)
   - [State Management](#state-management)
   - [API Client](#api-client)
   - [Types](#types)
6. [Seed Data](#seed-data)
7. [Running the Application](#running-the-application)

---

## Overview

Festival Pulse is a real-time crowd monitoring dashboard for music and food festivals. Festival stewards use it to report how busy different areas are. When an area becomes too crowded, the system automatically raises an alert for organisers to act on.

The system supports multiple festivals simultaneously. All data — areas, reports, and alerts — is scoped to a specific festival.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend framework | Spring Boot 4.0.6 |
| Language | Java 17 |
| Database | H2 in-memory |
| ORM | Spring Data JPA / Hibernate 7 |
| Validation | Jakarta Bean Validation |
| API docs | SpringDoc OpenAPI (Swagger UI) |
| Boilerplate reduction | Lombok |
| Frontend framework | React 19 + TypeScript |
| Build tool | Vite 8 |
| Canvas rendering | Konva / react-konva |
| State management | Zustand |
| Icons | Lucide React |

---

## System Architecture

```
┌─────────────────────────────────────────────────────┐
│                    Browser (React)                   │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────┐ │
│  │MapViewer │ │AlertPanel│ │ReportPanel│ │Builder │ │
│  └────┬─────┘ └────┬─────┘ └─────┬────┘ └───┬────┘ │
│       └────────────┴─────────────┴───────────┘      │
│                    Zustand Store                     │
│                    api.ts (fetch)                    │
└──────────────────────┬──────────────────────────────┘
                       │ HTTP/JSON  (port 3000 → proxy → 8080)
┌──────────────────────▼──────────────────────────────┐
│              Spring Boot REST API                    │
│  ┌────────────┐ ┌──────────┐ ┌──────────────────┐   │
│  │Controllers │→│ Services │→│  Repositories    │   │
│  └────────────┘ └──────────┘ └────────┬─────────┘   │
│                                        │ JPA/SQL     │
│                              ┌─────────▼──────────┐  │
│                              │  H2 In-Memory DB   │  │
│                              └────────────────────┘  │
└─────────────────────────────────────────────────────┘
```

The frontend runs on port **3000** with Vite's dev server proxying all `/api` requests to the Spring Boot backend on port **8080**. This means no CORS configuration is needed in development.

---

## Backend

### Backend Project Structure

```
src/main/java/com/ericsson/festivalpulse/
├── controller/
│   ├── FestivalController.java
│   ├── FestivalAreaController.java
│   ├── CrowdReportController.java
│   ├── CrowdAlertController.java
│   └── DashboardController.java
├── service/
│   ├── FestivalService.java
│   ├── FestivalAreaService.java
│   ├── CrowdReportService.java
│   ├── CrowdAlertService.java
│   └── DashboardService.java
├── repository/
│   ├── FestivalRepository.java
│   ├── FestivalAreaRepository.java
│   ├── CrowdReportRepository.java
│   └── CrowdAlertRepository.java
├── model/
│   ├── Festival.java
│   ├── FestivalArea.java
│   ├── CrowdReport.java
│   ├── CrowdAlert.java
│   ├── CrowdLevel.java        (enum: LOW, MEDIUM, FULL)
│   ├── AlertStatus.java       (enum: ACTIVE, RESOLVED)
│   └── DashboardSummary.java  (response DTO)
├── exception/
│   └── GlobalExceptionHandler.java
└── FestivalPulseApplication.java
```

---

### Domain Model

#### Festival
Represents a music or food festival event.

| Field | Type | Constraints |
|---|---|---|
| id | Long | Auto-generated primary key |
| name | String | Not blank |
| description | String | Optional |
| startDate | LocalDate | Optional |
| endDate | LocalDate | Optional |

#### FestivalArea
A named zone within a festival (e.g. Main Stage, Food Village).

| Field | Type | Constraints |
|---|---|---|
| id | Long | Auto-generated primary key |
| festival | Festival | Many-to-one, required |
| name | String | Not blank |
| description | String | Optional |
| areaType | String | Optional (Stage, Food & Drink, Medical, etc.) |
| coordinates | String | JSON array of `{x, y}` points for map rendering |
| baseColor | String | Hex colour string |

#### CrowdReport
A steward's report of how busy an area is at a point in time.

| Field | Type | Constraints |
|---|---|---|
| id | Long | Auto-generated primary key |
| area | FestivalArea | Many-to-one, required |
| crowdLevel | CrowdLevel | Enum — LOW, MEDIUM, FULL. Required |
| note | String | Optional short description |
| submittedAt | LocalDateTime | Set automatically on submission |

#### CrowdAlert
An alert raised automatically when a FULL crowd report is submitted.

| Field | Type | Constraints |
|---|---|---|
| id | Long | Auto-generated primary key |
| area | FestivalArea | Many-to-one, required |
| message | String | Auto-generated (e.g. "Main Stage is FULL!") |
| status | AlertStatus | Enum — ACTIVE, RESOLVED |
| createdAt | LocalDateTime | Set automatically on creation |

---

### Database Schema

```sql
TABLE festival (
  id          BIGINT IDENTITY PRIMARY KEY,
  name        VARCHAR(255) NOT NULL,
  description VARCHAR(255),
  start_date  DATE,
  end_date    DATE
)

TABLE festival_area (
  id          BIGINT IDENTITY PRIMARY KEY,
  festival_id BIGINT NOT NULL REFERENCES festival(id),
  name        VARCHAR(255) NOT NULL,
  description VARCHAR(255),
  area_type   VARCHAR(255),
  coordinates VARCHAR(10000),
  base_color  VARCHAR(255)
)

TABLE crowd_report (
  id           BIGINT IDENTITY PRIMARY KEY,
  area_id      BIGINT NOT NULL REFERENCES festival_area(id),
  crowd_level  ENUM('LOW','MEDIUM','FULL') NOT NULL,
  note         VARCHAR(255),
  submitted_at TIMESTAMP NOT NULL
)

TABLE crowd_alert (
  id         BIGINT IDENTITY PRIMARY KEY,
  area_id    BIGINT NOT NULL REFERENCES festival_area(id),
  message    VARCHAR(255) NOT NULL,
  status     ENUM('ACTIVE','RESOLVED') NOT NULL,
  created_at TIMESTAMP NOT NULL
)
```

**Relationships:**
- One `Festival` → many `FestivalArea`
- One `FestivalArea` → many `CrowdReport`
- One `FestivalArea` → many `CrowdAlert`

---

### REST API Endpoints

All endpoints are prefixed with `/api`. Festival-scoped endpoints require a `festivalId` path variable.

#### Festivals

| Method | Path | Description | Request Body | Response |
|---|---|---|---|---|
| `POST` | `/api/festivals` | Create a new festival | `{ name, description, startDate, endDate }` | `201 Festival` |
| `GET` | `/api/festivals` | List all festivals | — | `200 Festival[]` |

#### Festival Areas

| Method | Path | Description | Request Body | Response |
|---|---|---|---|---|
| `POST` | `/api/festivals/{festivalId}/areas` | Create an area in a festival | `{ name, description, areaType, coordinates, baseColor }` | `201 FestivalArea` |
| `GET` | `/api/festivals/{festivalId}/areas` | List all areas for a festival | — | `200 FestivalArea[]` |

#### Crowd Reports

| Method | Path | Description | Request Body | Response |
|---|---|---|---|---|
| `POST` | `/api/festivals/{festivalId}/reports` | Submit a crowd report | `{ areaId, crowdLevel, note }` | `201 CrowdReport` |
| `GET` | `/api/festivals/{festivalId}/reports` | Get 20 most recent reports | — | `200 CrowdReport[]` |

#### Crowd Alerts

| Method | Path | Description | Request Body | Response |
|---|---|---|---|---|
| `GET` | `/api/festivals/{festivalId}/alerts` | List active alerts for a festival | — | `200 CrowdAlert[]` |
| `PATCH` | `/api/festivals/{festivalId}/alerts/{id}/resolve` | Resolve an alert | — | `200 CrowdAlert` |

#### Dashboard

| Method | Path | Description | Response |
|---|---|---|---|
| `GET` | `/api/festivals/{festivalId}/dashboard` | Get summary for a festival | `200 DashboardSummary` |

**DashboardSummary response shape:**
```json
{
  "totalAreas": 10,
  "recentReports": [ ...CrowdReport[] ],
  "activeAlerts":  [ ...CrowdAlert[]  ]
}
```

#### Example Request/Response

**POST** `/api/festivals/1/reports`
```json
// Request
{
  "areaId": 3,
  "crowdLevel": "FULL",
  "note": "Queue backing up near the entrance"
}

// Response 201
{
  "id": 42,
  "area": {
    "id": 3,
    "name": "Food Village",
    "areaType": "Food & Drink"
  },
  "crowdLevel": "FULL",
  "note": "Queue backing up near the entrance",
  "submittedAt": "2025-08-29T14:32:00"
}
```

---

### Business Rules

| Rule | Where enforced |
|---|---|
| A crowd report must belong to an existing area | `CrowdReportService` — returns `404` if area not found |
| A report's area must belong to the festival in the path | `CrowdReportService` — filters by `festivalId` |
| Submitting a `FULL` report automatically creates an active alert | `CrowdReportService` → `CrowdAlertService.createAlertIfNotExists()` |
| No duplicate active alerts for the same area | `CrowdAlertService.createAlertIfNotExists()` — checks before creating |
| Cannot submit a report to an area that already has an active alert | `CrowdReportService` — returns `409 Conflict` |
| Resolved alerts do not appear in the active alerts list | `CrowdAlertRepository.findByAreaFestivalAndStatus(festival, ACTIVE)` |
| Resolving an alert must belong to the festival in the path | `CrowdAlertService.resolveAlert()` — validates `festivalId` match, returns `404` if not |

---

### Validation & Error Handling

All validation errors and exceptions return a consistent JSON error response:

```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "name": "Name is required",
    "crowdLevel": "Crowd level is required"
  },
  "timestamp": "2025-08-29T14:32:00"
}
```

| Scenario | HTTP Status |
|---|---|
| Missing required field | `400 Bad Request` |
| Festival / area / alert not found | `404 Not Found` |
| Submitting report to area with active alert | `409 Conflict` |
| Alert does not belong to the festival | `404 Not Found` |

Handled globally by `GlobalExceptionHandler` using `@RestControllerAdvice`.

---

### Configuration

**`application.yaml`**
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:festivalpulse
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop   # Schema recreated on every restart
    show-sql: true
    defer-datasource-initialization: true
  sql:
    init:
      mode: always            # Runs data.sql on startup
  h2:
    console:
      enabled: true           # http://localhost:8080/h2-console

springdoc:
  api-docs:
    version: openapi_3_0      # Swagger UI at http://localhost:8080/swagger-ui/index.html
```

---

## Frontend

### Frontend Project Structure

```
frontend/src/
├── components/
│   ├── MapBackground.tsx     Shared Konva grass/tree/stone background layer
│   ├── MapViewer.tsx         Monitor mode — interactive festival map
│   ├── MapBuilder.tsx        Builder mode — draw and save new areas
│   ├── AlertsPanel.tsx       Active alerts list with resolve button
│   ├── ReportPanel.tsx       Submit crowd report form + recent reports feed
│   ├── ReportDetailModal.tsx Modal showing full details of a crowd report
│   └── ResolveModal.tsx      Confirmation modal for resolving an alert
├── utils/
│   └── colors.ts             Crowd level colour constants and helpers
├── api.ts                    All fetch calls to the backend
├── store.ts                  Zustand global state
├── types.ts                  TypeScript interfaces
├── App.tsx                   Root component, layout, festival selector
├── App.css                   All component styles
└── main.tsx                  React entry point
```

---

### Components

#### `App.tsx`
The root component. Responsible for:
- Loading all festivals on mount
- Switching the active festival via a dropdown
- Loading areas, reports, and alerts when the festival changes
- Attaching the latest crowd level from reports to each area for map colouring
- Rendering the festival selector, mode toggle, map, stats, alerts panel, and report panel

#### `MapBackground.tsx`
A shared Konva `<Layer>` that renders the aerial grass field background used by both MapViewer and MapBuilder. Contains:
- Base green grass fill
- Lighter inner field ellipse
- Subtle contour blobs
- Perimeter stones
- 29 perimeter trees (3-circle layered canopy)

#### `MapViewer.tsx` — Monitor Mode
An interactive Konva canvas showing all festival areas colour-coded by crowd level.

- **Green** — LOW crowd level
- **Yellow** — MEDIUM crowd level
- **Orange** — FULL crowd level
- **Flashing red** — Active alert (flashes every 600ms between red and transparent)
- **White/neutral** — No report submitted yet

Clicking an area fetches its most recent report and opens the `ReportDetailModal`.

#### `MapBuilder.tsx` — Builder Mode
A Konva canvas for drawing new festival areas by clicking to place polygon points. When the area is complete (minimum 3 points), a styled modal prompts for the area name and type. The area is then saved to the backend.

#### `AlertsPanel.tsx`
Displays all active alerts for the current festival, sorted newest first. Each alert card shows:
- Pulsing red indicator dot
- Area name
- Alert message
- Time created

Clicking **Resolve** opens the `ResolveModal`.

#### `ReportPanel.tsx`
Two-column layout:
- **Left** — Submit crowd report form: area dropdown, LOW/MEDIUM/FULL level toggle buttons, optional note field. Blocked with a red error banner if the area already has an active alert.
- **Right** — Scrollable feed of the 20 most recent reports for the current festival. Each row shows the coloured level badge, area name, note, and time. Clicking a row opens the `ReportDetailModal`.

#### `ReportDetailModal.tsx`
A modal showing the full details of a crowd report:
- Crowd level badge (coloured)
- Area name and type
- Submitted timestamp
- Note
- Red alert banner if the area currently has an active alert

Triggered by clicking a report in the feed or clicking an area on the map.

#### `ResolveModal.tsx`
A confirmation modal shown before resolving an alert. The organiser must:
1. Confirm they want to resolve the alert
2. Select the new crowd level (LOW or MEDIUM)

On confirm, the system:
1. Resolves the alert via `PATCH /api/festivals/{id}/alerts/{id}/resolve`
2. Submits a follow-up crowd report at the selected level
3. Refreshes the areas so the map colour updates immediately

---

### State Management

Zustand store (`store.ts`) holds all global application state:

| State | Type | Description |
|---|---|---|
| `festivals` | `Festival[]` | All festivals loaded from backend |
| `currentFestival` | `Festival \| null` | Currently selected festival |
| `areas` | `FestivalArea[]` | Areas for the current festival, with crowd level attached |
| `selectedArea` | `FestivalArea \| null` | Area clicked on the map |
| `selectedReport` | `CrowdReport \| null` | Report open in the detail modal |
| `alerts` | `CrowdAlert[]` | Active alerts for the current festival |
| `viewMode` | `'monitor' \| 'builder'` | Which map mode is active |
| `isDrawing` | `boolean` | Whether the builder is in drawing mode |

---

### API Client

`api.ts` wraps all backend calls using the browser `fetch` API. All paths are relative (`/api/...`) and proxied by Vite to `http://localhost:8080`.

| Function | Method | Path |
|---|---|---|
| `getFestivals()` | GET | `/api/festivals` |
| `createFestival(data)` | POST | `/api/festivals` |
| `getAreas(festivalId)` | GET | `/api/festivals/{id}/areas` |
| `createArea(festivalId, data)` | POST | `/api/festivals/{id}/areas` |
| `getReports(festivalId)` | GET | `/api/festivals/{id}/reports` |
| `submitReport(festivalId, data)` | POST | `/api/festivals/{id}/reports` |
| `getAlerts(festivalId)` | GET | `/api/festivals/{id}/alerts` |
| `resolveAlert(festivalId, alertId)` | PATCH | `/api/festivals/{id}/alerts/{alertId}/resolve` |
| `getDashboard(festivalId)` | GET | `/api/festivals/{id}/dashboard` |

---

### Types

Defined in `types.ts`:

```typescript
Festival       { id, name, description, startDate, endDate }
FestivalArea   { id, festivalId, name, description, areaType, coordinates, baseColor, crowdLevel, hasAlert }
CrowdReport    { id, area, crowdLevel, note, submittedAt }
CrowdAlert     { id, area, message, status, createdAt }
DashboardSummary { totalAreas, recentReports, activeAlerts }
Point          { x, y }
ViewMode       'builder' | 'monitor'
```

---

## Seed Data

On startup, `data.sql` pre-populates the database with 4 real Irish festivals, 30 areas, 29 crowd reports, and 6 active alerts for testing.

| Festival | Areas | Active Alerts |
|---|---|---|
| Electric Picnic | 10 | Main Stage, Food Village |
| Longitude | 7 | Main Stage, Bar Village |
| Forbidden Fruit | 6 | The Greenhouse |
| Body & Soul | 7 | Woodland Stage |

---

## Running the Application

### Backend
```bash
# From project root
./mvnw spring-boot:run
# Runs on http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui/index.html
# H2 Console: http://localhost:8080/h2-console
#   JDBC URL: jdbc:h2:mem:festivalpulse
```

### Frontend
```bash
cd frontend
npm install
npm run dev
# Runs on http://localhost:3000
```

> Both must be running simultaneously. The frontend proxies all `/api` calls to the backend automatically.
