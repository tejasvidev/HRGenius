package com.hrgenius.payroll.entity;

import com.hrgenius.employee.entity.Employee;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="payroll_items",uniqueConstraints=@UniqueConstraint(name="uk_payroll_item_employee",columnNames={"payroll_run_id","employee_id"}))
public class PayrollItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="payroll_run_id",nullable=false) private PayrollRun run;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="employee_id",nullable=false) private Employee employee;
    @Column(nullable=false,length=3,columnDefinition="char(3)") private String currency;
    @Column(name="gross_pay",nullable=false,precision=13,scale=2) private BigDecimal grossPay;
    @Column(name="fixed_deductions",nullable=false,precision=13,scale=2) private BigDecimal fixedDeductions;
    @Column(name="net_pay",nullable=false,precision=13,scale=2) private BigDecimal netPay;
    protected PayrollItem() { }
    public PayrollItem(PayrollRun run,Employee employee,String currency,BigDecimal gross,BigDecimal deductions){this.run=run;this.employee=employee;this.currency=currency;grossPay=gross;fixedDeductions=deductions;netPay=gross.subtract(deductions);}
    public Long getId(){return id;} public PayrollRun getRun(){return run;} public Employee getEmployee(){return employee;} public String getCurrency(){return currency;} public BigDecimal getGrossPay(){return grossPay;} public BigDecimal getFixedDeductions(){return fixedDeductions;} public BigDecimal getNetPay(){return netPay;}
}
