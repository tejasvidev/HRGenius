package com.hrgenius.recruitment.entity;

import com.hrgenius.employee.entity.Employee;
import com.hrgenius.employee.entity.EmploymentType;
import com.hrgenius.organization.entity.Department;
import com.hrgenius.organization.entity.Designation;
import com.hrgenius.organization.entity.Location;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name = "job_openings")
public class JobOpening {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 160) private String title;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "department_id") private Department department;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "designation_id") private Designation designation;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "location_id") private Location location;
    @Column(nullable = false) private int vacancies;
    @Column(nullable = false, columnDefinition = "text") private String description;
    @Column(name = "required_skills", length = 1000) private String requiredSkills;
    @Column(name = "min_experience_years", precision = 4, scale = 1) private BigDecimal minExperienceYears;
    @Column(name = "max_experience_years", precision = 4, scale = 1) private BigDecimal maxExperienceYears;
    @Enumerated(EnumType.STRING) @Column(name = "employment_type", nullable = false, length = 24) private EmploymentType employmentType;
    @Column(name = "salary_min", precision = 13, scale = 2) private BigDecimal salaryMin;
    @Column(name = "salary_max", precision = 13, scale = 2) private BigDecimal salaryMax;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "hiring_manager_id") private Employee hiringManager;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "recruiter_id") private Employee recruiter;
    @Column(name = "opening_date", nullable = false) private LocalDate openingDate;
    @Column(name = "closing_date") private LocalDate closingDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private JobOpeningStatus status = JobOpeningStatus.DRAFT;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    protected JobOpening() { }
    public JobOpening(String title, Department department, Designation designation, Location location, int vacancies,
            String description, String requiredSkills, BigDecimal minExperienceYears, BigDecimal maxExperienceYears,
            EmploymentType employmentType, BigDecimal salaryMin, BigDecimal salaryMax, Employee hiringManager,
            Employee recruiter, LocalDate openingDate, LocalDate closingDate) {
        this.title = title.trim(); this.department = department; this.designation = designation; this.location = location;
        this.vacancies = vacancies; this.description = description.trim(); this.requiredSkills = clean(requiredSkills);
        this.minExperienceYears = minExperienceYears; this.maxExperienceYears = maxExperienceYears;
        this.employmentType = employmentType; this.salaryMin = salaryMin; this.salaryMax = salaryMax;
        this.hiringManager = hiringManager; this.recruiter = recruiter; this.openingDate = openingDate; this.closingDate = closingDate;
    }
    public void update(JobOpening other) {
        title = other.title; department = other.department; designation = other.designation; location = other.location;
        vacancies = other.vacancies; description = other.description; requiredSkills = other.requiredSkills;
        minExperienceYears = other.minExperienceYears; maxExperienceYears = other.maxExperienceYears;
        employmentType = other.employmentType; salaryMin = other.salaryMin; salaryMax = other.salaryMax;
        hiringManager = other.hiringManager; recruiter = other.recruiter; openingDate = other.openingDate; closingDate = other.closingDate;
    }
    public void setStatus(JobOpeningStatus status) { this.status = status; }
    private static String clean(String v) { return v == null || v.isBlank() ? null : v.trim(); }
    @PrePersist void onCreate() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
    public Long getId() { return id; } public String getTitle() { return title; }
    public Department getDepartment() { return department; } public Designation getDesignation() { return designation; }
    public Location getLocation() { return location; } public int getVacancies() { return vacancies; }
    public String getDescription() { return description; } public String getRequiredSkills() { return requiredSkills; }
    public BigDecimal getMinExperienceYears() { return minExperienceYears; } public BigDecimal getMaxExperienceYears() { return maxExperienceYears; }
    public EmploymentType getEmploymentType() { return employmentType; } public BigDecimal getSalaryMin() { return salaryMin; }
    public BigDecimal getSalaryMax() { return salaryMax; } public Employee getHiringManager() { return hiringManager; }
    public Employee getRecruiter() { return recruiter; } public LocalDate getOpeningDate() { return openingDate; }
    public LocalDate getClosingDate() { return closingDate; } public JobOpeningStatus getStatus() { return status; }
}
