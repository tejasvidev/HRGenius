package com.hrgenius;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@EnabledIfSystemProperty(named = "hrgenius.mysql-it", matches = "true")
@SpringBootTest(properties = {
        "app.security.jwt-secret=mysql-it-secret-material-at-least-32-characters-long",
        "app.bootstrap-admin.email=integration-admin@example.test",
        "logging.level.root=INFO",
        "logging.level.org.springframework.web.servlet=INFO",
        "logging.level.org.springframework.jdbc=INFO",
        "logging.level.org.springframework.security=INFO",
        "logging.level.org.hibernate.SQL=WARN",
        "app.bootstrap-admin.password=Integration-Password-123"
})
@AutoConfigureMockMvc
class MySqlMigrationIntegrationTest {
    @Container
    static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.4")
            .withDatabaseName("hrgenius_it")
            .withUsername("hrgenius")
            .withPassword("integration-only-password");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", MYSQL::getJdbcUrl);
        properties.add("spring.datasource.username", MYSQL::getUsername);
        properties.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Autowired private JdbcTemplate jdbc;
    @Autowired private MockMvc mockMvc;

    @Test
    void flywayCreatesIdentitySchemaAndHibernateValidatesIt() {
        Integer appliedMigrations = jdbc.queryForObject(
                "SELECT COUNT(*) FROM flyway_schema_history WHERE success = TRUE", Integer.class);
        Integer userTable = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'users'", Integer.class);
        Integer refreshTable = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'refresh_tokens'", Integer.class);
        Integer rolesTable = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'user_roles'", Integer.class);
        Integer bootstrapUser = jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE email = 'integration-admin@example.test'", Integer.class);

        Integer departmentTable = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'departments'", Integer.class);
        Integer designationTable = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'designations'", Integer.class);
        Integer locationTable = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'locations'", Integer.class);
        Integer profileTable = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'organization_profile'", Integer.class);
        Integer employeeTable = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'employees'", Integer.class);
        Integer employeeHistoryTable = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'employee_history'", Integer.class);
        Integer leaveTypesTable = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'leave_types'", Integer.class);
        Integer leaveBalancesTable = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'leave_balances'", Integer.class);
        Integer leaveRequestsTable = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'leave_requests'", Integer.class);
        Integer openingsTable = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'job_openings'", Integer.class);
        Integer candidatesTable = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'candidates'", Integer.class);
        Integer applicationsTable = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'job_applications'", Integer.class);
        Integer applicationHistoryTable = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'application_history'", Integer.class);
        Integer performanceReviewsTable = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'performance_reviews'", Integer.class);
        Integer announcementsTable = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'announcements'", Integer.class);
        Integer notificationsTable = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'in_app_notifications'", Integer.class);
        Integer interviewsTable = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'interviews'", Integer.class);
        Integer offersTable = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'offers'", Integer.class);
        Integer onboardingTemplates = jdbc.queryForObject("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'onboarding_templates'", Integer.class);
        Integer onboardingTemplateTasks = jdbc.queryForObject("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'onboarding_template_tasks'", Integer.class);
        Integer onboardingPlans = jdbc.queryForObject("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'onboarding_plans'", Integer.class);
        Integer onboardingTasks = jdbc.queryForObject("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'onboarding_tasks'", Integer.class);
        Integer onboardingDocuments = jdbc.queryForObject("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'onboarding_documents'", Integer.class);

        Integer leavePolicyTable = jdbc.queryForObject("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'leave_calendar_policy'", Integer.class);
        Integer leaveHolidaysTable = jdbc.queryForObject("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'leave_holidays'", Integer.class);
        Integer accrualRulesTable = jdbc.queryForObject("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'leave_accrual_rules'", Integer.class);
        Integer accrualLedgerTable = jdbc.queryForObject("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'leave_accrual_ledger'", Integer.class);
        Integer attendanceTable = jdbc.queryForObject("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'attendance_records'", Integer.class);
        Integer attendanceCorrectionsTable = jdbc.queryForObject("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'attendance_correction_requests'", Integer.class);
        Integer attendancePolicyTable = jdbc.queryForObject("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'attendance_policy'", Integer.class);
        Integer salaryAssignments = jdbc.queryForObject("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'salary_assignments'", Integer.class);
        Integer payrollRuns = jdbc.queryForObject("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'payroll_runs'", Integer.class);
        Integer payrollItems = jdbc.queryForObject("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'payroll_items'", Integer.class);
        assertThat(appliedMigrations).isEqualTo(14);
        assertThat(userTable).isEqualTo(1);
        assertThat(refreshTable).isEqualTo(1);
        assertThat(rolesTable).isEqualTo(1);
        assertThat(bootstrapUser).isEqualTo(1);
        assertThat(departmentTable).isEqualTo(1);
        assertThat(designationTable).isEqualTo(1);
        assertThat(locationTable).isEqualTo(1);
        assertThat(profileTable).isEqualTo(1);
        assertThat(employeeTable).isEqualTo(1);
        assertThat(employeeHistoryTable).isEqualTo(1);
        assertThat(leaveTypesTable).isEqualTo(1);
        assertThat(leaveBalancesTable).isEqualTo(1);
        assertThat(leaveRequestsTable).isEqualTo(1);
        assertThat(accrualRulesTable).isEqualTo(1);
        assertThat(accrualLedgerTable).isEqualTo(1);
        assertThat(attendanceTable).isEqualTo(1);
        assertThat(attendanceCorrectionsTable).isEqualTo(1);
        assertThat(attendancePolicyTable).isEqualTo(1);
        assertThat(salaryAssignments).isEqualTo(1);
        assertThat(payrollRuns).isEqualTo(1);
        assertThat(payrollItems).isEqualTo(1);
        assertThat(openingsTable).isEqualTo(1);
        assertThat(candidatesTable).isEqualTo(1);
        assertThat(applicationsTable).isEqualTo(1);
        assertThat(applicationHistoryTable).isEqualTo(1);
        assertThat(performanceReviewsTable).isEqualTo(1);
        assertThat(announcementsTable).isEqualTo(1);
        assertThat(notificationsTable).isEqualTo(1);
        assertThat(interviewsTable).isEqualTo(1);
        assertThat(offersTable).isEqualTo(1);
        assertThat(onboardingTemplates).isEqualTo(1);
        assertThat(onboardingTemplateTasks).isEqualTo(1);
        assertThat(onboardingPlans).isEqualTo(1);
        assertThat(onboardingTasks).isEqualTo(1);
        assertThat(onboardingDocuments).isEqualTo(1);
        assertThat(leavePolicyTable).isEqualTo(1);
        assertThat(leaveHolidaysTable).isEqualTo(1);
    }

    @Test
    void bootstrappedAdministratorCanAuthenticateAndRefreshTokenIsPersistedAsHash() throws Exception {
        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"integration-admin@example.test\",\"password\":\"Integration-Password-123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.roles[0]").value("SUPER_ADMIN"))
                .andReturn();

        jakarta.servlet.http.Cookie refreshCookie = login.getResponse().getCookie("hrgenius_refresh");
        assertThat(refreshCookie).isNotNull();
        String storedHash = jdbc.queryForObject("SELECT token_hash FROM refresh_tokens", String.class);
        assertThat(storedHash).hasSize(64).isNotEqualTo(refreshCookie.getValue());
        String accessToken=com.fasterxml.jackson.databind.json.JsonMapper.builder().build().readTree(login.getResponse().getContentAsString()).get("accessToken").asText();
        mockMvc.perform(get("/api/v1/reports/headcount").param("from","2026-01-01").param("to","2026-12-31").header("Authorization","Bearer "+accessToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalEmployees").value(0));
        mockMvc.perform(get("/api/v1/reports/headcount/data").param("from","2026-01-01").param("to","2026-12-31").header("Authorization","Bearer "+accessToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.report").value("headcount"));
        for(String type:java.util.List.of("employee-directory","department-wise","designation-wise","attendance","late-arrivals","absences","overtime","leave","leave-balances","recruitment","hiring-funnel","onboarding","payroll","performance")){
            mockMvc.perform(get("/api/v1/reports/"+type).param("from","2026-01-01").param("to","2026-12-31").header("Authorization","Bearer "+accessToken))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.report").value(type));
        }
    }
}
