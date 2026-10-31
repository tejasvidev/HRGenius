package com.hrgenius.leave.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="leave_holidays", uniqueConstraints=@UniqueConstraint(name="uk_leave_holiday_date", columnNames="holiday_date"))
public class LeaveHoliday {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="holiday_date", nullable=false) private LocalDate date;
    @Column(nullable=false, length=120) private String name;
    @Column(length=500) private String description;
    @Column(nullable=false) private boolean active=true;
    @Column(name="created_at", nullable=false, updatable=false) private Instant createdAt;
    @Column(name="updated_at", nullable=false) private Instant updatedAt;
    protected LeaveHoliday() { }
    public LeaveHoliday(LocalDate date,String name,String description){this.date=date;this.name=name.trim();this.description=clean(description);}
    public void setActive(boolean active){this.active=active;}
    private static String clean(String s){return s==null||s.isBlank()?null:s.trim();}
    @PrePersist void onCreate(){createdAt=Instant.now();updatedAt=createdAt;}
    @PreUpdate void onUpdate(){updatedAt=Instant.now();}
    public Long getId(){return id;} public LocalDate getDate(){return date;} public String getName(){return name;}
    public String getDescription(){return description;} public boolean isActive(){return active;}
}
