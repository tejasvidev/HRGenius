# HRGenius

HRGenius is an HR-only modular monolith for employee lifecycle management. The repository started as an empty application scaffold; implementation is proceeding in phases. See the [project audit](docs/PROJECT_AUDIT.md) and [test strategy](docs/TEST_STRATEGY.md) for the verified starting point and coverage plan.

## Current status

The platform includes a Spring Boot backend, MySQL/Flyway configuration, OpenAPI/Actuator, and a React/TypeScript workspace. Authentication includes login, refresh rotation, logout, current profile, password change, role claims, failed-login lockout, and environment-driven first-admin bootstrap. Organization APIs manage the company profile and paginated department, designation, and location catalogs; the workspace supports profile setup and catalog management. Employee APIs manage records, assignments, directory search, status, history, and basic self-service; HR can view and edit authorized employee details and history. Attendance supports employee check-in/out, working-time calculation, history, HR correction review, configurable lateness and overtime thresholds, and scheduled absence rollup. Payroll supports fixed monthly salary assignments, draft monthly calculations, approval, and employee-only payslip access. Leave APIs support HR-managed balances, employee requests and manager/HR review, working-day policy, dated holidays, and opt-in monthly accrual with annual caps and a unique period ledger; HR can configure rules and run/backfill a month from the leave workspace. Recruitment APIs cover openings, candidates, applications, interviews, offers, and conversion to employees; the React workspace supports opening management, candidate records, application creation and stages, interview scheduling and feedback, offer decisions, and accepted-offer conversion. Onboarding includes reusable templates, task plans, employee task tracking, document reference submission and review, and creates a plan during accepted-offer conversion; the workspace supports setup and assignment. Optional SMTP notifications cover leave decisions, manager leave review requests, sent candidate offers, and new employee welcome messages.

## Requirements

- Java 21 and Maven 3.9+
- Node.js 20.19+ or 22.12+ and npm
- MySQL 8.x (or Docker Compose for a dedicated development database)

## Local setup

1. Copy `.env.example` to `.env` and set credentials for a dedicated `hrgenius` database. Do not point this project at an existing database whose schema has not been inspected.
   - Set `JWT_SECRET` to at least 32 bytes of unique random secret material. To generate a value in PowerShell, run `[Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(48))` and copy the result into `.env`.
   - To create the initial super administrator, set `BOOTSTRAP_ADMIN_EMAIL` and `BOOTSTRAP_ADMIN_PASSWORD` (12 or more characters). The bootstrap is idempotent and never changes an existing account. Unset the bootstrap values after first startup.
2. Ensure MySQL is running and create a database and user for local development, for example:

   ```sql
   CREATE DATABASE hrgenius CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
   CREATE USER 'hrgenius'@'localhost' IDENTIFIED BY 'replace-with-a-local-password';
   GRANT ALL PRIVILEGES ON hrgenius.* TO 'hrgenius'@'localhost';
   ```

3. Start the backend in one terminal:

   ```powershell
   cd backend
   mvn spring-boot:run
   ```

4. Start the frontend in another terminal:

   ```powershell
   cd frontend
   npm ci
   npm run dev
   ```

The frontend runs at `http://localhost:5173`; backend API documentation is at `http://localhost:8080/swagger-ui.html`; health is at `http://localhost:8080/actuator/health`.

## Build and verification

```powershell
cd backend; mvn verify
cd ..\frontend; npm ci; npm run typecheck; npm run build
```

Run the disposable MySQL 8.4 migration/bootstrap integration tests when Docker Desktop is available:

```powershell
cd backend
mvn -Dhrgenius.mysql-it=true -Dtest=MySqlMigrationIntegrationTest test
```

`POST /api/v1/auth/login` returns a short-lived access token and sets a rotating refresh token in an HttpOnly, SameSite=Strict cookie. Keep the access token in client memory; do not put it in local storage. `POST /api/v1/auth/refresh` rotates the cookie, `POST /api/v1/auth/logout` revokes the refresh token, `GET /api/v1/auth/me` returns the authenticated profile, and `POST /api/v1/auth/change-password` changes the password and revokes refresh sessions. There is no public user-registration endpoint.

The `.env.example` sets `AUTH_COOKIE_SECURE=false` for local HTTP development; the application default is `true`. Use Secure cookies behind HTTPS. No demo HR records or default administrator credentials are seeded.

## Organization API

All endpoints require a bearer access token. `SUPER_ADMIN` and `ADMIN` can create and update organization settings; `HR_MANAGER` can read them.

- `GET` / `PUT` `/api/v1/organization` — read or save the singleton organization profile.
- `GET`, `POST` `/api/v1/departments`; `GET`, `PUT` `/api/v1/departments/{id}`; `PATCH` `/api/v1/departments/{id}/active`.
- The same list/create/read/update/active pattern is available at `/api/v1/designations` and `/api/v1/locations`.
- Catalog list endpoints support `page`, `size` (capped at 100), `sort`, and `active`; designations also support `departmentId`.

## Employee API

`SUPER_ADMIN`, `ADMIN`, and `HR_MANAGER` can create, update, change status, and view employment history. HR roles can search the directory. `GET /api/v1/employees/me` returns the record whose work email matches the signed-in account; `PUT` on that route lets the employee update personal email, phone, and address fields. Directory responses omit personal email, date of birth, home address, and emergency contact details.

- `GET /api/v1/employees` supports `q`, `departmentId`, `designationId`, `locationId`, `status`, and standard pagination/sorting.
- `POST /api/v1/employees`, `GET`/`PUT /api/v1/employees/{id}` manage employee records.
- `PATCH /api/v1/employees/{id}/status` changes lifecycle status; `GET /api/v1/employees/{id}/history` reads HR-only change history.

## Leave API

Leave types and balances are configured by HR. Employees submit requests against an explicitly configured calendar-year balance. Pending requests can be cancelled by their owner. HR can review any request; managers can review requests from direct reports. Approval locks and deducts from the balance in the same transaction. HR can configure organization-wide working weekdays and date-based holidays; new requests count only configured working days and exclude active holidays. Requests must stay within one calendar year. Existing pending requests retain the day count calculated when they were submitted.

- `GET /api/v1/leave/types`; HR can `POST` a type and `PATCH /{id}/active?active=true|false`.
- `GET /api/v1/leave/balances/me?year=2026`; HR can `PUT /api/v1/leave/balances` to set an employee entitlement.
- `POST /api/v1/leave/requests`, `GET /api/v1/leave/requests`, `POST /{id}/decision`, and `POST /{id}/cancel` provide the request and review workflow.
- `GET`/`PUT /api/v1/leave/calendar/policy` reads and configures working weekdays. `GET`/`POST /api/v1/leave/calendar/holidays?year=2026` lists or adds date-specific holidays; HR can deactivate or reactivate a holiday.
- HR can `GET /api/v1/leave/accrual-rules?includeInactive=true`, `PUT /api/v1/leave/accrual-rules` with `leaveTypeId`, `monthlyDays`, optional `annualCap`, and `effectiveFrom`, and `PATCH /api/v1/leave/accrual-rules/{id}/active?active=false`.
- HR can `POST /api/v1/leave/accrual-runs?period=2026-10` to run or backfill a month. A scheduled run occurs at 00:05 on the first of each month in `Asia/Kolkata` by default; configure `app.leave.accrual-cron` and `app.leave.accrual-time-zone` to change it. The unique ledger makes a repeat run safe.

## Attendance API

Employees can record their own attendance and view their records. HR roles can browse attendance records and review correction requests. Duplicate daily records and invalid check-out times are rejected; corrections apply only after approval.

- `POST /api/v1/attendance/check-in` and `POST /api/v1/attendance/check-out` record current-day punches.
- `GET /api/v1/attendance/me?from=2026-10-01&to=2026-10-31` lists the authenticated employee's history.
- `POST /api/v1/attendance/corrections` accepts `workDate`, `checkIn`, `checkOut`, and `reason`; HR lists `GET /api/v1/attendance/corrections?status=PENDING` and decides via `PATCH /api/v1/attendance/corrections/{id}/decision`.
- HR can query `GET /api/v1/attendance/records?from=2026-10-01&to=2026-10-31`; `ATTENDANCE_TIME_ZONE` sets the organization's attendance day boundary (default `Asia/Kolkata`).

HR can configure the workday start, late grace, half-day threshold, and overtime threshold through `GET`/`PUT /api/v1/attendance/policy`. A daily job creates absent records for active employees on configured working days without a punch; it uses the leave working-day and holiday calendar. `APP_ATTENDANCE_ABSENCE_CRON` changes its default schedule (00:15 in the attendance time zone). Break deductions are not yet included in worked or overtime minutes. Biometric/GPS verification and manager team dashboards are not implemented.

## Payroll API

HR can set one current salary assignment per active employee with a currency, monthly gross, fixed deduction, and effective date. `POST /api/v1/payroll/runs?period=2026-10` creates a draft snapshot; HR can inspect items and approve it. Employees can access only their own approved statements from `GET /api/v1/payroll/me`.

The initial calculation is monthly gross minus the configured fixed deduction, without tax, benefits, proration, or PDF generation. Every active salary assignment in a payroll period must use the same currency.

## Performance API

HR managers can create a named review for an active employee with an assigned direct manager. Employees submit a self-summary and 1–5 rating; the assigned manager adds feedback and a rating to complete the review. Employees can read only their own reviews, managers can access their assigned reviews, and HR can administer or audit reviews. The app includes a role-aware review workspace. Endpoints are under `/api/v1/performance` (`GET /me`, `GET /team`, `POST /`, `PATCH /{id}/self`, and `PATCH /{id}/manager`). Goals, competencies, and calibration are not included yet.

## Announcements

HR managers and administrators can create drafts, optionally target one department, set priority and expiry, and publish. Employees see currently published organization-wide notices and those targeted to their department. Publishing creates an in-app notification for each active audience member; employees can view their own inbox, unread count, and mark one or all as read at `/api/v1/notifications/me`. Attachments, scheduled publishing, and notifications for events beyond announcements remain future work.

## Reports

The Reports workspace includes employee directory and headcount, department/designation distribution, attendance, late arrivals, absences, overtime, leave requests and balances, recruitment and hiring funnel, onboarding, payroll, and performance reports. Reports support date range, department and employee filters where applicable, sorting, pagination, and CSV or XLSX exports; Print / Save as PDF prints the current report page. The headcount API is `/api/v1/reports/headcount?from=2026-01-01&to=2026-10-31`. Payroll and performance data are restricted to super admins, admins, and HR managers; recruiters can access recruitment reports. CSV/XLSX downloads cap output at 20,000 rows and return `X-Export-Truncated: true` when capped. Detailed scheduled reports and custom report builders are not included.

## Internal recruitment API

Recruitment records are available only to `SUPER_ADMIN`, `ADMIN`, `HR_MANAGER`, `HR_EXECUTIVE`, and `RECRUITER`; managers can view assigned interviews and submit feedback. There is no public candidate portal. Sending an offer also emails the candidate when optional SMTP notifications are enabled.

## Onboarding workflows

HR roles can create reusable onboarding templates, assign plans to active employees, update tasks, and verify or reject document references. Accepted-offer conversion requires a template ID and creates the employee and plan in the same transaction. Employees can view their plans, update assigned checklist items, and submit HTTPS document references. Task due dates are derived from the plan start date. Document files are not uploaded or stored by HRGenius; references point to the organization's approved document storage. The workspace supports template setup, plan assignment, task tracking, and document review.

- `/api/v1/recruitment/openings`: list/create/update and change status between draft, open, and closed.
- `/api/v1/recruitment/candidates`: recruiter-managed candidate directory and create.
- `/api/v1/recruitment/applications`: attach candidates to open positions, track stages, and list pipeline entries.
- `/api/v1/recruitment/interviews`: schedule interviews and record interviewer feedback/rating.
- `/api/v1/recruitment/offers`: create, send, record acceptance/decline, and convert accepted applications into employees.

Resume files are not uploaded or stored; candidate records can hold an HTTPS resume reference. Salary on an offer is not yet connected to payroll.

## Configuration

Backend settings use `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `SERVER_PORT`. Frontend API origin uses `VITE_API_BASE_URL`. Email uses `EMAIL_NOTIFICATIONS_ENABLED`, `EMAIL_FROM_ADDRESS`, `SMTP_HOST`, `SMTP_PORT`, `SMTP_USERNAME`, `SMTP_PASSWORD`, `SMTP_AUTH`, and `SMTP_STARTTLS`; it is disabled by default, and notifications are sent asynchronously after the database transaction commits. Do not commit `.env` or secrets.
