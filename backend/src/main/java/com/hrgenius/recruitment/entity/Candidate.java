package com.hrgenius.recruitment.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Locale;

@Entity @Table(name = "candidates")
public class Candidate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "first_name", nullable = false, length = 100) private String firstName;
    @Column(name = "last_name", nullable = false, length = 100) private String lastName;
    @Column(nullable = false, unique = true, length = 254) private String email;
    @Column(length = 40) private String phone;
    @Column(name = "resume_url", length = 512) private String resumeUrl;
    @Column(length = 80) private String source;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    protected Candidate() { }
    public Candidate(String firstName, String lastName, String email, String phone, String resumeUrl, String source) {
        this.firstName = firstName.trim(); this.lastName = lastName.trim(); this.email = email.trim().toLowerCase(Locale.ROOT);
        this.phone = clean(phone); this.resumeUrl = clean(resumeUrl); this.source = clean(source);
    }
    private static String clean(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    @PrePersist void onCreate() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
    public Long getId() { return id; } public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; } public String getEmail() { return email; }
    public String getPhone() { return phone; } public String getResumeUrl() { return resumeUrl; }
    public String getSource() { return source; }
}
