package com.hrgenius.attendance.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalTime;

@Entity @Table(name="attendance_policy")
public class AttendancePolicy {
    @Id @Column(nullable=false) private Byte id=(byte)1;
    @Column(name="workday_start",nullable=false) private LocalTime workdayStart=LocalTime.of(9,0);
    @Column(name="standard_work_minutes",nullable=false) private int standardWorkMinutes=480;
    @Column(name="late_grace_minutes",nullable=false) private int lateGraceMinutes=15;
    @Column(name="half_day_threshold_minutes",nullable=false) private int halfDayThresholdMinutes=240;
    @Column(name="overtime_after_minutes",nullable=false) private int overtimeAfterMinutes=480;
    @Column(name="updated_at",nullable=false) private Instant updatedAt;
    protected AttendancePolicy() { }
    public static AttendancePolicy defaults(){return new AttendancePolicy();}
    public void update(LocalTime start,int standard,int grace,int halfDay,int overtime){workdayStart=start;standardWorkMinutes=standard;lateGraceMinutes=grace;halfDayThresholdMinutes=halfDay;overtimeAfterMinutes=overtime;}
    @PrePersist @PreUpdate void updateTimestamp(){updatedAt=Instant.now();}
    public LocalTime getWorkdayStart(){return workdayStart;} public int getStandardWorkMinutes(){return standardWorkMinutes;} public int getLateGraceMinutes(){return lateGraceMinutes;} public int getHalfDayThresholdMinutes(){return halfDayThresholdMinutes;} public int getOvertimeAfterMinutes(){return overtimeAfterMinutes;}
}
