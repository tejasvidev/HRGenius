package com.hrgenius.leave.dto;

import com.hrgenius.leave.entity.LeaveBalance;
import java.math.BigDecimal;

public record LeaveBalanceResponse(Long id, Long employeeId, String employeeName, Long leaveTypeId,
        String leaveTypeName, short year, BigDecimal entitledDays, BigDecimal usedDays, BigDecimal remainingDays) {
    public static LeaveBalanceResponse from(LeaveBalance b) {
        return new LeaveBalanceResponse(b.getId(), b.getEmployee().getId(),
                b.getEmployee().getFirstName() + " " + b.getEmployee().getLastName(), b.getLeaveType().getId(),
                b.getLeaveType().getName(), b.getCalendarYear(), b.getEntitledDays(), b.getUsedDays(), b.getRemainingDays());
    }
}
