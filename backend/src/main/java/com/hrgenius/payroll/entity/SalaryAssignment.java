package com.hrgenius.payroll.entity;

import com.hrgenius.employee.entity.Employee;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="salary_assignments",uniqueConstraints=@UniqueConstraint(name="uk_salary_assignment_employee",columnNames="employee_id"))
public class SalaryAssignment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="employee_id",nullable=false) private Employee employee;
    @Column(nullable=false,length=3,columnDefinition="char(3)") private String currency="INR";
    @Column(name="monthly_gross",nullable=false,precision=13,scale=2) private BigDecimal monthlyGross;
    @Column(name="fixed_deduction",nullable=false,precision=13,scale=2) private BigDecimal fixedDeduction;
    @Column(name="effective_from",nullable=false) private LocalDate effectiveFrom;
    @Column(nullable=false) private boolean active=true;
    @Column(name="updated_at",nullable=false) private Instant updatedAt;
    protected SalaryAssignment() { }
    public SalaryAssignment(Employee employee,String currency,BigDecimal gross,BigDecimal deduction,LocalDate from){this.employee=employee;update(currency,gross,deduction,from);}
    public void update(String currency,BigDecimal gross,BigDecimal deduction,LocalDate from){this.currency=currency;monthlyGross=gross;fixedDeduction=deduction;effectiveFrom=from;active=true;}
    @PrePersist @PreUpdate void touch(){updatedAt=Instant.now();}
    public Long getId(){return id;} public Employee getEmployee(){return employee;} public String getCurrency(){return currency;} public BigDecimal getMonthlyGross(){return monthlyGross;} public BigDecimal getFixedDeduction(){return fixedDeduction;} public LocalDate getEffectiveFrom(){return effectiveFrom;} public boolean isActive(){return active;}
}
