package com.hrgenius.onboarding.entity;

import jakarta.persistence.*;

@Entity @Table(name="onboarding_template_tasks")
public class OnboardingTemplateTask {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="template_id", nullable=false) private OnboardingTemplate template;
    @Column(nullable=false, length=160) private String title;
    @Column(length=1000) private String description;
    @Enumerated(EnumType.STRING) @Column(name="assignee_role", nullable=false, length=16) private OnboardingAssigneeRole assigneeRole;
    @Column(name="days_from_start", nullable=false) private short daysFromStart;
    @Column(nullable=false) private boolean required;
    @Column(name="display_order", nullable=false) private short displayOrder;
    protected OnboardingTemplateTask() { }
    public OnboardingTemplateTask(String title, String description, OnboardingAssigneeRole role, short days, boolean required, short order) {
        this.title=title.trim(); this.description=description==null||description.isBlank()?null:description.trim();
        this.assigneeRole=role; this.daysFromStart=days; this.required=required; this.displayOrder=order;
    }
    void setTemplate(OnboardingTemplate template){this.template=template;}
    public Long getId(){return id;} public String getTitle(){return title;} public String getDescription(){return description;}
    public OnboardingAssigneeRole getAssigneeRole(){return assigneeRole;} public short getDaysFromStart(){return daysFromStart;}
    public boolean isRequired(){return required;} public short getDisplayOrder(){return displayOrder;}
}
