package com.hrgenius.leave.dto;
import com.hrgenius.leave.entity.LeaveHoliday;
import java.time.LocalDate;
public record LeaveHolidayResponse(Long id,LocalDate date,String name,String description,boolean active) {
    public static LeaveHolidayResponse from(LeaveHoliday h){return new LeaveHolidayResponse(h.getId(),h.getDate(),h.getName(),h.getDescription(),h.isActive());}
}
