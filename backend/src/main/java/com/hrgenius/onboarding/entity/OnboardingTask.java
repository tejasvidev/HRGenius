package com.hrgenius.onboarding.entity;

import com.hrgenius.employee.entity.Employee;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="onboarding_tasks")
public class OnboardingTask {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="plan_id", nullable=false) private OnboardingPlan plan;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="template_task_id") private OnboardingTemplateTask templateTask;
    @Column(nullable=false, length=160) private String title;
    @Column(length=1000) private String description;
    @Enumerated(EnumType.STRING) @Column(name="assignee_role", nullable=false, length=16) private OnboardingAssigneeRole assigneeRole;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="assignee_employee_id") private Employee assignee;
    @Column(name="due_date", nullable=false) private LocalDate dueDate;
    @Column(nullable=false) private boolean required;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=16) private OnboardingTaskStatus status=OnboardingTaskStatus.PENDING;
    @Column(length=1000) private String notes;
    @Column(name="completed_at") private Instant completedAt;
    @Column(name="created_at", nullable=false, updatable=false) private Instant createdAt;
    @Column(name="updated_at", nullable=false) private Instant updatedAt;
    protected OnboardingTask() { }
    public OnboardingTask(OnboardingPlan plan, OnboardingTemplateTask source, Employee assignee, LocalDate dueDate) {
        this.plan=plan; this.templateTask=source; this.title=source.getTitle(); this.description=source.getDescription();
        this.assigneeRole=source.getAssigneeRole(); this.assignee=assignee; this.dueDate=dueDate; this.required=source.isRequired();
    }
    public void updateStatus(OnboardingTaskStatus status, String notes) {
        this.status=status; this.notes=notes==null||notes.isBlank()?null:notes.trim();
        this.completedAt=status==OnboardingTaskStatus.COMPLETED?Instant.now():null;
    }
    @PrePersist void onCreate(){createdAt=Instant.now();updatedAt=createdAt;}
    @PreUpdate void onUpdate(){updatedAt=Instant.now();}
    public Long getId(){return id;} public OnboardingPlan getPlan(){return plan;} public String getTitle(){return title;}
    public String getDescription(){return description;} public OnboardingAssigneeRole getAssigneeRole(){return assigneeRole;}
    public Employee getAssignee(){return assignee;} public LocalDate getDueDate(){return dueDate;} public boolean isRequired(){return required;}
    public OnboardingTaskStatus getStatus(){return status;} public String getNotes(){return notes;} public Instant getCompletedAt(){return completedAt;}
}
