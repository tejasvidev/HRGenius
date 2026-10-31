package com.hrgenius.attendance.dto;

import com.hrgenius.attendance.entity.AttendanceCorrectionRequest;
import com.hrgenius.attendance.entity.CorrectionStatus;
import java.time.Instant;
import java.time.LocalDate;

public record AttendanceCorrectionResponse(Long id,Long employeeId,String employeeNumber,String employeeName,LocalDate workDate,
        Instant requestedCheckInAt,Instant requestedCheckOutAt,String reason,CorrectionStatus status,String reviewerComment,Instant reviewedAt) {
    public static AttendanceCorrectionResponse from(AttendanceCorrectionRequest r){var e=r.getEmployee();return new AttendanceCorrectionResponse(r.getId(),e.getId(),e.getEmployeeNumber(),e.getFirstName()+" "+e.getLastName(),r.getWorkDate(),r.getRequestedCheckInAt(),r.getRequestedCheckOutAt(),r.getReason(),r.getStatus(),r.getReviewerComment(),r.getReviewedAt());}
}
