package com.hrgenius.leave.dto;

import com.hrgenius.leave.entity.LeaveRequest;
import com.hrgenius.leave.entity.LeaveRequestStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record LeaveRequestResponse(Long id, Long employeeId, String employeeName, Long leaveTypeId,
        String leaveTypeName, LocalDate startDate, LocalDate endDate, BigDecimal requestedDays,
        String reason, LeaveRequestStatus status, String reviewerName, String reviewerComment,
        Instant decidedAt, Instant createdAt) {
    public static LeaveRequestResponse from(LeaveRequest r) {
        return new LeaveRequestResponse(r.getId(), r.getEmployee().getId(),
                r.getEmployee().getFirstName() + " " + r.getEmployee().getLastName(), r.getLeaveType().getId(),
                r.getLeaveType().getName(), r.getStartDate(), r.getEndDate(), r.getRequestedDays(), r.getReason(),
                r.getStatus(), r.getReviewer() == null ? null : r.getReviewer().getFirstName() + " " + r.getReviewer().getLastName(),
                r.getReviewerComment(), r.getDecidedAt(), r.getCreatedAt());
    }
}
