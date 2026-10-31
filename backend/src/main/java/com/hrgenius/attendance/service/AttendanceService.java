package com.hrgenius.attendance.service;

import com.hrgenius.attendance.dto.*;
import com.hrgenius.attendance.entity.*;
import com.hrgenius.attendance.repository.*;
import com.hrgenius.leave.entity.LeaveCalendarPolicy;
import com.hrgenius.leave.repository.LeaveCalendarPolicyRepository;
import com.hrgenius.leave.repository.LeaveHolidayRepository;
import com.hrgenius.common.ConflictException;
import com.hrgenius.common.PageRequests;
import com.hrgenius.common.PageResponse;
import com.hrgenius.common.ResourceNotFoundException;
import com.hrgenius.employee.entity.Employee;
import com.hrgenius.employee.entity.EmployeeStatus;
import com.hrgenius.employee.repository.EmployeeRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.Set;

@Service
public class AttendanceService {
    private static final Set<String> HR_ROLES=Set.of("ROLE_SUPER_ADMIN","ROLE_ADMIN","ROLE_HR_MANAGER","ROLE_HR_EXECUTIVE");
    private final EmployeeRepository employees;
    private final AttendanceRecordRepository records;
    private final AttendanceCorrectionRepository corrections;
    private final AttendancePolicyRepository policies;
    private final LeaveCalendarPolicyRepository calendarPolicies;
    private final LeaveHolidayRepository holidays;
    private final ZoneId zone;

    public AttendanceService(EmployeeRepository employees,AttendanceRecordRepository records,AttendanceCorrectionRepository corrections,
                             AttendancePolicyRepository policies,LeaveCalendarPolicyRepository calendarPolicies,LeaveHolidayRepository holidays,
                             @org.springframework.beans.factory.annotation.Value("${app.attendance.time-zone:Asia/Kolkata}") String zone){
        this.employees=employees;this.records=records;this.corrections=corrections;this.policies=policies;this.calendarPolicies=calendarPolicies;this.holidays=holidays;this.zone=ZoneId.of(zone);
    }

    public LocalDate today(){return LocalDate.now(zone);}

    @Transactional(readOnly=true)
    public AttendancePolicyResponse policy(){return AttendancePolicyResponse.from(policies.findById((byte)1).orElseGet(AttendancePolicy::defaults));}

    @Transactional
    public AttendancePolicyResponse updatePolicy(AttendancePolicyRequest input){
        if(input.halfDayThresholdMinutes()>input.standardWorkMinutes())throw new ConflictException("Half-day threshold cannot exceed standard work minutes");
        AttendancePolicy policy=policies.findById((byte)1).orElseGet(AttendancePolicy::defaults);
        policy.update(input.workdayStart(),input.standardWorkMinutes(),input.lateGraceMinutes(),input.halfDayThresholdMinutes(),input.overtimeAfterMinutes());
        return AttendancePolicyResponse.from(policies.save(policy));
    }

    @Transactional
    public AttendanceResponse checkIn(String email){
        Employee employee=employeeByEmail(email);ensureActive(employee);
        LocalDate day=LocalDate.now(zone);
        if(records.findByEmployee_IdAndWorkDate(employee.getId(),day).isPresent())throw new ConflictException("You already have an attendance record for today");
        Instant now=Instant.now();AttendancePolicy policy=currentPolicy();
        AttendanceRecord record=records.save(new AttendanceRecord(employee,day,now,lateMinutes(now,policy)));
        return AttendanceResponse.from(record);
    }

    @Transactional
    public AttendanceResponse checkOut(String email){
        Employee employee=employeeByEmail(email);LocalDate day=LocalDate.now(zone);
        AttendanceRecord record=records.lockByEmployeeAndDate(employee.getId(),day).orElseThrow(()->new ConflictException("Check in before checking out"));
        AttendancePolicy policy=currentPolicy();
        try{record.checkOut(Instant.now(),policy.getHalfDayThresholdMinutes(),policy.getOvertimeAfterMinutes());}catch(IllegalStateException|IllegalArgumentException ex){throw new ConflictException(ex.getMessage());}
        return AttendanceResponse.from(records.save(record));
    }

    @Transactional(readOnly=true)
    public PageResponse<AttendanceResponse> mine(String email,LocalDate from,LocalDate to,Pageable pageable){
        Employee employee=employeeByEmail(email);validateRange(from,to);
        return PageResponse.from(records.findAllByEmployee_IdAndWorkDateBetweenOrderByWorkDateDesc(employee.getId(),from,to,PageRequests.capped(pageable)),AttendanceResponse::from);
    }

    @Transactional(readOnly=true)
    public PageResponse<AttendanceResponse> employeeRecords(long employeeId,LocalDate from,LocalDate to,Pageable pageable){
        employee(employeeId);validateRange(from,to);
        return PageResponse.from(records.findAllByEmployee_IdAndWorkDateBetweenOrderByWorkDateDesc(employeeId,from,to,PageRequests.capped(pageable)),AttendanceResponse::from);
    }

    @Transactional(readOnly=true)
    public PageResponse<AttendanceResponse> allRecords(LocalDate from,LocalDate to,Pageable pageable){
        validateRange(from,to);
        return PageResponse.from(records.findAllByWorkDateBetweenOrderByWorkDateDesc(from,to,PageRequests.capped(pageable)),AttendanceResponse::from);
    }

    @Transactional
    public AttendanceCorrectionResponse requestCorrection(String email,AttendanceCorrectionRequestBody input){
        Employee employee=employeeByEmail(email);ensureActive(employee);
        if(input.workDate().isAfter(LocalDate.now(zone)))throw new ConflictException("Attendance corrections cannot be requested for a future date");
        if(corrections.existsByEmployee_IdAndWorkDate(employee.getId(),input.workDate()))throw new ConflictException("A correction request already exists for this employee and date");
        Instant in=input.workDate().atTime(input.checkIn()).atZone(zone).toInstant();
        Instant out=input.workDate().atTime(input.checkOut()).atZone(zone).toInstant();
        if(!out.isAfter(in))throw new ConflictException("Check-out must be later than check-in on the same work date");
        if(out.isAfter(Instant.now()))throw new ConflictException("Requested check-out time cannot be in the future");
        return AttendanceCorrectionResponse.from(corrections.save(new AttendanceCorrectionRequest(employee,input.workDate(),in,out,input.reason())));
    }

    @Transactional(readOnly=true)
    public PageResponse<AttendanceCorrectionResponse> correctionRequests(CorrectionStatus status,Pageable pageable){
        return PageResponse.from(corrections.findAllByStatusOrderByCreatedAtAsc(status,PageRequests.capped(pageable)),AttendanceCorrectionResponse::from);
    }

    @Transactional
    public AttendanceCorrectionResponse decideCorrection(long id,String email,Set<String> roles,AttendanceCorrectionDecision input){
        if(roles.stream().noneMatch(HR_ROLES::contains))throw new AccessDeniedException("HR role required to review attendance corrections");
        AttendanceCorrectionRequest correction=corrections.lockById(id).orElseThrow(()->new ResourceNotFoundException("Attendance correction",id));
        if(correction.getStatus()!=CorrectionStatus.PENDING)throw new ConflictException("Only pending correction requests can be reviewed");
        Employee reviewer=employees.findByWorkEmailIgnoreCase(email).orElse(null);
        if(input.approved()){
            AttendancePolicy policy=currentPolicy();int late=lateMinutes(correction.getRequestedCheckInAt(),policy);
            AttendanceRecord record=records.lockByEmployeeAndDate(correction.getEmployee().getId(),correction.getWorkDate())
                    .orElseGet(()->new AttendanceRecord(correction.getEmployee(),correction.getWorkDate(),correction.getRequestedCheckInAt(),late));
            record.correct(correction.getRequestedCheckInAt(),correction.getRequestedCheckOutAt(),late,policy.getHalfDayThresholdMinutes(),policy.getOvertimeAfterMinutes());
            records.save(record);
        }
        try{correction.decide(reviewer,input.approved(),input.comment());}catch(IllegalStateException ex){throw new ConflictException(ex.getMessage());}
        return AttendanceCorrectionResponse.from(corrections.save(correction));
    }

    @Transactional
    public int markAbsences(LocalDate workDate){
        if(!isScheduledWorkday(workDate))return 0;
        int pageNumber=0,created=0;Page<Employee> page;
        do{
            page=employees.findAllByStatus(EmployeeStatus.ACTIVE,PageRequest.of(pageNumber++,500,Sort.by("id").ascending()));
            for(Employee employee:page.getContent())if(records.findByEmployee_IdAndWorkDate(employee.getId(),workDate).isEmpty()){
                records.save(AttendanceRecord.absent(employee,workDate));created++;
            }
        }while(page.hasNext());
        return created;
    }

    private boolean isScheduledWorkday(LocalDate day){
        Set<DayOfWeek> working=calendarPolicies.findById((byte)1).orElseGet(LeaveCalendarPolicy::defaults).getWorkingDays();
        if(!working.contains(day.getDayOfWeek()))return false;
        return holidays.findAllByActiveTrueAndDateBetweenOrderByDateAsc(day,day).isEmpty();
    }
    private AttendancePolicy currentPolicy(){return policies.findById((byte)1).orElseGet(AttendancePolicy::defaults);}
    private int lateMinutes(Instant checkIn,AttendancePolicy policy){
        LocalTime local=checkIn.atZone(zone).toLocalTime();
        long late=Duration.between(policy.getWorkdayStart(),local).toMinutes()-policy.getLateGraceMinutes();
        return (int)Math.max(0,late);
    }

    private Employee employeeByEmail(String email){return employees.findByWorkEmailIgnoreCase(email).orElseThrow(()->new ResourceNotFoundException("Employee profile",null));}
    private Employee employee(long id){return employees.findById(id).orElseThrow(()->new ResourceNotFoundException("Employee",id));}
    private void ensureActive(Employee employee){if(employee.getStatus()!=EmployeeStatus.ACTIVE)throw new ConflictException("Only active employees can record attendance");}
    private void validateRange(LocalDate from,LocalDate to){if(to.isBefore(from))throw new ConflictException("End date must be on or after start date");if(from.plusDays(366).isBefore(to))throw new ConflictException("Attendance queries are limited to 367 days");}
}
