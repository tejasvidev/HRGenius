package com.hrgenius.attendance.entity;

import com.hrgenius.employee.entity.Employee;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name="attendance_records", uniqueConstraints=@UniqueConstraint(name="uk_attendance_employee_work_date",columnNames={"employee_id","work_date"}))
public class AttendanceRecord {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="employee_id",nullable=false) private Employee employee;
    @Column(name="work_date",nullable=false) private LocalDate workDate;
    @Column(name="check_in_at") private Instant checkInAt;
    @Column(name="check_out_at") private Instant checkOutAt;
    @Column(name="worked_minutes",nullable=false) private int workedMinutes;
    @Column(name="late_minutes",nullable=false) private int lateMinutes;
    @Column(name="overtime_minutes",nullable=false) private int overtimeMinutes;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private AttendanceStatus status=AttendanceStatus.PRESENT;
    @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
    @Column(name="updated_at",nullable=false) private Instant updatedAt;
    protected AttendanceRecord() { }
    public AttendanceRecord(Employee employee,LocalDate workDate,Instant checkInAt,int lateMinutes){this.employee=employee;this.workDate=workDate;this.checkInAt=checkInAt;this.lateMinutes=lateMinutes;}
    public static AttendanceRecord absent(Employee employee,LocalDate workDate){AttendanceRecord record=new AttendanceRecord();record.employee=employee;record.workDate=workDate;record.status=AttendanceStatus.ABSENT;return record;}
    public void checkOut(Instant time,int halfDayThreshold,int overtimeAfterMinutes){if(checkInAt==null)throw new IllegalStateException("Absent records cannot be checked out");if(checkOutAt!=null)throw new IllegalStateException("Attendance has already been checked out");if(!time.isAfter(checkInAt))throw new IllegalArgumentException("Check-out must be later than check-in");checkOutAt=time;workedMinutes=(int)java.time.Duration.between(checkInAt,time).toMinutes();status=workedMinutes<halfDayThreshold?AttendanceStatus.HALF_DAY:AttendanceStatus.PRESENT;overtimeMinutes=Math.max(0,workedMinutes-overtimeAfterMinutes);}
    public void correct(Instant in,Instant out,int lateMinutes,int halfDayThreshold,int overtimeAfterMinutes){if(!out.isAfter(in))throw new IllegalArgumentException("Check-out must be later than check-in");checkInAt=in;checkOutAt=out;this.lateMinutes=lateMinutes;workedMinutes=(int)java.time.Duration.between(in,out).toMinutes();status=workedMinutes<halfDayThreshold?AttendanceStatus.HALF_DAY:AttendanceStatus.PRESENT;overtimeMinutes=Math.max(0,workedMinutes-overtimeAfterMinutes);}
    @PrePersist void onCreate(){createdAt=Instant.now();updatedAt=createdAt;}
    @PreUpdate void onUpdate(){updatedAt=Instant.now();}
    public Long getId(){return id;} public Employee getEmployee(){return employee;} public LocalDate getWorkDate(){return workDate;}
    public Instant getCheckInAt(){return checkInAt;} public Instant getCheckOutAt(){return checkOutAt;} public int getWorkedMinutes(){return workedMinutes;} public int getLateMinutes(){return lateMinutes;} public int getOvertimeMinutes(){return overtimeMinutes;} public AttendanceStatus getStatus(){return status;}
}
