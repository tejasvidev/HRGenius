package com.hrgenius.organization.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.Locale;

@Entity
@Table(name = "departments", uniqueConstraints = {
        @UniqueConstraint(name = "uk_departments_code", columnNames = "code"),
        @UniqueConstraint(name = "uk_departments_name", columnNames = "name")
})
public class Department {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 24) private String code;
    @Column(nullable = false, length = 120) private String name;
    @Column(length = 500) private String description;
    @Column(nullable = false) private boolean active = true;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;

    protected Department() { }
    public Department(String code, String name, String description) { update(code, name, description); }
    public void update(String code, String name, String description) {
        this.code = code.trim().toUpperCase(Locale.ROOT);
        this.name = name.trim();
        this.description = description == null || description.isBlank() ? null : description.trim();
    }
    public void deactivate() { active = false; }
    public void activate() { active = true; }
    @PrePersist void onCreate() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
}
