package com.hrgenius.employee.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "employee_history")
public class EmployeeHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "employee_id", nullable = false) private Employee employee;
    @Column(name = "change_type", nullable = false, length = 32) private String changeType;
    @Column(nullable = false, length = 1000) private String details;
    @Column(name = "effective_date", nullable = false) private LocalDate effectiveDate;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    protected EmployeeHistory() { }
    public EmployeeHistory(Employee employee, String changeType, String details, LocalDate effectiveDate) {
        this.employee = employee; this.changeType = changeType; this.details = details; this.effectiveDate = effectiveDate;
    }
    @PrePersist void onCreate() { createdAt = Instant.now(); }
    public String getChangeType() { return changeType; }
    public String getDetails() { return details; }
    public LocalDate getEffectiveDate() { return effectiveDate; }
    public Instant getCreatedAt() { return createdAt; }
}
