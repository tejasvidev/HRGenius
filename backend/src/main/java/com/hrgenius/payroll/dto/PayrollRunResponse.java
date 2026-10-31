package com.hrgenius.payroll.dto;

import com.hrgenius.payroll.entity.PayrollRun;
import com.hrgenius.payroll.entity.PayrollStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;

public record PayrollRunResponse(Long id,YearMonth period,String currency,PayrollStatus status,int employeeCount,
        BigDecimal totalGross,BigDecimal totalDeductions,BigDecimal totalNet,Instant approvedAt) {
    public static PayrollRunResponse from(PayrollRun r){return new PayrollRunResponse(r.getId(),r.getPeriod(),r.getCurrency(),r.getStatus(),r.getEmployeeCount(),r.getTotalGross(),r.getTotalDeductions(),r.getTotalNet(),r.getApprovedAt());}
}
