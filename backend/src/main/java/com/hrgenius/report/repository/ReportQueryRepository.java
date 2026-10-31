package com.hrgenius.report.repository;

import com.hrgenius.attendance.entity.AttendanceRecord;
import com.hrgenius.leave.entity.LeaveBalance;
import com.hrgenius.leave.entity.LeaveRequest;
import com.hrgenius.onboarding.entity.OnboardingPlan;
import com.hrgenius.payroll.entity.PayrollItem;
import com.hrgenius.performance.entity.PerformanceReview;
import com.hrgenius.recruitment.entity.JobApplication;
import com.hrgenius.employee.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface ReportQueryRepository extends Repository<Employee, Long> {
    @Query("select count(e) from Employee e where (:departmentId is null or e.department.id=:departmentId) and (:employeeId is null or e.id=:employeeId)")
    long employeeCount(@Param("departmentId") Long departmentId,@Param("employeeId") Long employeeId);
    @Query("select count(e) from Employee e where e.status=com.hrgenius.employee.entity.EmployeeStatus.SEPARATED and (:departmentId is null or e.department.id=:departmentId) and (:employeeId is null or e.id=:employeeId)")
    long separatedCount(@Param("departmentId") Long departmentId,@Param("employeeId") Long employeeId);
    @Query("select count(e) from Employee e where e.dateOfJoining between :fromDate and :toDate and (:departmentId is null or e.department.id=:departmentId) and (:employeeId is null or e.id=:employeeId)")
    long joinerCount(@Param("fromDate") LocalDate from,@Param("toDate") LocalDate to,@Param("departmentId") Long departmentId,@Param("employeeId") Long employeeId);
    @Query("select e from Employee e where (:employeeId is null or e.id=:employeeId) and (:departmentId is null or e.department.id=:departmentId) and (:fromDate is null or e.dateOfJoining>=:fromDate) and (:toDate is null or e.dateOfJoining<=:toDate)")
    Page<Employee> employees(@Param("fromDate") LocalDate from,@Param("toDate") LocalDate to,@Param("departmentId") Long departmentId,@Param("employeeId") Long employeeId,Pageable page);

    @Query("select coalesce(d.name,'Unassigned'), count(e.id) from Employee e left join e.department d where (:departmentId is null or d.id=:departmentId) and (:employeeId is null or e.id=:employeeId) and (:fromDate is null or e.dateOfJoining>=:fromDate) and (:toDate is null or e.dateOfJoining<=:toDate) group by d.id,d.name order by d.name")
    List<Object[]> departments(@Param("fromDate") LocalDate from,@Param("toDate") LocalDate to,@Param("departmentId") Long departmentId,@Param("employeeId") Long employeeId);

    @Query("select coalesce(d.title,'Unassigned'), count(e.id) from Employee e left join e.designation d where (:departmentId is null or e.department.id=:departmentId) and (:employeeId is null or e.id=:employeeId) and (:fromDate is null or e.dateOfJoining>=:fromDate) and (:toDate is null or e.dateOfJoining<=:toDate) group by d.id,d.title order by d.title")
    List<Object[]> designations(@Param("fromDate") LocalDate from,@Param("toDate") LocalDate to,@Param("departmentId") Long departmentId,@Param("employeeId") Long employeeId);

    @Query("select a from AttendanceRecord a where (:fromDate is null or a.workDate>=:fromDate) and (:toDate is null or a.workDate<=:toDate) and (:departmentId is null or a.employee.department.id=:departmentId) and (:employeeId is null or a.employee.id=:employeeId) and (:lateOnly=false or a.lateMinutes>0) and (:absenceOnly=false or a.status=com.hrgenius.attendance.entity.AttendanceStatus.ABSENT) and (:overtimeOnly=false or a.overtimeMinutes>0)")
    Page<AttendanceRecord> attendance(@Param("fromDate") LocalDate from,@Param("toDate") LocalDate to,@Param("departmentId") Long departmentId,@Param("employeeId") Long employeeId,@Param("lateOnly") boolean lateOnly,@Param("absenceOnly") boolean absenceOnly,@Param("overtimeOnly") boolean overtimeOnly,Pageable page);

    @Query("select l from LeaveRequest l where (:fromDate is null or l.endDate>=:fromDate) and (:toDate is null or l.startDate<=:toDate) and (:departmentId is null or l.employee.department.id=:departmentId) and (:employeeId is null or l.employee.id=:employeeId)")
    Page<LeaveRequest> leaveRequests(@Param("fromDate") LocalDate from,@Param("toDate") LocalDate to,@Param("departmentId") Long departmentId,@Param("employeeId") Long employeeId,Pageable page);

    @Query("select b from LeaveBalance b where (:yearFrom is null or b.calendarYear>=:yearFrom) and (:yearTo is null or b.calendarYear<=:yearTo) and (:departmentId is null or b.employee.department.id=:departmentId) and (:employeeId is null or b.employee.id=:employeeId)")
    Page<LeaveBalance> leaveBalances(@Param("yearFrom") Short yearFrom,@Param("yearTo") Short yearTo,@Param("departmentId") Long departmentId,@Param("employeeId") Long employeeId,Pageable page);

    @Query("select a from JobApplication a where (:fromInstant is null or a.appliedAt>=:fromInstant) and (:toExclusive is null or a.appliedAt<:toExclusive) and (:departmentId is null or a.opening.department.id=:departmentId)")
    Page<JobApplication> recruitment(@Param("fromInstant") Instant from,@Param("toExclusive") Instant toExclusive,@Param("departmentId") Long departmentId,Pageable page);

    @Query("select a.status, count(a.id) from JobApplication a where (:fromInstant is null or a.appliedAt>=:fromInstant) and (:toExclusive is null or a.appliedAt<:toExclusive) and (:departmentId is null or a.opening.department.id=:departmentId) group by a.status order by a.status")
    List<Object[]> applicationFunnel(@Param("fromInstant") Instant from,@Param("toExclusive") Instant toExclusive,@Param("departmentId") Long departmentId);

    @Query("select p from OnboardingPlan p where (:fromDate is null or p.startDate>=:fromDate) and (:toDate is null or p.startDate<=:toDate) and (:departmentId is null or p.employee.department.id=:departmentId) and (:employeeId is null or p.employee.id=:employeeId)")
    Page<OnboardingPlan> onboarding(@Param("fromDate") LocalDate from,@Param("toDate") LocalDate to,@Param("departmentId") Long departmentId,@Param("employeeId") Long employeeId,Pageable page);

    @Query("select i from PayrollItem i where (:fromPeriod is null or i.run.year*100+i.run.month>=:fromPeriod) and (:toPeriod is null or i.run.year*100+i.run.month<=:toPeriod) and (:departmentId is null or i.employee.department.id=:departmentId) and (:employeeId is null or i.employee.id=:employeeId)")
    Page<PayrollItem> payroll(@Param("fromPeriod") Integer fromPeriod,@Param("toPeriod") Integer toPeriod,@Param("departmentId") Long departmentId,@Param("employeeId") Long employeeId,Pageable page);

    @Query("select r from PerformanceReview r where (:fromInstant is null or r.createdAt>=:fromInstant) and (:toExclusive is null or r.createdAt<:toExclusive) and (:departmentId is null or r.employee.department.id=:departmentId) and (:employeeId is null or r.employee.id=:employeeId)")
    Page<PerformanceReview> performance(@Param("fromInstant") Instant from,@Param("toExclusive") Instant toExclusive,@Param("departmentId") Long departmentId,@Param("employeeId") Long employeeId,Pageable page);
}
