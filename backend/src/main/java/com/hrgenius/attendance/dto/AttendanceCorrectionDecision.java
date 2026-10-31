package com.hrgenius.attendance.dto;

import jakarta.validation.constraints.Size;

public record AttendanceCorrectionDecision(boolean approved,@Size(max=1000) String comment) { }
