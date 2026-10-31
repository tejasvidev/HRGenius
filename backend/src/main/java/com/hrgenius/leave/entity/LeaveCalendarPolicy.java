package com.hrgenius.leave.entity;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.Instant;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity @Table(name="leave_calendar_policy")
public class LeaveCalendarPolicy {
    @Id private byte id=1;
    @Column(name="working_days", nullable=false, length=64) private String workingDays="MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY";
    @Column(name="updated_at", nullable=false) private Instant updatedAt;
    protected LeaveCalendarPolicy() { }
    public static LeaveCalendarPolicy defaults(){return new LeaveCalendarPolicy();}
    public void setWorkingDays(Set<DayOfWeek> days){this.workingDays=days.stream().sorted().map(Enum::name).collect(Collectors.joining(","));}
    public Set<DayOfWeek> getWorkingDays(){return Arrays.stream(workingDays.split(",")).map(DayOfWeek::valueOf).collect(Collectors.toCollection(()->EnumSet.noneOf(DayOfWeek.class)));}
    @PrePersist void onCreate(){if(updatedAt==null)updatedAt=Instant.now();}
    @PreUpdate void onUpdate(){updatedAt=Instant.now();}
}
