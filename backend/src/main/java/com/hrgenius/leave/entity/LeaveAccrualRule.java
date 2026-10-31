package com.hrgenius.leave.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="leave_accrual_rules", uniqueConstraints=@UniqueConstraint(name="uk_leave_accrual_type",columnNames="leave_type_id"))
public class LeaveAccrualRule {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="leave_type_id",nullable=false) private LeaveType leaveType;
    @Column(name="monthly_days",nullable=false,precision=5,scale=2) private BigDecimal monthlyDays;
    @Column(name="annual_cap",precision=6,scale=2) private BigDecimal annualCap;
    @Column(name="effective_from",nullable=false) private LocalDate effectiveFrom;
    @Column(nullable=false) private boolean active=true;
    @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
    @Column(name="updated_at",nullable=false) private Instant updatedAt;
    protected LeaveAccrualRule() { }
    public LeaveAccrualRule(LeaveType type,BigDecimal days,BigDecimal cap,LocalDate from){this.leaveType=type;update(days,cap,from);}
    public void update(BigDecimal days,BigDecimal cap,LocalDate from){this.monthlyDays=days;this.annualCap=cap;this.effectiveFrom=from;}
    public void setActive(boolean active){this.active=active;}
    @PrePersist void onCreate(){createdAt=Instant.now();updatedAt=createdAt;}
    @PreUpdate void onUpdate(){updatedAt=Instant.now();}
    public Long getId(){return id;} public LeaveType getLeaveType(){return leaveType;} public BigDecimal getMonthlyDays(){return monthlyDays;}
    public BigDecimal getAnnualCap(){return annualCap;} public LocalDate getEffectiveFrom(){return effectiveFrom;} public boolean isActive(){return active;}
}
