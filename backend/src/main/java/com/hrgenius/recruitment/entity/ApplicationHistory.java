package com.hrgenius.recruitment.entity;

import com.hrgenius.employee.entity.Employee;
import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name = "application_history")
public class ApplicationHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "application_id", nullable = false) private JobApplication application;
    @Column(name = "from_status", length = 24) private String fromStatus;
    @Column(name = "to_status", nullable = false, length = 24) private String toStatus;
    @Column(length = 1000) private String note;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "changed_by_employee_id") private Employee changedBy;
    @Column(name = "changed_at", nullable = false, updatable = false) private Instant changedAt;
    protected ApplicationHistory() { }
    public ApplicationHistory(JobApplication application, String from, String to, String note, Employee actor) {
        this.application = application; this.fromStatus = from; this.toStatus = to; this.note = note; this.changedBy = actor;
    }
    @PrePersist void onCreate() { changedAt = Instant.now(); }
}
