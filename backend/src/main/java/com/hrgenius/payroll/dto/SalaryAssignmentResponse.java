package com.hrgenius.payroll.dto;

import com.hrgenius.payroll.entity.SalaryAssignment;
import java.math.BigDecimal;
import java.time.LocalDate;

public record SalaryAssignmentResponse(Long id,Long employeeId,String employeeNumber,String employeeName,
        String currency,BigDecimal monthlyGross,BigDecimal fixedDeduction,LocalDate effectiveFrom,boolean active) {
    public static SalaryAssignmentResponse from(SalaryAssignment a){var e=a.getEmployee();return new SalaryAssignmentResponse(a.getId(),e.getId(),e.getEmployeeNumber(),e.getFirstName()+" "+e.getLastName(),a.getCurrency(),a.getMonthlyGross(),a.getFixedDeduction(),a.getEffectiveFrom(),a.isActive());}
}
