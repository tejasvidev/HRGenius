# HRGenius

HRGenius is a modular human resource management system for managing employee information and core employee lifecycle workflows. It includes a Spring Boot REST API, a React and TypeScript web app, and a MySQL database managed by Flyway migrations.

## Features

- **Authentication and access control:** JWT access tokens, rotating refresh tokens in secure HTTP-only cookies, logout, password changes, failed-login lockout, and environment-configured first administrator bootstrap.
- **Organization and employees:** Company profile, department/designation/location catalogs, employee directory and records, employment history, status management, and employee self-service profile updates.
- **Attendance:** Employee check-in/out, attendance history, HR corrections, configurable workday and overtime policy, and scheduled absence rollup.
- **Leave:** Leave types and balances, employee requests and manager/HR decisions, workday and holiday calendars, and optional monthly accrual with annual caps.
- **Recruitment and onboarding:** Openings, candidates, applications, interviews, offers, accepted-offer conversion, onboarding templates, tasks, and document-reference review.
- **Payroll:** Monthly salary assignments, draft payroll runs, approval, and employee access to approved personal payslips.
- **Performance:** Manager-assigned review cycles, employee self-reviews, and manager feedback.
- **Announcements and notifications:** Department-targeted announcements and an employee notification inbox with read state.
- **Reports:** Employee/headcount, organization distribution, attendance, leave, recruitment, onboarding, payroll, and performance reports. Applicable filters, sorting, pagination, CSV/XLSX exports, and browser print-to-PDF are available.
- **Optional email:** SMTP notifications for leave decisions and manager review requests, candidate offers, and new employee welcome messages.

See [Project Audit](docs/PROJECT_AUDIT.md) for implemented workflows and known gaps, and [Test Strategy](docs/TEST_STRATEGY.md) for verification coverage.

## Technology

- Java 21, Spring Boot 3.5, Spring Security, Spring Data JPA, Flyway
- MySQL 8.x
- React 19, TypeScript, Vite
- Maven, npm, JUnit, MockMvc, Testcontainers

## Requirements

- Java 21
- Maven 3.9 or later
- Node.js 20.19+ or 22.12+, with npm
- MySQL 8.x
- Docker Desktop (optional; needed for the disposable MySQL integration tests)

## Quick start

### 1. Configure the database

Create a dedicated local database and user. Do not use a production or otherwise valuable database for development.

```sql
CREATE DATABASE hrgenius CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER 'hrgenius'@'localhost' IDENTIFIED BY 'replace-with-a-local-password';
GRANT ALL PRIVILEGES ON hrgenius.* TO 'hrgenius'@'localhost';
```

### 2. Configure environment variables

Copy `.env.example` to `.env` in the repository root. Set `DB_USERNAME` and `DB_PASSWORD` for the database created above. Generate a unique JWT secret of at least 32 bytes; in PowerShell:

```powershell
[Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(48))
```

Set the result as `JWT_SECRET`. To create the first super administrator, also set `BOOTSTRAP_ADMIN_EMAIL` and `BOOTSTRAP_ADMIN_PASSWORD` (at least 12 characters). The bootstrap does not alter an existing account. After the first successful startup, remove those bootstrap values from `.env`.

The example configuration uses `AUTH_COOKIE_SECURE=false` for local HTTP. Use `true` behind HTTPS. SMTP notifications are disabled by default.

### 3. Start the backend

From the repository root, run:

```powershell
cd backend
mvn spring-boot:run
```

The backend starts at `http://localhost:8080`. Flyway applies migrations at startup and Hibernate validates the resulting schema.

### 4. Start the frontend

In a second terminal:

```powershell
cd frontend
npm ci
npm run dev
```

Open `http://localhost:5173` and sign in with the bootstrap administrator account. The frontend API URL defaults to `http://localhost:8080`; set `VITE_API_BASE_URL` if the backend runs elsewhere.

## Useful URLs

- Web app: `http://localhost:5173`
- OpenAPI / Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Health check: `http://localhost:8080/actuator/health`

Except for health and API documentation endpoints, API routes require authentication. There is no public user registration endpoint.

## Build and verification

Run backend tests and package verification:

```powershell
cd backend
mvn verify
```

Check and build the frontend:

```powershell
cd frontend
npm ci
npm run typecheck
npm run build
```

The H2/MockMvc suite runs with `mvn verify`. To also apply all migrations and validate the schema against a disposable MySQL 8.4 container, ensure Docker Desktop is running and execute:

```powershell
cd backend
mvn -Dhrgenius.mysql-it=true -Dtest=MySqlMigrationIntegrationTest test
```

The MySQL integration suite uses Testcontainers and does not connect to the local development database. See [Test Strategy](docs/TEST_STRATEGY.md) for coverage and suites that are not yet implemented.

## API overview

All application APIs are under `/api/v1`. The exact request and response schemas are available in Swagger UI.

| Area | Routes |
| --- | --- |
| Authentication | `/auth` |
| Organization catalogs | `/organization`, `/departments`, `/designations`, `/locations` |
| Employees | `/employees` |
| Attendance | `/attendance` |
| Leave | `/leave` |
| Recruitment | `/recruitment/openings`, `/candidates`, `/applications`, `/interviews`, `/offers` |
| Onboarding | `/onboarding` |
| Payroll | `/payroll` |
| Performance | `/performance` |
| Announcements and notifications | `/announcements`, `/notifications` |
| Reports | `/reports` |

Role-based access is enforced by the API. Employee self-service is limited to the signed-in employee; managers can review assigned team workflows; HR and recruiter access varies by feature. Payroll and performance reports are restricted to `SUPER_ADMIN`, `ADMIN`, and `HR_MANAGER`. Recruiters can access recruitment reports. Report downloads are capped at 20,000 rows and return `X-Export-Truncated: true` when the cap is reached.

## Configuration reference

Settings are read from environment variables. The backend loads the repository-root `.env` file for local development; environment variables supplied by the runtime take precedence.

| Variable | Purpose |
| --- | --- |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | MySQL connection |
| `SERVER_PORT` | Backend port (default `8080`) |
| `VITE_API_BASE_URL` | Frontend API base URL (default `http://localhost:8080`) |
| `CORS_ALLOWED_ORIGIN` | Allowed frontend origin (default `http://localhost:5173`) |
| `JWT_SECRET` | Unique signing secret; at least 32 bytes |
| `JWT_ACCESS_TOKEN_MINUTES`, `JWT_REFRESH_TOKEN_DAYS` | Token lifetimes |
| `FAILED_LOGIN_LIMIT`, `FAILED_LOGIN_LOCK_MINUTES` | Login lockout policy |
| `AUTH_COOKIE_SECURE` | Require HTTPS for the refresh cookie; use `true` in production |
| `BOOTSTRAP_ADMIN_EMAIL`, `BOOTSTRAP_ADMIN_PASSWORD` | One-time initial administrator account |
| `ATTENDANCE_TIME_ZONE` | Attendance date/time zone (default `Asia/Kolkata`) |
| `EMAIL_NOTIFICATIONS_ENABLED`, `EMAIL_FROM_ADDRESS` | Enable and identify outgoing email |
| `SMTP_HOST`, `SMTP_PORT`, `SMTP_USERNAME`, `SMTP_PASSWORD` | SMTP server settings |
| `SMTP_AUTH`, `SMTP_STARTTLS` | SMTP authentication and STARTTLS options |

Never commit `.env`, database credentials, signing secrets, SMTP credentials, or real employee data. No demo employee records or default administrator password are seeded.

## Current limitations

- Payroll calculation is monthly gross minus a fixed deduction; tax, benefits, proration, accounting exports, and PDF payslips are not included.
- Recruitment resumes and onboarding documents are stored as HTTPS references; the app does not upload or store files.
- Attendance does not deduct breaks and does not include biometric/GPS verification or manager team dashboards.
- Performance goals, competencies, and calibration are not included.
- Reports do not yet support scheduled delivery or a custom report builder. PDF output is provided through the browser's print dialog.
- Frontend component tests and end-to-end browser tests remain outstanding.
