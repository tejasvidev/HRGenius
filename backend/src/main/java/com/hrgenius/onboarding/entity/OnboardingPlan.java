package com.hrgenius.onboarding.entity;

import com.hrgenius.employee.entity.Employee;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="onboarding_plans")
public class OnboardingPlan {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="employee_id", nullable=false) private Employee employee;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="template_id", nullable=false) private OnboardingTemplate template;
    @Column(name="start_date", nullable=false) private LocalDate startDate;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=16) private OnboardingPlanStatus status=OnboardingPlanStatus.NOT_STARTED;
    @Column(name="created_at", nullable=false, updatable=false) private Instant createdAt;
    @Column(name="updated_at", nullable=false) private Instant updatedAt;
    protected OnboardingPlan() { }
    public OnboardingPlan(Employee employee, OnboardingTemplate template, LocalDate startDate) { this.employee=employee;this.template=template;this.startDate=startDate; }
    public void setStatus(OnboardingPlanStatus status){this.status=status;}
    @PrePersist void onCreate(){createdAt=Instant.now();updatedAt=createdAt;}
    @PreUpdate void onUpdate(){updatedAt=Instant.now();}
    public Long getId(){return id;} public Employee getEmployee(){return employee;} public OnboardingTemplate getTemplate(){return template;}
    public LocalDate getStartDate(){return startDate;} public OnboardingPlanStatus getStatus(){return status;}
}
