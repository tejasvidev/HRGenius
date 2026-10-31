package com.hrgenius.payroll.entity;

import com.hrgenius.employee.entity.Employee;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;

@Entity @Table(name="payroll_runs",uniqueConstraints=@UniqueConstraint(name="uk_payroll_period",columnNames={"period_year","period_month"}))
public class PayrollRun {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="period_year",nullable=false) private short year;
    @Column(name="period_month",nullable=false) private byte month;
    @Column(nullable=false,length=3,columnDefinition="char(3)") private String currency;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=16) private PayrollStatus status=PayrollStatus.DRAFT;
    @Column(name="employee_count",nullable=false) private int employeeCount;
    @Column(name="total_gross",nullable=false,precision=15,scale=2) private BigDecimal totalGross=BigDecimal.ZERO;
    @Column(name="total_deductions",nullable=false,precision=15,scale=2) private BigDecimal totalDeductions=BigDecimal.ZERO;
    @Column(name="total_net",nullable=false,precision=15,scale=2) private BigDecimal totalNet=BigDecimal.ZERO;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="approved_by") private Employee approver;
    @Column(name="approved_at") private Instant approvedAt;
    @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
    protected PayrollRun() { }
    public PayrollRun(YearMonth period,String currency){year=(short)period.getYear();month=(byte)period.getMonthValue();this.currency=currency;}
    public void totals(int count,BigDecimal gross,BigDecimal deductions,BigDecimal net){employeeCount=count;totalGross=gross;totalDeductions=deductions;totalNet=net;}
    public void approve(Employee employee){if(status!=PayrollStatus.DRAFT)throw new IllegalStateException("Only draft payroll runs can be approved");status=PayrollStatus.APPROVED;approver=employee;approvedAt=Instant.now();}
    @PrePersist void onCreate(){createdAt=Instant.now();}
    public Long getId(){return id;} public YearMonth getPeriod(){return YearMonth.of(year,month);} public String getCurrency(){return currency;} public PayrollStatus getStatus(){return status;} public int getEmployeeCount(){return employeeCount;} public BigDecimal getTotalGross(){return totalGross;} public BigDecimal getTotalDeductions(){return totalDeductions;} public BigDecimal getTotalNet(){return totalNet;} public Instant getApprovedAt(){return approvedAt;}
}
