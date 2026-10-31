package com.hrgenius.announcement.entity;
import com.hrgenius.organization.entity.Department;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="announcements")
public class Announcement {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,length=180) private String title;
    @Column(nullable=false,length=5000) private String body;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="department_id") private Department department;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=16) private AnnouncementPriority priority=AnnouncementPriority.NORMAL;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=16) private AnnouncementStatus status=AnnouncementStatus.DRAFT;
    @Column(name="published_at") private Instant publishedAt;
    @Column(name="expires_at") private Instant expiresAt;
    @Column(name="created_by_email",nullable=false,length=254) private String createdByEmail;
    @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
    @Column(name="updated_at",nullable=false) private Instant updatedAt;
    protected Announcement(){}
    public Announcement(String title,String body,Department department,AnnouncementPriority priority,Instant expiresAt,String author){update(title,body,department,priority,expiresAt);this.createdByEmail=author;}
    public void update(String title,String body,Department department,AnnouncementPriority priority,Instant expiresAt){if(status!=AnnouncementStatus.DRAFT)throw new IllegalStateException("Published announcements cannot be edited");if(expiresAt!=null&&!expiresAt.isAfter(Instant.now()))throw new IllegalArgumentException("Expiry must be in the future");this.title=title.trim();this.body=body.trim();this.department=department;this.priority=priority;this.expiresAt=expiresAt;}
    public void publish(){if(status!=AnnouncementStatus.DRAFT)throw new IllegalStateException("Announcement is already published");status=AnnouncementStatus.PUBLISHED;publishedAt=Instant.now();}
    @PrePersist void create(){createdAt=Instant.now();updatedAt=createdAt;} @PreUpdate void updateTimestamp(){updatedAt=Instant.now();}
    public Long getId(){return id;} public String getTitle(){return title;} public String getBody(){return body;} public Department getDepartment(){return department;} public AnnouncementPriority getPriority(){return priority;} public AnnouncementStatus getStatus(){return status;} public Instant getPublishedAt(){return publishedAt;} public Instant getExpiresAt(){return expiresAt;} public String getCreatedByEmail(){return createdByEmail;} public Instant getCreatedAt(){return createdAt;}
}
