# HRGenius Test Strategy

This plan distinguishes tests that exercise isolated logic from tests that require the actual database, browser, or deployment infrastructure. A passing compile is not evidence that an HR workflow is correct.

## Current baseline

The H2/MockMvc suite covers health, authentication, organization APIs, employee security/self profile and detail privacy, leave submission/approval/balance, holiday-based working day calculation and idempotent monthly accrual, attendance check-in/out, correction approval, policy settings, repeatable workday absence rollup, fixed salary assignment and payroll approval/payslip access, manager-owned performance review completion, announcement draft/publish visibility and in-app notification read state, HR headcount/date filtering and the full paged report catalog with CSV/XLSX export, recruitment through accepted-offer conversion, and onboarding setup. Separate unit tests cover enabled and disabled email notification delivery using a mocked mail sender. The H2 suite uses a disposable database in MySQL compatibility mode with Flyway disabled. A separate opt-in Testcontainers suite applies all fourteen migrations, validates Hibernate mappings on MySQL 8.4, checks feature tables/bootstrap, and authenticates the bootstrap account. Run it with `mvn -Dhrgenius.mysql-it=true -Dtest=MySqlMigrationIntegrationTest test`. H2 results are not treated as MySQL migration or dialect validation. Frontend component and E2E tests remain outstanding.

## Backend

- **Unit:** JUnit 5, Mockito, and AssertJ for service rules, validators, mappings, authorization decisions, calculations, and state transitions.
- **Repository:** Spring Data tests for constraints, joins, search, filters, sorting, and pagination.
- **Integration/API:** Spring Boot Test and MockMvc against MySQL 8 via Testcontainers when Docker is available. Cover migrations, transaction rollback, API validation, error contracts, authentication, and permissions.
- **Fallback:** If Docker is unavailable, run isolated unit tests and document that MySQL-specific integration checks remain unverified. Do not describe H2 results as MySQL validation.
- **Coverage:** Configure JaCoCo; target at least 80% meaningful line coverage overall and 90% of critical business logic as the project matures.

## Frontend

- Vitest, React Testing Library, and user-event for accessible component behavior, forms, role-based navigation, loading/error/empty states, and API interactions.
- MSW for deterministic HTTP behavior in component tests.
- TypeScript checking, linting, and production build are required CI gates.

## End-to-end

Use Playwright against a running application with disposable test data. Cover administrator setup, organization and employee creation, employee self-service, attendance, leave submission/approval, recruitment through offer and employee conversion, onboarding, payroll payslip access, and performance review. Include unauthorized and invalid-transition cases.

## Security cases

- Anonymous access to protected APIs is rejected.
- Employees cannot access another employee's records or sensitive payroll/bank data.
- Managers are limited to their authorized team; recruiters cannot access payroll.
- Employees cannot approve their own leave or assign themselves elevated roles.
- Invalid, expired, and revoked tokens are rejected.
- Passwords, hashes, refresh tokens, and secrets do not appear in responses or error messages.
- Upload validation rejects unsupported or oversized content.

## Database safety

- Apply Flyway migrations to a clean disposable MySQL database in integration tests.
- Verify unique, foreign-key, and check constraints as well as rollback behavior.
- Never point automated destructive setup at a developer's existing database. Keep test credentials and database names separate.

## Reporting

For each phase, record the exact command and its actual result. Report unit, integration, browser, security, and coverage outcomes separately. Mark unavailable infrastructure and unexecuted suites explicitly; do not infer success from compilation.
