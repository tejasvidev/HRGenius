package com.hrgenius.leave.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Locale;

@Entity
@Table(name = "leave_types", uniqueConstraints = {
        @UniqueConstraint(name = "uk_leave_types_code", columnNames = "code"),
        @UniqueConstraint(name = "uk_leave_types_name", columnNames = "name")
})
public class LeaveType {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 24) private String code;
    @Column(nullable = false, length = 100) private String name;
    @Column(length = 500) private String description;
    @Column(nullable = false) private boolean active = true;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    protected LeaveType() { }
    public LeaveType(String code, String name, String description) { update(code, name, description); }
    public void update(String code, String name, String description) {
        this.code = code.trim().toUpperCase(Locale.ROOT); this.name = name.trim();
        this.description = description == null || description.isBlank() ? null : description.trim();
    }
    public void setActive(boolean active) { this.active = active; }
    @PrePersist void onCreate() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
    public Long getId() { return id; } public String getCode() { return code; }
    public String getName() { return name; } public String getDescription() { return description; }
    public boolean isActive() { return active; }
}
