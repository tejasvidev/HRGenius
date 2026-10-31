package com.hrgenius.recruitment.entity;

import com.hrgenius.employee.entity.Employee;
import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name = "job_applications", uniqueConstraints = @UniqueConstraint(name = "uk_application_candidate_opening", columnNames = {"opening_id", "candidate_id"}))
public class JobApplication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "opening_id", nullable = false) private JobOpening opening;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "candidate_id", nullable = false) private Candidate candidate;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 24) private ApplicationStatus status = ApplicationStatus.APPLIED;
    @Column(name = "applied_at", nullable = false, updatable = false) private Instant appliedAt;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "employee_id") private Employee employee;
    protected JobApplication() { }
    public JobApplication(JobOpening opening, Candidate candidate) { this.opening = opening; this.candidate = candidate; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public void setEmployee(Employee employee) { this.employee = employee; this.status = ApplicationStatus.HIRED; }
    @PrePersist void onCreate() { appliedAt = Instant.now(); }
    public Long getId() { return id; } public JobOpening getOpening() { return opening; }
    public Candidate getCandidate() { return candidate; } public ApplicationStatus getStatus() { return status; }
    public Employee getEmployee() { return employee; } public Instant getAppliedAt() { return appliedAt; }
}
