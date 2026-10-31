package com.hrgenius.leave.entity;

import com.hrgenius.employee.entity.Employee;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "leave_balances", uniqueConstraints = @UniqueConstraint(name = "uk_leave_balance_employee_type_year",
        columnNames = {"employee_id", "leave_type_id", "calendar_year"}))
public class LeaveBalance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "employee_id", nullable = false) private Employee employee;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "leave_type_id", nullable = false) private LeaveType leaveType;
    @Column(name = "calendar_year", nullable = false) private short calendarYear;
    @Column(name = "entitled_days", nullable = false, precision = 6, scale = 2) private BigDecimal entitledDays;
    @Column(name = "used_days", nullable = false, precision = 6, scale = 2) private BigDecimal usedDays = BigDecimal.ZERO;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    protected LeaveBalance() { }
    public LeaveBalance(Employee employee, LeaveType type, short year, BigDecimal entitlement) {
        this.employee = employee; this.leaveType = type; this.calendarYear = year; this.entitledDays = entitlement;
    }
    public void setEntitlement(BigDecimal days) {
        if (days.compareTo(usedDays) < 0) throw new IllegalArgumentException("Entitlement cannot be below days already used");
        this.entitledDays = days;
    }
    public void grant(BigDecimal days) { if (days.signum() > 0) this.entitledDays = this.entitledDays.add(days); }
    public void consume(BigDecimal days) {
        if (getRemainingDays().compareTo(days) < 0) throw new IllegalStateException("Insufficient leave balance");
        usedDays = usedDays.add(days);
    }
    public BigDecimal getRemainingDays() { return entitledDays.subtract(usedDays); }
    @PrePersist void onCreate() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
    public Long getId() { return id; } public Employee getEmployee() { return employee; }
    public LeaveType getLeaveType() { return leaveType; } public short getCalendarYear() { return calendarYear; }
    public BigDecimal getEntitledDays() { return entitledDays; } public BigDecimal getUsedDays() { return usedDays; }
}
