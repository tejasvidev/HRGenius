package com.hrgenius.payroll.dto;

import com.hrgenius.payroll.entity.PayrollItem;
import com.hrgenius.payroll.entity.PayrollStatus;
import java.math.BigDecimal;
import java.time.YearMonth;

public record PayrollItemResponse(Long id,Long employeeId,String employeeNumber,String employeeName,YearMonth period,
        String currency,PayrollStatus status,BigDecimal grossPay,BigDecimal fixedDeductions,BigDecimal netPay) {
    public static PayrollItemResponse from(PayrollItem i){var e=i.getEmployee();return new PayrollItemResponse(i.getId(),e.getId(),e.getEmployeeNumber(),e.getFirstName()+" "+e.getLastName(),i.getRun().getPeriod(),i.getCurrency(),i.getRun().getStatus(),i.getGrossPay(),i.getFixedDeductions(),i.getNetPay());}
}
