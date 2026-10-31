package com.hrgenius;

import com.hrgenius.auth.entity.RefreshToken;
import com.hrgenius.auth.repository.RefreshTokenRepository;
import com.hrgenius.employee.repository.EmployeeHistoryRepository;
import com.hrgenius.employee.repository.EmployeeRepository;
import com.hrgenius.leave.repository.LeaveBalanceRepository;
import com.hrgenius.leave.repository.LeaveRequestRepository;
import com.hrgenius.leave.repository.LeaveTypeRepository;
import com.hrgenius.leave.repository.LeaveAccrualRuleRepository;
import com.hrgenius.leave.repository.LeaveAccrualLedgerRepository;
import com.hrgenius.attendance.repository.AttendanceRecordRepository;
import com.hrgenius.attendance.repository.AttendanceCorrectionRepository;
import com.hrgenius.attendance.service.AttendanceService;
import com.hrgenius.payroll.repository.SalaryAssignmentRepository;
import com.hrgenius.payroll.repository.PayrollRunRepository;
import com.hrgenius.payroll.repository.PayrollItemRepository;
import com.hrgenius.leave.repository.LeaveHolidayRepository;
import com.hrgenius.recruitment.repository.JobOpeningRepository;
import com.hrgenius.recruitment.repository.CandidateRepository;
import com.hrgenius.recruitment.repository.JobApplicationRepository;
import com.hrgenius.recruitment.repository.ApplicationHistoryRepository;
import com.hrgenius.recruitment.repository.InterviewRepository;
import com.hrgenius.recruitment.repository.OfferRepository;
import com.hrgenius.onboarding.repository.OnboardingDocumentRepository;
import com.hrgenius.onboarding.repository.OnboardingTaskRepository;
import com.hrgenius.onboarding.repository.OnboardingPlanRepository;
import com.hrgenius.onboarding.repository.OnboardingTemplateRepository;
import com.hrgenius.organization.repository.DepartmentRepository;
import com.hrgenius.organization.repository.DesignationRepository;
import com.hrgenius.organization.repository.LocationRepository;
import com.hrgenius.organization.repository.OrganizationProfileRepository;
import com.hrgenius.performance.repository.PerformanceReviewRepository;
import com.hrgenius.performance.entity.PerformanceReview;
import com.hrgenius.announcement.repository.AnnouncementRepository;
import com.hrgenius.notification.repository.InAppNotificationRepository;
import com.hrgenius.employee.entity.Employee;
import com.hrgenius.employee.entity.EmploymentType;
import com.hrgenius.security.Role;
import com.hrgenius.user.entity.UserAccount;
import com.hrgenius.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;
import java.util.List;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:hrgenius;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "logging.level.root=INFO",
        "logging.level.org.springframework.web.servlet=INFO",
        "logging.level.org.springframework.jdbc=INFO",
        "logging.level.org.springframework.security=INFO",
        "logging.level.org.hibernate.SQL=WARN",
        "app.security.jwt-secret=test-secret-material-that-is-at-least-32-bytes-long",
        "app.bootstrap-admin.email=bootstrap@example.test",
        "app.bootstrap-admin.password=Bootstrap-Password-123"
})
@AutoConfigureMockMvc
class HRGeniusApplicationTests {
    private static final String EMAIL = "person@example.test";
    private static final String PASSWORD = "Correct-Password-123";
    private static final String REFRESH_COOKIE = "hrgenius_refresh";

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository users;
    @Autowired private RefreshTokenRepository refreshTokens;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EmployeeRepository employees;
    @Autowired private EmployeeHistoryRepository employeeHistory;
    @Autowired private LeaveRequestRepository leaveRequests;
    @Autowired private LeaveBalanceRepository leaveBalances;
    @Autowired private LeaveTypeRepository leaveTypes;
    @Autowired private LeaveAccrualRuleRepository accrualRules;
    @Autowired private LeaveAccrualLedgerRepository accrualLedger;
    @Autowired private AttendanceRecordRepository attendanceRecords;
    @Autowired private AttendanceCorrectionRepository attendanceCorrections;
    @Autowired private AttendanceService attendanceService;
    @Autowired private SalaryAssignmentRepository salaryAssignments;
    @Autowired private PayrollRunRepository payrollRuns;
    @Autowired private PayrollItemRepository payrollItems;
    @Autowired private LeaveHolidayRepository leaveHolidays;
    @Autowired private DepartmentRepository departments;
    @Autowired private DesignationRepository designations;
    @Autowired private LocationRepository locations;
    @Autowired private OrganizationProfileRepository organizationProfiles;
    @Autowired private OfferRepository offers;
    @Autowired private InterviewRepository interviews;
    @Autowired private ApplicationHistoryRepository applicationHistory;
    @Autowired private JobApplicationRepository applications;
    @Autowired private CandidateRepository candidates;
    @Autowired private JobOpeningRepository openings;
    @Autowired private OnboardingDocumentRepository onboardingDocuments;
    @Autowired private OnboardingTaskRepository onboardingTasks;
    @Autowired private OnboardingPlanRepository onboardingPlans;
    @Autowired private OnboardingTemplateRepository onboardingTemplates;
    @Autowired private PerformanceReviewRepository performanceReviews;
    @Autowired private AnnouncementRepository announcements;
    @Autowired private InAppNotificationRepository inAppNotifications;

    @BeforeEach
    void prepareAccount() {
        offers.deleteAll();
        inAppNotifications.deleteAll();
        announcements.deleteAll();
        performanceReviews.deleteAll();
        interviews.deleteAll();
        applicationHistory.deleteAll();
        applications.deleteAll();
        candidates.deleteAll();
        openings.deleteAll();
        onboardingDocuments.deleteAll(); onboardingTasks.deleteAll(); onboardingPlans.deleteAll(); onboardingTemplates.deleteAll();
        leaveRequests.deleteAll();
        attendanceCorrections.deleteAll();
        attendanceRecords.deleteAll();
        payrollItems.deleteAll();payrollRuns.deleteAll();salaryAssignments.deleteAll();
        leaveHolidays.deleteAll();
        accrualLedger.deleteAll();
        leaveBalances.deleteAll();
        accrualRules.deleteAll();
        employeeHistory.deleteAll();
        employees.deleteAll();
        leaveTypes.deleteAll();
        designations.deleteAll();
        departments.deleteAll();
        locations.deleteAll();
        organizationProfiles.deleteAll();
        refreshTokens.deleteAll();
        users.deleteAll();
        users.save(new UserAccount(EMAIL, passwordEncoder.encode(PASSWORD), "Test", "Person", Set.of(Role.EMPLOYEE)));
    }

    @Test
    void healthEndpointReportsApplicationUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void performanceReviewMovesFromEmployeeSelfReviewToAssignedManagerCompletion() throws Exception {
        String managerEmail="manager@example.test", employeeEmail="reviewee@example.test";
        UserAccount managerAccount=new UserAccount(managerEmail,passwordEncoder.encode(PASSWORD),"Taylor","Manager",Set.of(Role.MANAGER));
        UserAccount employeeAccount=new UserAccount(employeeEmail,passwordEncoder.encode(PASSWORD),"Jamie","Reviewee",Set.of(Role.EMPLOYEE));
        users.save(managerAccount); users.save(employeeAccount);
        Employee manager=employees.save(new Employee("MGR-1","Taylor","Manager",managerEmail,null,null,null,null,null,null,null,null,null,null,EmploymentType.FULL_TIME,LocalDate.now(),null,null,null,null,null,null,null));
        Employee reviewee=employees.save(new Employee("REV-1","Jamie","Reviewee",employeeEmail,null,null,null,null,null,null,null,null,null,null,EmploymentType.FULL_TIME,LocalDate.now(),null,null,null,null,null,manager,null));
        PerformanceReview review=performanceReviews.save(new PerformanceReview("FY 2026",reviewee,manager));
        String employeeToken=tokenFor(employeeEmail,PASSWORD), managerToken=tokenFor(managerEmail,PASSWORD);
        mockMvc.perform(get("/api/v1/performance/me").header("Authorization","Bearer "+employeeToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].cycleName").value("FY 2026"));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/v1/performance/"+review.getId()+"/self")
                        .header("Authorization","Bearer "+employeeToken).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"summary\":\"Delivered the migration milestone\",\"rating\":4}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("MANAGER_REVIEW"));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/v1/performance/"+review.getId()+"/manager")
                        .header("Authorization","Bearer "+managerToken).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"feedback\":\"Strong delivery\",\"rating\":5}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void announcementsRemainHiddenAsDraftAndBecomeVisibleAfterPublish() throws Exception {
        employees.save(new Employee("ANN-EMP","Test","Person",EMAIL,null,null,null,null,null,null,null,null,null,null,EmploymentType.FULL_TIME,LocalDate.now(),null,null,null,null,null,null,null));
        String adminEmail="announcement-admin@example.test";
        users.save(new UserAccount(adminEmail,passwordEncoder.encode(PASSWORD),"Notice","Admin",Set.of(Role.HR_MANAGER)));
        String adminToken=tokenFor(adminEmail,PASSWORD), employeeToken=loginAndGetAccessToken();
        String body="{\"title\":\"Benefits enrollment\",\"body\":\"Enrollment opens next week.\",\"priority\":\"HIGH\"}";
        long id=com.fasterxml.jackson.databind.json.JsonMapper.builder().build().readTree(
                mockMvc.perform(post("/api/v1/announcements").header("Authorization","Bearer "+adminToken).contentType(MediaType.APPLICATION_JSON).content(body))
                        .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("DRAFT")).andReturn().getResponse().getContentAsString()).get("id").asLong();
        mockMvc.perform(get("/api/v1/announcements/me").header("Authorization","Bearer "+employeeToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(0));
        mockMvc.perform(post("/api/v1/announcements/"+id+"/publish").header("Authorization","Bearer "+adminToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("PUBLISHED"));
        mockMvc.perform(get("/api/v1/announcements/me").header("Authorization","Bearer "+employeeToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].title").value("Benefits enrollment"));
        mockMvc.perform(get("/api/v1/notifications/me/unread-count").header("Authorization","Bearer "+employeeToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.count").value(1));
        long notificationId=inAppNotifications.findAll().getFirst().getId();
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/v1/notifications/me/"+notificationId+"/read").header("Authorization","Bearer "+employeeToken))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/v1/notifications/me/unread-count").header("Authorization","Bearer "+employeeToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.count").value(0));
    }

    @Test
    void headcountReportReturnsLiveTotalsDepartmentCountsAndDateFilteredJoiners() throws Exception {
        LocalDate today=LocalDate.now();
        Employee active=employees.save(new Employee("RPT-1","Active","Person","active-report@example.test",null,null,null,null,null,null,null,null,null,null,EmploymentType.FULL_TIME,today,null,null,null,null,null,null,null));
        Employee separated=employees.save(new Employee("RPT-2","Former","Person","former-report@example.test",null,null,null,null,null,null,null,null,null,null,EmploymentType.FULL_TIME,today.minusDays(1),null,null,null,null,null,null,null));
        separated.setStatus(com.hrgenius.employee.entity.EmployeeStatus.SEPARATED); employees.save(separated);
        String adminEmail="report-hr@example.test";
        users.save(new UserAccount(adminEmail,passwordEncoder.encode(PASSWORD),"Report","HR",Set.of(Role.HR_MANAGER)));
        String adminToken=tokenFor(adminEmail,PASSWORD);
        mockMvc.perform(get("/api/v1/reports/headcount").param("from",today.toString()).param("to",today.toString()).header("Authorization","Bearer "+adminToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalEmployees").value(2))
                .andExpect(jsonPath("$.activeEmployees").value(1)).andExpect(jsonPath("$.inactiveEmployees").value(1))
                .andExpect(jsonPath("$.newJoiners").value(1)).andExpect(jsonPath("$.departments[0].department").value("Unassigned"));
    }

    @Test
    void reportCatalogRunsEveryReportAndExportsCsvAndExcelForAuthorizedHr() throws Exception {
        String reportHr="reports@example.test";
        users.save(new UserAccount(reportHr,passwordEncoder.encode(PASSWORD),"Report","Manager",Set.of(Role.HR_MANAGER)));
        String hrToken=tokenFor(reportHr,PASSWORD), employeeToken=loginAndGetAccessToken();
        String from=LocalDate.now().withDayOfYear(1).toString(),to=LocalDate.now().toString();
        mockMvc.perform(get("/api/v1/reports/headcount/data").param("from",from).param("to",to).header("Authorization","Bearer "+hrToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.report").value("headcount"));
        for(String type:List.of("employee-directory","department-wise","designation-wise","attendance","late-arrivals","absences","overtime","leave","leave-balances","recruitment","hiring-funnel","onboarding","payroll","performance")){
            mockMvc.perform(get("/api/v1/reports/"+type).param("from",from).param("to",to).param("page","0").param("size","25").header("Authorization","Bearer "+hrToken))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.report").value(type)).andExpect(jsonPath("$.columns").isArray());
        }
        mockMvc.perform(get("/api/v1/reports/employee-directory/export").param("format","csv").header("Authorization","Bearer "+hrToken))
                .andExpect(status().isOk()).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("Content-Disposition",org.hamcrest.Matchers.containsString(".csv")));
        MvcResult workbook=mockMvc.perform(get("/api/v1/reports/employee-directory/export").param("format","xlsx").header("Authorization","Bearer "+hrToken))
                .andExpect(status().isOk()).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().contentTypeCompatibleWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andReturn();
        assertThat(new String(workbook.getResponse().getContentAsByteArray(),0,2,java.nio.charset.StandardCharsets.US_ASCII)).isEqualTo("PK");
        try(java.util.zip.ZipInputStream zip=new java.util.zip.ZipInputStream(new java.io.ByteArrayInputStream(workbook.getResponse().getContentAsByteArray()))){
            java.util.Set<String> entries=new java.util.HashSet<>();java.util.zip.ZipEntry entry;while((entry=zip.getNextEntry())!=null)entries.add(entry.getName());
            assertThat(entries).contains("xl/workbook.xml","xl/worksheets/sheet1.xml");
        }catch(java.io.IOException ex){throw new AssertionError("Excel workbook was not a valid ZIP package",ex);}
        mockMvc.perform(get("/api/v1/reports/payroll").header("Authorization","Bearer "+employeeToken)).andExpect(status().isForbidden());
    }

    @Test
    void loginReturnsShortLivedAccessTokenAndHttpOnlyRefreshCookieWithoutSecrets() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"PERSON@example.test\",\"password\":\"Correct-Password-123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresInSeconds").value(900))
                .andExpect(jsonPath("$.user.email").value(EMAIL))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn();

        String cookie = result.getResponse().getHeader("Set-Cookie");
        assertThat(cookie).contains("HttpOnly", "SameSite=Strict", "Path=/api/v1/auth");
        assertThat(cookie).doesNotContain(PASSWORD);
        assertThat(refreshTokens.count()).isEqualTo(1);
    }

    @Test
    void protectedProfileRequiresValidAccessToken() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized());
        String accessToken = loginAndGetAccessToken();
        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.roles[0]").value("EMPLOYEE"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void refreshRotatesTokenAndLogoutRevokesIt() throws Exception {
        MvcResult login = login();
        jakarta.servlet.http.Cookie first = login.getResponse().getCookie(REFRESH_COOKIE);
        assertThat(first).isNotNull();

        MvcResult refresh = mockMvc.perform(post("/api/v1/auth/refresh").cookie(first))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn();
        jakarta.servlet.http.Cookie rotated = refresh.getResponse().getCookie(REFRESH_COOKIE);
        assertThat(rotated).isNotNull();
        assertThat(rotated.getValue()).isNotEqualTo(first.getValue());
        assertThat(refreshTokens.count()).isEqualTo(2);

        mockMvc.perform(post("/api/v1/auth/refresh").cookie(first)).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/v1/auth/logout").cookie(rotated)).andExpect(status().isNoContent());
        mockMvc.perform(post("/api/v1/auth/refresh").cookie(rotated)).andExpect(status().isUnauthorized());
    }

    @Test
    void repeatedBadPasswordsLockAccountAndReturnGenericAuthenticationError() throws Exception {
        for (int attempt = 0; attempt < 5; attempt++) {
            mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"person@example.test\",\"password\":\"incorrect\"}"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.detail").value("Authentication failed"));
        }
        assertThat(users.findByEmail(EMAIL).orElseThrow().getFailedLoginAttempts()).isEqualTo(5);
        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"person@example.test\",\"password\":\"Correct-Password-123\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changingPasswordRevokesRefreshSessions() throws Exception {
        MvcResult login = login();
        jakarta.servlet.http.Cookie refreshCookie = login.getResponse().getCookie(REFRESH_COOKIE);
        String accessToken = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(login.getResponse().getContentAsString()).get("accessToken").asText();

        mockMvc.perform(post("/api/v1/auth/change-password")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"Correct-Password-123\",\"newPassword\":\"Another-Password-456\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/v1/auth/refresh").cookie(refreshCookie)).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"person@example.test\",\"password\":\"Another-Password-456\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void departmentWritesRequireAdministratorAndRejectDuplicateCodes() throws Exception {
        String employeeToken = loginAndGetAccessToken();
        mockMvc.perform(post("/api/v1/departments")
                        .header("Authorization", "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"ENG\",\"name\":\"Engineering\"}"))
                .andExpect(status().isForbidden());

        users.save(new UserAccount("admin@example.test", passwordEncoder.encode("Admin-Password-123"),
                "Site", "Admin", Set.of(Role.ADMIN)));
        MvcResult adminLogin = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@example.test\",\"password\":\"Admin-Password-123\"}"))
                .andExpect(status().isOk()).andReturn();
        String adminToken = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(adminLogin.getResponse().getContentAsString()).get("accessToken").asText();

        mockMvc.perform(post("/api/v1/departments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"ENG\",\"name\":\"Engineering\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("ENG"));
        mockMvc.perform(post("/api/v1/departments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"eng\",\"name\":\"Platform\"}"))
                .andExpect(status().isConflict());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/departments")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void employeeDirectoryIsRoleLimitedAndSelfProfileUsesAuthenticatedEmail() throws Exception {
        String employeeToken = loginAndGetAccessToken();
        mockMvc.perform(get("/api/v1/employees").header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isForbidden());

        users.save(new UserAccount("admin@example.test", passwordEncoder.encode("Admin-Password-123"),
                "Site", "Admin", Set.of(Role.ADMIN)));
        MvcResult adminLogin = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@example.test\",\"password\":\"Admin-Password-123\"}"))
                .andExpect(status().isOk()).andReturn();
        String adminToken = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(adminLogin.getResponse().getContentAsString()).get("accessToken").asText();
        mockMvc.perform(post("/api/v1/employees").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeNumber\":\"E-100\",\"firstName\":\"Test\",\"lastName\":\"Person\","
                                + "\"workEmail\":\"person@example.test\",\"personalEmail\":\"private@example.test\","
                                + "\"phone\":\"555-0100\",\"addressLine1\":\"Private address\","
                                + "\"employmentType\":\"FULL_TIME\",\"dateOfJoining\":\"2024-01-15\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeNumber").value("E-100"))
                .andExpect(jsonPath("$.personalEmail").doesNotExist())
                .andExpect(jsonPath("$.addressLine1").doesNotExist());
        mockMvc.perform(get("/api/v1/employees/me").header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workEmail").value(EMAIL))
                .andExpect(jsonPath("$.id").isNumber());
        mockMvc.perform(put("/api/v1/employees/me").header("Authorization", "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"personalEmail\":\"person.private@example.test\",\"phone\":\"555-0110\","
                                + "\"addressLine1\":\"12 Example Road\",\"firstName\":\"Injected\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.personalEmail").value("person.private@example.test"))
                .andExpect(jsonPath("$.addressLine1").value("12 Example Road"))
                .andExpect(jsonPath("$.firstName").value("Test"));
        MvcResult employeePage = mockMvc.perform(get("/api/v1/employees").param("q", "E-100")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1)).andReturn();
        long employeeId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(employeePage.getResponse().getContentAsString()).get("content").get(0).get("id").asLong();
        mockMvc.perform(get("/api/v1/employees/" + employeeId).header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.personalEmail").value("person.private@example.test"))
                .andExpect(jsonPath("$.addressLine1").value("12 Example Road"));
        mockMvc.perform(get("/api/v1/employees/" + employeeId).header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void leaveRequestRequiresBalanceAndApprovalConsumesItWithoutAllowingSelfApproval() throws Exception {
        users.save(new UserAccount("admin@example.test", passwordEncoder.encode("Admin-Password-123"),
                "Site", "Admin", Set.of(Role.ADMIN)));
        String adminToken = tokenFor("admin@example.test", "Admin-Password-123");
        String employeeToken = loginAndGetAccessToken();

        MvcResult employeeResult = mockMvc.perform(post("/api/v1/employees").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeNumber\":\"LV-100\",\"firstName\":\"Test\",\"lastName\":\"Person\","
                                + "\"workEmail\":\"person@example.test\",\"employmentType\":\"FULL_TIME\","
                                + "\"dateOfJoining\":\"2024-01-15\"}"))
                .andExpect(status().isCreated()).andReturn();
        long employeeId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(employeeResult.getResponse().getContentAsString()).get("id").asLong();
        MvcResult typeResult = mockMvc.perform(post("/api/v1/leave/types").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"ANNUAL\",\"name\":\"Annual leave\"}"))
                .andExpect(status().isCreated()).andReturn();
        long leaveTypeId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(typeResult.getResponse().getContentAsString()).get("id").asLong();
        short year = (short) LocalDate.now().getYear();
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/v1/leave/balances")
                        .header("Authorization", "Bearer " + adminToken).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeId\":" + employeeId + ",\"leaveTypeId\":" + leaveTypeId
                                + ",\"year\":" + year + ",\"entitledDays\":5}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.remainingDays").value(5));

        String accrualRule = "{\"leaveTypeId\":" + leaveTypeId + ",\"monthlyDays\":2,\"annualCap\":10,\"effectiveFrom\":\"" + LocalDate.now().withDayOfYear(1) + "\"}";
        mockMvc.perform(put("/api/v1/leave/accrual-rules").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content(accrualRule))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.active").value(true));
        String period = YearMonth.now().toString();
        mockMvc.perform(post("/api/v1/leave/accrual-runs").param("period", period).header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.processedEmployees").value(1)).andExpect(jsonPath("$.totalGrantedDays").value(2));
        mockMvc.perform(post("/api/v1/leave/accrual-runs").param("period", period).header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.processedEmployees").value(0)).andExpect(jsonPath("$.totalGrantedDays").value(0));

        LocalDate start = LocalDate.now().with(java.time.temporal.TemporalAdjusters.next(java.time.DayOfWeek.MONDAY)).plusWeeks(2);
        mockMvc.perform(post("/api/v1/leave/calendar/holidays").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"date\":\"" + start.plusDays(1) + "\",\"name\":\"Company holiday\"}"))
                .andExpect(status().isCreated());
        String requestBody = "{\"leaveTypeId\":" + leaveTypeId + ",\"startDate\":\"" + start
                + "\",\"endDate\":\"" + start.plusDays(2) + "\",\"reason\":\"Personal time\"}";
        MvcResult requestResult = mockMvc.perform(post("/api/v1/leave/requests")
                        .header("Authorization", "Bearer " + employeeToken).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.requestedDays").value(2)).andReturn();
        long requestId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(requestResult.getResponse().getContentAsString()).get("id").asLong();
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(
                        "/api/v1/leave/requests/" + requestId + "/decision")
                        .header("Authorization", "Bearer " + employeeToken).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"approved\":true}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(
                        "/api/v1/leave/requests/" + requestId + "/decision")
                        .header("Authorization", "Bearer " + adminToken).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"approved\":true,\"comment\":\"Approved\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("APPROVED"));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(
                        "/api/v1/leave/balances/employee/" + employeeId).param("year", Short.toString(year))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].usedDays").value(2))
                  .andExpect(jsonPath("$[0].remainingDays").value(5));
        mockMvc.perform(post("/api/v1/leave/requests").header("Authorization", "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isConflict());
    }

    @Test
    void attendanceCheckInOutAndCorrectionApprovalEnforceDailyRules() throws Exception {
        users.save(new UserAccount("admin@example.test", passwordEncoder.encode("Admin-Password-123"), "Site", "Admin", Set.of(Role.ADMIN)));
        String adminToken=tokenFor("admin@example.test","Admin-Password-123");
        String employeeToken=loginAndGetAccessToken();
        mockMvc.perform(post("/api/v1/employees").header("Authorization","Bearer "+adminToken).contentType(MediaType.APPLICATION_JSON)
                .content("{\"employeeNumber\":\"ATT-1\",\"firstName\":\"Test\",\"lastName\":\"Person\",\"workEmail\":\"person@example.test\",\"employmentType\":\"FULL_TIME\",\"dateOfJoining\":\"2024-01-15\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/v1/attendance/policy").header("Authorization","Bearer "+adminToken).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"workdayStart\":\"09:00\",\"standardWorkMinutes\":480,\"lateGraceMinutes\":15,\"halfDayThresholdMinutes\":240,\"overtimeAfterMinutes\":480}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.lateGraceMinutes").value(15));
        mockMvc.perform(post("/api/v1/attendance/check-in").header("Authorization","Bearer "+employeeToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.checkInAt").isNotEmpty());
        mockMvc.perform(post("/api/v1/attendance/check-in").header("Authorization","Bearer "+employeeToken))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/v1/attendance/check-out").header("Authorization","Bearer "+employeeToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.checkOutAt").isNotEmpty()).andExpect(jsonPath("$.status").value("HALF_DAY"));
        mockMvc.perform(post("/api/v1/attendance/check-out").header("Authorization","Bearer "+employeeToken))
                .andExpect(status().isConflict());

        LocalDate correctionDay=LocalDate.now().minusDays(1);
        MvcResult correction=mockMvc.perform(post("/api/v1/attendance/corrections").header("Authorization","Bearer "+employeeToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"workDate\":\""+correctionDay+"\",\"checkIn\":\"09:00\",\"checkOut\":\"17:00\",\"reason\":\"Forgot to check in\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("PENDING")).andReturn();
        long correctionId=com.fasterxml.jackson.databind.json.JsonMapper.builder().build().readTree(correction.getResponse().getContentAsString()).get("id").asLong();
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/v1/attendance/corrections/"+correctionId+"/decision")
                        .header("Authorization","Bearer "+adminToken).contentType(MediaType.APPLICATION_JSON).content("{\"approved\":true}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("APPROVED"));
        mockMvc.perform(get("/api/v1/attendance/me").param("from",correctionDay.withDayOfMonth(1).toString()).param("to",LocalDate.now().toString())
                        .header("Authorization","Bearer "+employeeToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void dailyAttendanceRollupMarksOnlyWorkdayAbsencesAndIsRepeatable() throws Exception {
        users.save(new UserAccount("admin@example.test", passwordEncoder.encode("Admin-Password-123"), "Site", "Admin", Set.of(Role.ADMIN)));
        String adminToken=tokenFor("admin@example.test","Admin-Password-123");
        mockMvc.perform(post("/api/v1/employees").header("Authorization","Bearer "+adminToken).contentType(MediaType.APPLICATION_JSON)
                .content("{\"employeeNumber\":\"ATT-2\",\"firstName\":\"Test\",\"lastName\":\"Person\",\"workEmail\":\"person@example.test\",\"employmentType\":\"FULL_TIME\",\"dateOfJoining\":\"2024-01-15\"}"))
                .andExpect(status().isCreated());
        LocalDate monday=LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).minusWeeks(1);
        assertThat(attendanceService.markAbsences(monday)).isEqualTo(1);
        assertThat(attendanceService.markAbsences(monday)).isZero();
        LocalDate sunday=monday.plusDays(6);
        assertThat(attendanceService.markAbsences(sunday)).isZero();
        String employeeToken=loginAndGetAccessToken();
        mockMvc.perform(get("/api/v1/attendance/me").param("from",monday.toString()).param("to",monday.toString())
                        .header("Authorization","Bearer "+employeeToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].status").value("ABSENT"));
    }

    @Test
    void payrollRunSnapshotsSalaryApprovesAndExposesOnlyOwnPayslip() throws Exception {
        users.save(new UserAccount("admin@example.test",passwordEncoder.encode("Admin-Password-123"),"Site","Admin",Set.of(Role.ADMIN)));
        String adminToken=tokenFor("admin@example.test","Admin-Password-123");String employeeToken=loginAndGetAccessToken();
        MvcResult employeeResult=mockMvc.perform(post("/api/v1/employees").header("Authorization","Bearer "+adminToken).contentType(MediaType.APPLICATION_JSON)
                .content("{\"employeeNumber\":\"PAY-1\",\"firstName\":\"Test\",\"lastName\":\"Person\",\"workEmail\":\"person@example.test\",\"employmentType\":\"FULL_TIME\",\"dateOfJoining\":\"2024-01-15\"}"))
                .andExpect(status().isCreated()).andReturn();
        long employeeId=com.fasterxml.jackson.databind.json.JsonMapper.builder().build().readTree(employeeResult.getResponse().getContentAsString()).get("id").asLong();
        mockMvc.perform(put("/api/v1/payroll/assignments").header("Authorization","Bearer "+adminToken).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeId\":"+employeeId+",\"currency\":\"USD\",\"monthlyGross\":5000,\"fixedDeduction\":500,\"effectiveFrom\":\""+LocalDate.now().withDayOfMonth(1)+"\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.currency").value("USD"));
        MvcResult run=mockMvc.perform(post("/api/v1/payroll/runs").param("period",YearMonth.now().toString()).header("Authorization","Bearer "+adminToken))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.totalNet").value(4500)).andExpect(jsonPath("$.employeeCount").value(1)).andReturn();
        long runId=com.fasterxml.jackson.databind.json.JsonMapper.builder().build().readTree(run.getResponse().getContentAsString()).get("id").asLong();
        mockMvc.perform(post("/api/v1/payroll/runs/"+runId+"/approve").header("Authorization","Bearer "+adminToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("APPROVED"));
        mockMvc.perform(get("/api/v1/payroll/me").header("Authorization","Bearer "+employeeToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].netPay").value(4500)).andExpect(jsonPath("$[0].currency").value("USD"));
        mockMvc.perform(get("/api/v1/payroll/runs").header("Authorization","Bearer "+employeeToken)).andExpect(status().isForbidden());
    }

    @Test
    void recruitmentWorkflowMovesCandidateThroughInterviewOfferAndEmployeeConversion() throws Exception {
        String employeeToken = loginAndGetAccessToken();
        mockMvc.perform(post("/api/v1/recruitment/openings").header("Authorization", "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Backend Engineer\",\"vacancies\":1,\"description\":\"Build services\","
                                + "\"employmentType\":\"FULL_TIME\",\"openingDate\":\"" + LocalDate.now() + "\"}"))
                .andExpect(status().isForbidden());

        users.save(new UserAccount("admin@example.test", passwordEncoder.encode("Admin-Password-123"),
                "Site", "Admin", Set.of(Role.ADMIN)));
        String adminToken = tokenFor("admin@example.test", "Admin-Password-123");
        MvcResult employeeResult = mockMvc.perform(post("/api/v1/employees").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeNumber\":\"REC-1\",\"firstName\":\"Test\",\"lastName\":\"Person\","
                                + "\"workEmail\":\"person@example.test\",\"employmentType\":\"FULL_TIME\","
                                + "\"dateOfJoining\":\"2024-01-15\"}"))
                .andExpect(status().isCreated()).andReturn();
        long interviewerId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(employeeResult.getResponse().getContentAsString()).get("id").asLong();
        MvcResult openingResult = mockMvc.perform(post("/api/v1/recruitment/openings").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Backend Engineer\",\"vacancies\":1,\"description\":\"Build services\","
                                + "\"employmentType\":\"FULL_TIME\",\"openingDate\":\"" + LocalDate.now() + "\"}"))
                .andExpect(status().isCreated()).andReturn();
        long openingId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(openingResult.getResponse().getContentAsString()).get("id").asLong();
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch(
                        "/api/v1/recruitment/openings/" + openingId + "/status")
                        .header("Authorization", "Bearer " + adminToken).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"OPEN\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("OPEN"));
        MvcResult candidateResult = mockMvc.perform(post("/api/v1/recruitment/candidates")
                        .header("Authorization", "Bearer " + adminToken).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Alex\",\"lastName\":\"Candidate\",\"email\":\"alex@example.test\",\"source\":\"Referral\"}"))
                .andExpect(status().isCreated()).andReturn();
        long candidateId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(candidateResult.getResponse().getContentAsString()).get("id").asLong();
        MvcResult applicationResult = mockMvc.perform(post("/api/v1/recruitment/applications")
                        .header("Authorization", "Bearer " + adminToken).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"openingId\":" + openingId + ",\"candidateId\":" + candidateId + "}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("APPLIED")).andReturn();
        long applicationId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(applicationResult.getResponse().getContentAsString()).get("id").asLong();
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch(
                        "/api/v1/recruitment/applications/" + applicationId + "/stage")
                        .header("Authorization", "Bearer " + adminToken).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"SCREENING\",\"note\":\"Meets baseline\"}"))
                .andExpect(status().isOk());
        String scheduled = OffsetDateTime.now(ZoneOffset.UTC).plusDays(3).withNano(0).toString();
        MvcResult interviewResult = mockMvc.perform(post("/api/v1/recruitment/interviews")
                        .header("Authorization", "Bearer " + adminToken).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"applicationId\":" + applicationId + ",\"interviewerId\":" + interviewerId
                                + ",\"scheduledAt\":\"" + scheduled + "\",\"durationMinutes\":60,\"interviewType\":\"VIDEO\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("SCHEDULED")).andReturn();
        long interviewId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(interviewResult.getResponse().getContentAsString()).get("id").asLong();
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(
                        "/api/v1/recruitment/interviews/" + interviewId + "/feedback")
                        .header("Authorization", "Bearer " + adminToken).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"feedback\":\"Strong technical interview\",\"rating\":5}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("COMPLETED"));
        LocalDate start = LocalDate.now().plusDays(60); LocalDate expiry = LocalDate.now().plusDays(30);
        MvcResult offerResult = mockMvc.perform(post("/api/v1/recruitment/offers")
                        .header("Authorization", "Bearer " + adminToken).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"applicationId\":" + applicationId + ",\"salary\":120000,\"currency\":\"INR\","
                                + "\"startDate\":\"" + start + "\",\"expiresOn\":\"" + expiry + "\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("DRAFT")).andReturn();
        long offerId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(offerResult.getResponse().getContentAsString()).get("id").asLong();
        mockMvc.perform(post("/api/v1/recruitment/offers/" + offerId + "/send").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("SENT"));
        mockMvc.perform(post("/api/v1/recruitment/offers/" + offerId + "/decision")
                        .header("Authorization", "Bearer " + adminToken).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accepted\":true}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("ACCEPTED"));
        MvcResult template = mockMvc.perform(post("/api/v1/onboarding/templates")
                        .header("Authorization", "Bearer " + adminToken).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Default hire plan\",\"tasks\":[{\"title\":\"Complete induction\",\"assigneeRole\":\"EMPLOYEE\",\"daysFromStart\":0,\"required\":true}]}"))
                .andExpect(status().isCreated()).andReturn();
        long onboardingTemplateId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(template.getResponse().getContentAsString()).get("id").asLong();
        mockMvc.perform(post("/api/v1/recruitment/applications/" + applicationId + "/convert")
                        .header("Authorization", "Bearer " + adminToken).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeNumber\":\"NEW-100\",\"workEmail\":\"alex@company.test\","
                                + "\"employmentType\":\"FULL_TIME\",\"dateOfJoining\":\"" + start + "\",\"onboardingTemplateId\":" + onboardingTemplateId + "}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.employeeNumber").value("NEW-100"));
        mockMvc.perform(get("/api/v1/onboarding/plans").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].status").value("NOT_STARTED"));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/recruitment/applications")
                        .param("status", "HIRED").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
    }

    private String tokenFor(String email, String password) throws Exception {
        String body = mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return com.fasterxml.jackson.databind.json.JsonMapper.builder().build().readTree(body).get("accessToken").asText();
    }

    private MvcResult login() throws Exception {
        return mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"person@example.test\",\"password\":\"Correct-Password-123\"}"))
                .andExpect(status().isOk()).andReturn();
    }

    private String loginAndGetAccessToken() throws Exception {
        String body = login().getResponse().getContentAsString();
        return com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(body).get("accessToken").asText();
    }
}
