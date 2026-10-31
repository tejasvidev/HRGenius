package com.hrgenius.performance.entity;

import com.hrgenius.employee.entity.Employee;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "performance_reviews", uniqueConstraints = @UniqueConstraint(name = "uk_performance_cycle_employee", columnNames = {"cycle_name", "employee_id"}))
public class PerformanceReview {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "cycle_name", nullable = false, length = 120) private String cycleName;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "employee_id") private Employee employee;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "reviewer_id") private Employee reviewer;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 24) private PerformanceReviewStatus status = PerformanceReviewStatus.SELF_REVIEW;
    @Column(name = "self_summary", length = 3000) private String selfSummary;
    @Column(name = "self_rating") private Integer selfRating;
    @Column(name = "manager_feedback", length = 3000) private String managerFeedback;
    @Column(name = "manager_rating") private Integer managerRating;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    protected PerformanceReview() { }
    public PerformanceReview(String cycleName, Employee employee, Employee reviewer) { this.cycleName=cycleName.trim(); this.employee=employee; this.reviewer=reviewer; }
    public void selfReview(String summary, int rating) { if(status != PerformanceReviewStatus.SELF_REVIEW) throw new IllegalStateException("Self review is closed"); selfSummary=summary.trim(); selfRating=rating; status=PerformanceReviewStatus.MANAGER_REVIEW; }
    public void managerReview(String feedback, int rating) { if(status != PerformanceReviewStatus.MANAGER_REVIEW) throw new IllegalStateException("Self review must be submitted first"); managerFeedback=feedback.trim(); managerRating=rating; status=PerformanceReviewStatus.COMPLETED; }
    @PrePersist void onCreate(){createdAt=Instant.now();updatedAt=createdAt;} @PreUpdate void onUpdate(){updatedAt=Instant.now();}
    public Long getId(){return id;} public String getCycleName(){return cycleName;} public Employee getEmployee(){return employee;} public Employee getReviewer(){return reviewer;} public Instant getCreatedAt(){return createdAt;}
    public PerformanceReviewStatus getStatus(){return status;} public String getSelfSummary(){return selfSummary;} public Integer getSelfRating(){return selfRating;}
    public String getManagerFeedback(){return managerFeedback;} public Integer getManagerRating(){return managerRating;}
}
