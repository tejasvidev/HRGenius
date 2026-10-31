# HRGenius Project Audit

**Audit date:** 2026-10-31  
**Branch:** `develop`  
**Audited revision:** `4986466` (`chore: initialize HRGenius repository`)

## Current implementation status

The repository is an initial Git commit, not the partially built application described in the project brief. Its tracked contents are only `.gitignore` and this project's short `README.md`. There is no application code or executable baseline to preserve yet.

## Existing functionality

- Repository initialized on branch `develop`.
- `.gitignore` excludes common Java, Node, IDE, log, and test-output artifacts.
- README names the intended stack and modular-monolith direction.

No HR workflows, API, UI, database schema, authentication, or tests are implemented.

## Repository and configuration findings

| Area | Finding |
|---|---|
| Backend | No Maven/Gradle files, Java sources, Spring configuration, or API endpoints. |
| Frontend | No `package.json`, lockfile, Vite configuration, TypeScript sources, or UI. |
| Database | No database directory, schema, connection configuration, Flyway migrations, or migration history. |
| Security | No authentication, authorization, password storage, token handling, or security tests. |
| Tests | No test source, test framework configuration, or coverage configuration. |
| DevOps | No Docker Compose, container build, CI workflow, or environment template. |
| Documentation | Only a brief README; architecture, API, schema, test, and security documentation are absent. |
| Git | One initialization commit; working tree was clean at audit time. No `AGENTS.md` guidance was found. |

## Toolchain and local database

- Java 21.0.12, Maven 3.10.0, Node 24.13.0, npm 11.6.2, MySQL client/server 8.0.46, and Docker 29.9.2 are installed.
- The `MySQL80` Windows service is running.
- A read-only `SHOW DATABASES` attempt as `root` without a password failed with MySQL error 1045. No repository configuration or supplied credentials exist, so the configured schema, tables, and Flyway history cannot be inspected without credentials. No database was changed.
- Docker prints an access-denied warning while reading the user's Docker config; the CLI reports version 29.9.2. Docker daemon availability has not yet been established.
- No Maven or npm project exists, so dependency compatibility, compilation, dependency resolution, and application builds cannot yet be evaluated.

## Findings by risk area

### Dependency problems

There are no dependency manifests. Compatibility and vulnerability status are therefore unknown, rather than known to be healthy.

### Database problems

There is no schema or migration history in the repository. The local MySQL server is running, but its databases could not be enumerated with the available no-password credentials. It would be unsafe to assume an empty database or alter it.

### Security problems

The application has no security implementation. Authentication and authorization must be built before exposing business APIs. Secrets and administrator credentials must be configuration-only and must not be committed.

### Build problems

There is no backend or frontend build to run. The only available baseline is Git cleanliness and installed tool versions.

## Recommended implementation sequence

1. Establish a buildable Spring Boot and Vite foundation with environment-based configuration and a health endpoint.
2. Add Flyway-managed initial schema and test configuration without connecting to or changing an existing local database by default.
3. Implement authentication, role/permission enforcement, and secure bootstrap before business endpoints.
4. Implement organization and employee management, with validation, auditability, and self-service access rules.
5. Add recruitment, onboarding, attendance, leave, payroll, performance, documents, notifications, and reports in bounded modules.
6. Integrate the React application against real APIs, then add workflow, integration, security, and end-to-end coverage.
7. Add deployment, CI, and operations documentation after the local workflows are reproducible.

## Baseline limitations

The broad HRMS requirements cannot be considered implemented or verified from this starting point. Database inspection requires the correct local MySQL credentials, but implementation can proceed safely by keeping database access opt-in and using a separate test database/container.

## Foundation phase update (2026-10-31)

Added a Spring Boot 3.5.6 / Java 21 Maven application with MySQL, Flyway, JPA schema validation, Actuator health, and OpenAPI configuration; added a Vite/React/TypeScript workspace that reads the actual health endpoint; and added a backend smoke test that excludes database auto-configuration. Added environment examples and setup instructions. The MySQL service itself remains uninspected because credentials are still unavailable. The current backend test validates application startup and health only; it does not validate connectivity, migrations, or schema behavior against MySQL. No HR business module or authentication workflow is complete yet.

Verified on this date:

- `mvn -Dmaven.repo.local=E:\HRGenius\.m2\repository test` — initial dependency resolution and build succeeded before the smoke test was added; no tests were then present.
- `mvn -Dmaven.repo.local=E:\HRGenius\.m2\repository verify` — succeeded with the health smoke test.
- `npm install` — succeeded and reported 0 vulnerabilities; generated `frontend/package-lock.json`.
- `npm run typecheck` — passed after adding Vite environment declarations.
- `npm run build` — passed (Vite 7.3.7 production bundle).

## Authentication phase update (2026-10-31)

Added the first Flyway identity migration, user and refresh-token persistence, seven application roles, BCrypt password storage, JWT access-token verification, one-time environment-based super-admin bootstrap, rotating opaque refresh tokens stored by SHA-256 hash in an HttpOnly SameSite cookie, generic authentication errors, failed-login lockout, profile retrieval, logout, and password change that revokes refresh sessions. Endpoints require a bearer token except login, refresh, logout, health, and API documentation. Role values are carried as signed token authorities; granular feature permissions and team/ownership checks are still pending.

Verification: `mvn -Dmaven.repo.local=E:\HRGenius\.m2\repository verify` passed with six MockMvc tests on an ephemeral H2 database. No MySQL credentials were available for inspecting the existing local server. No public registration, forgot/reset password, or refresh-token reuse incident handling is implemented yet.

Added an opt-in Testcontainers integration test that starts disposable MySQL 8.4, runs Flyway, and lets Hibernate validate the mapped schema. It runs when explicitly enabled with `-Dhrgenius.mysql-it=true`.

MySQL integration verification completed successfully on 2026-10-31: `mvn -Dmaven.repo.local=E:\HRGenius\.m2\repository -Dhrgenius.mysql-it=true -Dtest=MySqlMigrationIntegrationTest test` passed 2 tests. Testcontainers 2.0.5 launched a disposable MySQL 8.4.13 container; Flyway applied V1, Hibernate validated the schema, the one-time administrator bootstrapped, and login persisted a hashed refresh token. Flyway logged that 8.4 is newer than its officially tested MySQL range (latest supported noted as 8.1), so this is observed compatibility, not vendor certification.

The frontend now has a login screen and auth context. It keeps access tokens in memory, uses the HttpOnly refresh cookie with credentials, refreshes once on a protected API 401 while coalescing concurrent retries, and clears session state if refresh fails. Its only other live data request is system health. No employee or HR workflow UI exists yet.

## Organization phase update (2026-10-31)

Added Flyway V2 organization profile, departments, designations, and locations schema plus validated CRUD APIs, active/inactive state, bounded pagination, and case-insensitive uniqueness checks. Organization profile is a singleton. `SUPER_ADMIN` and `ADMIN` may write; those roles and `HR_MANAGER` may read. Employee and other lifecycle workflows remain outstanding.


## Employee phase update (2026-10-31)

Added V3 employee and employee history tables, directory search/filter/pagination, create/update/status APIs, assignment validation against active organization records, a non-reactivatable separated state, and a self profile resolved from the authenticated email claim. Employees can update their own personal email, phone, and address, while directory responses omit those fields along with birth date and emergency contacts. HR history logs hire, transfer, and status events. Broader employee self-service workflows remain outstanding.

Verification: backend `mvn verify` passed with 8 H2 MockMvc tests, including employee write denial for a standard employee, administrator record creation, private-field omission, own-profile lookup, and directory search. The opt-in Testcontainers run also passed 2 tests against disposable MySQL 8.4.13; Flyway applied V1–V3 and Hibernate validated employee and history mappings. The existing Flyway warning that 8.4 is newer than its tested support range remains applicable.

The React workspace now calls the employee API for a searchable paginated directory and provides role-limited employee creation, detail, and edit views with employment history. The directory response omits personal and emergency contact details; those fields are returned only by the HR-authorized employee detail endpoint to preserve them when HR edits a full record. Broader self-service and frontend component/E2E tests remain outstanding.

## Leave phase update (2026-10-31)

Added Flyway V4 leave types, per-employee calendar-year balances, and leave requests. HR can configure types and entitlements. Employees can submit and cancel their own pending requests; HR roles can review all requests, while managers can review requests from direct reports. Self-approval is blocked, overlapping pending/approved dates are rejected, and approval locks and deducts the balance transactionally. The React workspace supports employee balances and requests plus HR request review, leave type configuration, annual entitlement management, working weekday policy, and year-based holiday management. New request day counts skip configured non-working days and active holidays; existing pending requests retain the count captured at submission.

## Leave calendar phase update (2026-10-31)

Added V7 organization-wide working day configuration and dated holidays with active/inactive state. New requests count configured workdays, exclude active holidays, reject ranges with no working days, and continue to enforce the configured annual entitlement. The leave administration workspace manages weekdays and holidays by year.

Added V8 opt-in monthly leave accrual rules, annual entitlement caps, and a unique employee/type/year/month ledger. A scheduled job grants active rules at the start of each month; HR can configure, deactivate, and manually run/backfill a period. Repeated runs skip ledgered grants. H2 tests cover rule creation and idempotent grant behavior. `mvn verify` passed with 10 H2/MockMvc tests, the V1–V8 migration and Hibernate validation passed against disposable MySQL 8.4 Testcontainers, and the frontend production build passed. Flyway warns that MySQL 8.4 is newer than its tested support range.

Added optional SMTP email notifications for manager review of leave requests, leave decision updates to employees, candidate offer delivery, and new employee welcome messages. Email events are dispatched after transaction commit and on a background executor, so a mail provider outage does not undo HR workflow updates. SMTP settings are environment-based and disabled by default.

Added V9 attendance records and correction requests. Employees can check in and out once per work date, review their own attendance, and request corrections; HR can review and approve/reject corrections and view attendance by date range. V10 adds configurable workday start, late grace, half-day threshold, and overtime threshold; a daily scheduled rollup creates absent records for active employees on configured workdays, excluding configured holidays. The workspace provides employee punch/history and HR attendance/correction review plus HR policy settings. The job is repeatable and protected by the employee/date unique key. Verification passed: backend `mvn verify` (12 H2/MockMvc and 2 notification unit tests), disposable MySQL 8.4 Testcontainers (2 tests; V1–V10 applied and Hibernate mappings validated), frontend `npm run build`, and `git diff --check`. Break deductions are not included in work minutes; manager team dashboards and biometric/GPS integrations remain unimplemented.

Added V11 payroll basics: HR salary assignments, monthly payroll calculation as fixed gross less fixed deduction, draft-to-approved runs, and employee access restricted to their own approved payslips. Payroll runs snapshot salary values and require a single currency across included employees. Tax, benefits, proration, accounting exports, and PDF payslips are not included in this first slice.

Verification passed: H2/MockMvc payroll workflow test, all prior backend tests, V1–V11 MySQL Testcontainers migration/mapping validation (2 tests), and the frontend production build. Payroll run approval is limited to super admins, admins, and HR managers; employees can see only approved personal items.

## Performance phase update (2026-10-31)

Added V12 performance reviews with HR-created named review cycles, self-review summaries and ratings, and manager feedback and ratings. Review assignment follows the employee's direct manager; inactive employees and employees without a manager cannot be assigned. Employee access is restricted to personal reviews, manager access to directly assigned reviews, and HR administration to HR roles. The API enforces self-review before manager completion and one review per employee per cycle. Goals, competencies, and calibration remain outstanding.

Verification: backend `mvn verify` passed with 14 H2/MockMvc workflow tests plus 2 email unit tests; the performance review workflow covers employee self-review and assigned-manager completion. The opt-in disposable MySQL 8.4 Testcontainers check passed 2 tests with all V1–V12 migrations applied and Hibernate schema validation successful. A role-aware performance workspace was subsequently added for review setup, employee self-review, manager feedback, and HR status review.

## Announcements phase update (2026-10-31)

Added V13 announcements with HR-managed draft, edit-before-publish, publish, priority, optional active-department targeting, and optional expiry. Employees see published, unexpired organization-wide notices and notices for their department. The app includes a management and employee announcement workspace. Attachments, scheduled publishing, in-app notification preferences, and read/unread tracking remain outstanding.

Verification: backend `mvn verify` passed with announcement draft/publish visibility covered in H2/MockMvc. The opt-in disposable MySQL 8.4 Testcontainers check passed 2 tests with all V1–V13 migrations applied and Hibernate schema validation successful. Frontend `npm run build` and `git diff --check` passed.

## In-app notification phase update (2026-10-31)

Added V14 persisted employee notifications generated transactionally when announcements publish. Employees can list their own inbox, get an unread count, mark an item read, and mark all their items read. The UI includes the employee inbox; notifications for leave, attendance, onboarding, payroll, and review events plus preferences remain outstanding.

Verification: backend `mvn verify` passed with the announcement notification creation/read-state workflow covered in H2/MockMvc. The opt-in disposable MySQL 8.4 Testcontainers suite passed 2 tests with all V1–V14 migrations applied and Hibernate schema validation successful. Frontend production build and `git diff --check` passed.

## Reporting phase update (2026-10-31)

Expanded reporting to include employee directory and headcount, department/designation distribution, attendance, late arrivals, absences, overtime, leave requests and balances, recruitment and hiring funnel, onboarding, payroll, and performance. Reports support applicable date, department, and employee filters, sorting, pagination, CSV and XLSX downloads, and browser print-to-PDF. Payroll and performance reports are limited to super admins, admins, and HR managers; recruiters can access recruitment reports. Exports cap output at 20,000 rows and identify truncation in a response header. Scheduled reports and custom report builders remain outstanding.

Verification: backend `mvn verify` passed (17 H2/MockMvc tests and 2 notification unit tests), including report catalog, CSV/XLSX generation, and access denial checks. The opt-in disposable MySQL 8.4 Testcontainers suite passed 2 tests with all V1–V14 migrations applied and every report query exercised against MySQL; Hibernate schema validation succeeded. Frontend `npm run build` and `git diff --check` passed.

## Organization workspace update (2026-10-31)

Added a role-aware organization workspace for the company profile and department, designation, and location catalogs. Super admins and admins can create, edit, and activate/deactivate records; HR managers can view catalogs. Organization profile and catalog data use the existing validated API permissions and uniqueness rules.

Verification: backend `mvn verify` passed with 9 H2/MockMvc tests, including request creation, self-approval denial, HR approval, balance deduction, and overlap rejection. The opt-in Testcontainers MySQL suite passed 2 tests with V1–V4 applied and Hibernate mappings validated. Flyway's MySQL 8.4 support warning remains.

## Recruitment phase update (2026-10-31)

Added V5 job openings, candidate records, applications and stage history, interviews with feedback/rating, offers, and accepted-offer conversion to employee records. Recruitment APIs are restricted to HR/recruiter roles; managers can view assigned interviews and provide feedback. State transitions enforce an open opening, vacancy limits, interview completion before offer, sent offer before decision, accepted offer before conversion, and no duplicate applications. The React workspace supports opening management, candidate records, application creation and stage changes, interview scheduling and feedback, offer send/decision, and accepted-offer conversion with onboarding template selection. Outbound email, resume upload/storage, and payroll integration remain outstanding.

## Onboarding phase update (2026-10-31)

Added V6 reusable onboarding templates, cloned employee plans and role-assigned checklist tasks, employee HTTPS document references, and HR verification/rejection. Accepted-offer conversion now requires a template and creates the employee and onboarding plan transactionally. The React workspace displays plan tasks, permits assigned task updates and document submission, lets HR review documents, create templates, and assign plans to active employees. File upload/storage remains outstanding. H2 MockMvc verification passed; V6 migration and Hibernate schema validation passed against disposable MySQL 8.4 Testcontainers.

Verification: backend `mvn verify` passed with 10 H2/MockMvc tests, including the opening → candidate application → interview → offer → employee conversion workflow and denial of employee access. The opt-in Testcontainers suite passed 2 tests with V1–V5 applied and Hibernate validating the schema. Frontend `npm run typecheck` and `npm run build` passed after adding the opening board. Flyway continues to warn that MySQL 8.4 is newer than its tested support range.
