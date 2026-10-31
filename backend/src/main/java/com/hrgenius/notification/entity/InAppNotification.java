package com.hrgenius.notification.entity;
import com.hrgenius.announcement.entity.Announcement;
import com.hrgenius.employee.entity.Employee;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="in_app_notifications",uniqueConstraints=@UniqueConstraint(name="uk_notification_employee_announcement",columnNames={"employee_id","announcement_id"}))
public class InAppNotification {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="employee_id") private Employee employee;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="announcement_id") private Announcement announcement;
    @Column(nullable=false,length=180) private String title;
    @Column(nullable=false,length=1000) private String body;
    @Column(name="notification_type",nullable=false,length=32) private String type;
    @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
    @Column(name="read_at") private Instant readAt;
    protected InAppNotification(){}
    public InAppNotification(Employee employee,Announcement announcement,String title,String body,String type){this.employee=employee;this.announcement=announcement;this.title=title;this.body=body;this.type=type;}
    public void markRead(){if(readAt==null)readAt=Instant.now();}
    @PrePersist void onCreate(){createdAt=Instant.now();}
    public Long getId(){return id;} public Employee getEmployee(){return employee;} public String getTitle(){return title;} public String getBody(){return body;} public String getType(){return type;} public Instant getCreatedAt(){return createdAt;} public Instant getReadAt(){return readAt;}
}
