package com.hrgenius.leave.dto;
import jakarta.validation.constraints.NotEmpty;
import java.time.DayOfWeek;
import java.util.Set;
public record LeaveCalendarPolicyRequest(@NotEmpty Set<DayOfWeek> workingDays) { }
