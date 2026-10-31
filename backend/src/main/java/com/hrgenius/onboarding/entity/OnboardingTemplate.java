package com.hrgenius.onboarding.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="onboarding_templates", uniqueConstraints=@UniqueConstraint(name="uk_onboarding_template_name", columnNames="name"))
public class OnboardingTemplate {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=120) private String name;
    @Column(length=500) private String description;
    @Column(nullable=false) private boolean active=true;
    @OneToMany(mappedBy="template", cascade=CascadeType.ALL, orphanRemoval=true) @OrderBy("displayOrder ASC")
    private List<OnboardingTemplateTask> tasks=new ArrayList<>();
    @Column(name="created_at", nullable=false, updatable=false) private Instant createdAt;
    @Column(name="updated_at", nullable=false) private Instant updatedAt;
    protected OnboardingTemplate() { }
    public OnboardingTemplate(String name, String description) { this.name=name.trim(); this.description=clean(description); }
    public void addTask(OnboardingTemplateTask task) { tasks.add(task); task.setTemplate(this); }
    public void setActive(boolean active) { this.active=active; }
    private static String clean(String value) { return value==null||value.isBlank()?null:value.trim(); }
    @PrePersist void onCreate() { createdAt=Instant.now(); updatedAt=createdAt; }
    @PreUpdate void onUpdate() { updatedAt=Instant.now(); }
    public Long getId(){return id;} public String getName(){return name;} public String getDescription(){return description;}
    public boolean isActive(){return active;} public List<OnboardingTemplateTask> getTasks(){return tasks;}
}
