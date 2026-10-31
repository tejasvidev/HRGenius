package com.hrgenius.leave.dto;
import com.hrgenius.leave.entity.LeaveCalendarPolicy;
import java.time.DayOfWeek;
import java.util.Set;
public record LeaveCalendarPolicyResponse(Set<DayOfWeek> workingDays) {
    public static LeaveCalendarPolicyResponse from(LeaveCalendarPolicy p){return new LeaveCalendarPolicyResponse(p.getWorkingDays());}
}
