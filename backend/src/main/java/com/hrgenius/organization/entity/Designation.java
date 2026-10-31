package com.hrgenius.organization.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.Locale;

@Entity
@Table(name = "designations", uniqueConstraints = {
        @UniqueConstraint(name = "uk_designations_code", columnNames = "code"),
        @UniqueConstraint(name = "uk_designations_department_title", columnNames = {"department_id", "title"})
})
public class Designation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "fk_designations_department"))
    private Department department;
    @Column(nullable = false, length = 24) private String code;
    @Column(nullable = false, length = 120) private String title;
    @Column(name = "job_grade", length = 40) private String jobGrade;
    @Column(length = 500) private String description;
    @Column(nullable = false) private boolean active = true;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;

    protected Designation() { }
    public Designation(Department department, String code, String title, String jobGrade, String description) {
        update(department, code, title, jobGrade, description);
    }
    public void update(Department department, String code, String title, String jobGrade, String description) {
        this.department = department;
        this.code = code.trim().toUpperCase(Locale.ROOT);
        this.title = title.trim();
        this.jobGrade = trimToNull(jobGrade);
        this.description = trimToNull(description);
    }
    public void deactivate() { active = false; }
    public void activate() { active = true; }
    private static String trimToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    @PrePersist void onCreate() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
    public Long getId() { return id; }
    public Department getDepartment() { return department; }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public String getJobGrade() { return jobGrade; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
}
