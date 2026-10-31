package com.hrgenius.employee.dto;

import com.hrgenius.employee.entity.EmployeeHistory;
import java.time.Instant;
import java.time.LocalDate;

public record EmployeeHistoryResponse(String changeType, String details, LocalDate effectiveDate, Instant createdAt) {
    public static EmployeeHistoryResponse from(EmployeeHistory h) {
        return new EmployeeHistoryResponse(h.getChangeType(), h.getDetails(), h.getEffectiveDate(), h.getCreatedAt());
    }
}
