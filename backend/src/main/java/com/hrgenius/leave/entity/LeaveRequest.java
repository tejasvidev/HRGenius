package com.hrgenius.leave.entity;

import com.hrgenius.employee.entity.Employee;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "leave_requests")
public class LeaveRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "employee_id", nullable = false) private Employee employee;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "leave_type_id", nullable = false) private LeaveType leaveType;
    @Column(name = "start_date", nullable = false) private LocalDate startDate;
    @Column(name = "end_date", nullable = false) private LocalDate endDate;
    @Column(name = "requested_days", nullable = false, precision = 6, scale = 2) private BigDecimal requestedDays;
    @Column(nullable = false, length = 1000) private String reason;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private LeaveRequestStatus status = LeaveRequestStatus.PENDING;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "reviewer_id") private Employee reviewer;
    @Column(name = "reviewer_comment", length = 1000) private String reviewerComment;
    @Column(name = "decided_at") private Instant decidedAt;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    protected LeaveRequest() { }
    public LeaveRequest(Employee employee, LeaveType leaveType, LocalDate start, LocalDate end,
                        BigDecimal days, String reason) {
        this.employee = employee; this.leaveType = leaveType; this.startDate = start; this.endDate = end;
        this.requestedDays = days; this.reason = reason.trim();
    }
    public void decide(Employee reviewer, boolean approve, String comment) {
        this.status = approve ? LeaveRequestStatus.APPROVED : LeaveRequestStatus.REJECTED;
        this.reviewer = reviewer; this.reviewerComment = comment == null || comment.isBlank() ? null : comment.trim();
        this.decidedAt = Instant.now();
    }
    public void cancel() {
        if (status != LeaveRequestStatus.PENDING) throw new IllegalStateException("Only pending requests can be cancelled");
        status = LeaveRequestStatus.CANCELLED;
    }
    @PrePersist void onCreate() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
    public Long getId() { return id; } public Employee getEmployee() { return employee; }
    public LeaveType getLeaveType() { return leaveType; } public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; } public BigDecimal getRequestedDays() { return requestedDays; }
    public String getReason() { return reason; } public LeaveRequestStatus getStatus() { return status; }
    public Employee getReviewer() { return reviewer; } public String getReviewerComment() { return reviewerComment; }
    public Instant getDecidedAt() { return decidedAt; } public Instant getCreatedAt() { return createdAt; }
}
