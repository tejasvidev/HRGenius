package com.hrgenius.attendance.dto;

import com.hrgenius.attendance.entity.AttendancePolicy;
import java.time.LocalTime;

public record AttendancePolicyResponse(LocalTime workdayStart,int standardWorkMinutes,int lateGraceMinutes,
        int halfDayThresholdMinutes,int overtimeAfterMinutes) {
    public static AttendancePolicyResponse from(AttendancePolicy p){return new AttendancePolicyResponse(p.getWorkdayStart(),p.getStandardWorkMinutes(),p.getLateGraceMinutes(),p.getHalfDayThresholdMinutes(),p.getOvertimeAfterMinutes());}
}
