package com.hrgenius.leave.entity;

import com.hrgenius.employee.entity.Employee;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name="leave_accrual_ledger",uniqueConstraints=@UniqueConstraint(name="uk_leave_accrual_employee_type_period",columnNames={"employee_id","leave_type_id","accrual_year","accrual_month"}))
public class LeaveAccrualLedger {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="employee_id",nullable=false) private Employee employee;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="leave_type_id",nullable=false) private LeaveType leaveType;
    @Column(name="accrual_year",nullable=false) private short year;
    @Column(name="accrual_month",nullable=false) private byte month;
    @Column(name="granted_days",nullable=false,precision=5,scale=2) private BigDecimal grantedDays;
    @Column(name="processed_at",nullable=false,updatable=false) private Instant processedAt;
    protected LeaveAccrualLedger() { }
    public LeaveAccrualLedger(Employee e,LeaveType t,short y,byte m,BigDecimal granted){this.employee=e;this.leaveType=t;this.year=y;this.month=m;this.grantedDays=granted;}
    @PrePersist void onCreate(){processedAt=Instant.now();}
}
