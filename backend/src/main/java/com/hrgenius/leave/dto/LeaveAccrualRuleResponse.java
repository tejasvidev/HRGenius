package com.hrgenius.leave.dto;
import com.hrgenius.leave.entity.LeaveAccrualRule;
import java.math.BigDecimal;
import java.time.LocalDate;
public record LeaveAccrualRuleResponse(Long id,Long leaveTypeId,String leaveTypeName,BigDecimal monthlyDays,
        BigDecimal annualCap,LocalDate effectiveFrom,boolean active) {
    public static LeaveAccrualRuleResponse from(LeaveAccrualRule r){return new LeaveAccrualRuleResponse(r.getId(),r.getLeaveType().getId(),r.getLeaveType().getName(),r.getMonthlyDays(),r.getAnnualCap(),r.getEffectiveFrom(),r.isActive());}
}
