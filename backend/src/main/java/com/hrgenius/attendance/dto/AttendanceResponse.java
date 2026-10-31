package com.hrgenius.attendance.dto;

import com.hrgenius.attendance.entity.AttendanceRecord;
import com.hrgenius.attendance.entity.AttendanceStatus;
import java.time.Instant;
import java.time.LocalDate;

public record AttendanceResponse(Long id,Long employeeId,String employeeNumber,String employeeName,LocalDate workDate,
        Instant checkInAt,Instant checkOutAt,int workedMinutes,int lateMinutes,int overtimeMinutes,AttendanceStatus status) {
    public static AttendanceResponse from(AttendanceRecord r){var e=r.getEmployee();return new AttendanceResponse(r.getId(),e.getId(),e.getEmployeeNumber(),e.getFirstName()+" "+e.getLastName(),r.getWorkDate(),r.getCheckInAt(),r.getCheckOutAt(),r.getWorkedMinutes(),r.getLateMinutes(),r.getOvertimeMinutes(),r.getStatus());}
}
