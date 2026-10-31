package com.hrgenius.recruitment.entity;

import com.hrgenius.employee.entity.Employee;
import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name = "interviews")
public class Interview {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "application_id", nullable = false) private JobApplication application;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "interviewer_id", nullable = false) private Employee interviewer;
    @Column(name = "scheduled_at", nullable = false) private Instant scheduledAt;
    @Column(name = "duration_minutes", nullable = false) private short durationMinutes;
    @Column(name = "interview_type", nullable = false, length = 24) private String interviewType;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private InterviewStatus status = InterviewStatus.SCHEDULED;
    @Column(length = 2000) private String feedback;
    private Byte rating;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    protected Interview() { }
    public Interview(JobApplication application, Employee interviewer, Instant scheduledAt, short duration, String type) {
        this.application = application; this.interviewer = interviewer; this.scheduledAt = scheduledAt;
        this.durationMinutes = duration; this.interviewType = type;
    }
    public void complete(String feedback, Byte rating) { this.feedback = feedback.trim(); this.rating = rating; this.status = InterviewStatus.COMPLETED; }
    public void cancel() { if (status != InterviewStatus.SCHEDULED) throw new IllegalStateException("Only scheduled interviews can be cancelled"); status = InterviewStatus.CANCELLED; }
    @PrePersist void onCreate() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
    public Long getId() { return id; } public JobApplication getApplication() { return application; }
    public Employee getInterviewer() { return interviewer; } public Instant getScheduledAt() { return scheduledAt; }
    public short getDurationMinutes() { return durationMinutes; } public String getInterviewType() { return interviewType; }
    public InterviewStatus getStatus() { return status; } public String getFeedback() { return feedback; }
    public Byte getRating() { return rating; }
}
