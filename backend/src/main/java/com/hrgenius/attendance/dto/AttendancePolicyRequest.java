package com.hrgenius.attendance.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record AttendancePolicyRequest(@NotNull LocalTime workdayStart,@Min(1) @Max(1440) int standardWorkMinutes,
        @Min(0) @Max(360) int lateGraceMinutes,@Min(1) @Max(1440) int halfDayThresholdMinutes,
        @Min(1) @Max(2880) int overtimeAfterMinutes) { }
