package com.hrgenius.report.service;

import com.hrgenius.attendance.entity.AttendanceRecord;
import com.hrgenius.common.ConflictException;
import com.hrgenius.employee.entity.Employee;
import com.hrgenius.leave.entity.LeaveBalance;
import com.hrgenius.leave.entity.LeaveRequest;
import com.hrgenius.onboarding.entity.OnboardingPlan;
import com.hrgenius.payroll.entity.PayrollItem;
import com.hrgenius.performance.entity.PerformanceReview;
import com.hrgenius.recruitment.entity.JobApplication;
import com.hrgenius.report.dto.*;
import com.hrgenius.report.repository.ReportQueryRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
import java.util.function.Function;

@Service
public class ReportService {
    private final ReportQueryRepository queries;
    public ReportService(ReportQueryRepository queries){this.queries=queries;}

    @Transactional(readOnly=true)
    public HeadcountReport headcount(LocalDate from,LocalDate to){
        return headcount(from,to,null,null);
    }
    @Transactional(readOnly=true)
    private HeadcountReport headcount(LocalDate from,LocalDate to,Long departmentId,Long employeeId){
        validateDates(from,to);
        long total=queries.employeeCount(departmentId,employeeId), separated=queries.separatedCount(departmentId,employeeId);
        List<DepartmentHeadcount> departments=queries.departments(from,to,departmentId,employeeId).stream().map(r->new DepartmentHeadcount((String)r[0],((Number)r[1]).longValue())).toList();
        return new HeadcountReport(total,total-separated,separated,queries.joinerCount(from,to,departmentId,employeeId),from,to,departments);
    }

    @Transactional(readOnly=true)
    public ReportDataset report(String path,LocalDate from,LocalDate to,Long departmentId,Long employeeId,Pageable requested){
        if(from!=null&&to!=null&&to.isBefore(from))throw new ConflictException("The report end date must be on or after its start date");
        ReportType type;try{type=ReportType.from(path);}catch(IllegalArgumentException ex){throw new ConflictException(ex.getMessage());}
        Pageable page=pageable(type,requested);
        return switch(type){
            case HEADCOUNT -> headcountDataset(from,to,departmentId,employeeId,page);
            case EMPLOYEE_DIRECTORY -> mapped(type,List.of("Employee number","Employee","Work email","Department","Designation","Location","Status","Employment type","Date joined"),
                    queries.employees(from,to,departmentId,employeeId,page),e->row(e.getEmployeeNumber(),name(e),e.getWorkEmail(),department(e),designation(e),location(e),e.getStatus(),e.getEmploymentType(),e.getDateOfJoining()));
            case DEPARTMENT_WISE -> aggregate(type,List.of("Department","Employees"),queries.departments(from,to,departmentId,employeeId),page);
            case DESIGNATION_WISE -> aggregate(type,List.of("Designation","Employees"),queries.designations(from,to,departmentId,employeeId),page);
            case ATTENDANCE,LATE_ARRIVALS,ABSENCES,OVERTIME -> mapped(type,List.of("Work date","Employee number","Employee","Department","Status","Check in","Check out","Worked minutes","Late minutes","Overtime minutes"),
                    queries.attendance(from,to,departmentId,employeeId,type==ReportType.LATE_ARRIVALS,type==ReportType.ABSENCES,type==ReportType.OVERTIME,page),this::attendanceRow);
            case LEAVE -> mapped(type,List.of("Employee number","Employee","Department","Leave type","Start date","End date","Days","Status","Submitted"),
                    queries.leaveRequests(from,to,departmentId,employeeId,page),this::leaveRow);
            case LEAVE_BALANCES -> mapped(type,List.of("Employee number","Employee","Department","Leave type","Year","Entitled days","Used days","Remaining days"),
                    queries.leaveBalances(from==null?null:(short)from.getYear(),to==null?null:(short)to.getYear(),departmentId,employeeId,page),this::balanceRow);
            case RECRUITMENT -> mapped(type,List.of("Applied at","Opening","Department","Candidate","Candidate email","Stage"),
                    queries.recruitment(fromStart(from),toExclusive(to),departmentId,page),this::applicationRow);
            case HIRING_FUNNEL -> aggregate(type,List.of("Application stage","Applications"),queries.applicationFunnel(fromStart(from),toExclusive(to),departmentId),page);
            case ONBOARDING -> mapped(type,List.of("Employee number","Employee","Department","Template","Start date","Status"),
                    queries.onboarding(from,to,departmentId,employeeId,page),this::onboardingRow);
            case PAYROLL -> mapped(type,List.of("Pay period","Employee number","Employee","Department","Currency","Gross","Deductions","Net","Run status"),
                    queries.payroll(periodFrom(from),periodTo(to),departmentId,employeeId,page),this::payrollRow);
            case PERFORMANCE -> mapped(type,List.of("Cycle","Employee number","Employee","Department","Reviewer","Status","Self rating","Manager rating"),
                    queries.performance(fromStart(from),toExclusive(to),departmentId,employeeId,page),this::performanceRow);
        };
    }

    @Transactional(readOnly=true)
    public ReportExportData exportData(String path,LocalDate from,LocalDate to,Long departmentId,Long employeeId,Pageable requested){
        List<List<Object>> rows=new ArrayList<>();ReportDataset first=report(path,from,to,departmentId,employeeId,PageRequest.of(0,100,requested.getSort()));rows.addAll(first.rows());
        int next=1;while(rows.size()<20_000&&next<first.totalPages()){rows.addAll(report(path,from,to,departmentId,employeeId,PageRequest.of(next++,100,requested.getSort())).rows());}
        boolean truncated=rows.size()<first.totalElements();
        ReportDataset result=new ReportDataset(first.report(),first.columns(),rows,0,rows.size(),first.totalElements(),1);
        return new ReportExportData(result,truncated);
    }

    private ReportDataset headcountDataset(LocalDate from,LocalDate to,Long departmentId,Long employeeId,Pageable page){
        LocalDate end=to==null?LocalDate.now():to,start=from==null?end.withDayOfYear(1):from;
        HeadcountReport summary=headcount(start,end,departmentId,employeeId);
        List<List<Object>> all=List.of(row("Total employees",summary.totalEmployees()),row("Active employees",summary.activeEmployees()),row("Separated employees",summary.inactiveEmployees()),row("New joiners",summary.newJoiners()));
        return slice(ReportType.HEADCOUNT,List.of("Metric","Value"),all,page);
    }

    private <T> ReportDataset mapped(ReportType type,List<String> columns,Page<T> data,Function<T,List<Object>> mapper){
        return new ReportDataset(type.path(),columns,data.getContent().stream().map(mapper).toList(),data.getNumber(),data.getSize(),data.getTotalElements(),data.getTotalPages());
    }
    private ReportDataset aggregate(ReportType type,List<String> columns,List<Object[]> data,Pageable page){
        List<Object[]> sorted=new ArrayList<>(data);Sort.Order order=page.getSort().stream().findFirst().orElse(null);
        if(order!=null){Comparator<Object[]> comparator=order.getProperty().equals("count")?Comparator.comparingLong(r->((Number)r[1]).longValue()):Comparator.comparing(r->String.valueOf(r[0]),String.CASE_INSENSITIVE_ORDER);if(order.isDescending())comparator=comparator.reversed();sorted.sort(comparator);}
        List<List<Object>> all=sorted.stream().map(r->row(r[0],r[1])).toList();return slice(type,columns,all,page);
    }
    private ReportDataset slice(ReportType type,List<String> columns,List<List<Object>> all,Pageable page){
        int start=Math.min((int)Math.min(Integer.MAX_VALUE,page.getOffset()),all.size()),end=Math.min(start+page.getPageSize(),all.size());
        return new ReportDataset(type.path(),columns,all.subList(start,end),page.getPageNumber(),page.getPageSize(),all.size(),(int)Math.ceil((double)all.size()/page.getPageSize()));
    }
    private Pageable pageable(ReportType type,Pageable requested){
        Map<ReportType,String> defaults=Map.ofEntries(Map.entry(ReportType.EMPLOYEE_DIRECTORY,"lastName"),Map.entry(ReportType.DEPARTMENT_WISE,"department"),Map.entry(ReportType.DESIGNATION_WISE,"designation"),Map.entry(ReportType.HIRING_FUNNEL,"stage"),Map.entry(ReportType.ATTENDANCE,"workDate"),Map.entry(ReportType.LATE_ARRIVALS,"workDate"),Map.entry(ReportType.ABSENCES,"workDate"),Map.entry(ReportType.OVERTIME,"workDate"),Map.entry(ReportType.LEAVE,"startDate"),Map.entry(ReportType.LEAVE_BALANCES,"calendarYear"),Map.entry(ReportType.RECRUITMENT,"appliedAt"),Map.entry(ReportType.ONBOARDING,"startDate"),Map.entry(ReportType.PAYROLL,"id"),Map.entry(ReportType.PERFORMANCE,"createdAt"));
        Map<ReportType,Set<String>> allowed=Map.ofEntries(Map.entry(ReportType.DEPARTMENT_WISE,Set.of("department","count")),Map.entry(ReportType.DESIGNATION_WISE,Set.of("designation","count")),Map.entry(ReportType.HIRING_FUNNEL,Set.of("stage","count")),Map.entry(ReportType.EMPLOYEE_DIRECTORY,Set.of("lastName","employeeNumber","status","dateOfJoining")),Map.entry(ReportType.ATTENDANCE,Set.of("workDate","status","lateMinutes","overtimeMinutes")),Map.entry(ReportType.LATE_ARRIVALS,Set.of("workDate","lateMinutes")),Map.entry(ReportType.ABSENCES,Set.of("workDate","employee.id")),Map.entry(ReportType.OVERTIME,Set.of("workDate","overtimeMinutes")),Map.entry(ReportType.LEAVE,Set.of("startDate","endDate","status","requestedDays")),Map.entry(ReportType.LEAVE_BALANCES,Set.of("calendarYear","entitledDays","usedDays")),Map.entry(ReportType.RECRUITMENT,Set.of("appliedAt","status")),Map.entry(ReportType.ONBOARDING,Set.of("startDate","status")),Map.entry(ReportType.PAYROLL,Set.of("id","netPay","grossPay")),Map.entry(ReportType.PERFORMANCE,Set.of("createdAt","status","selfRating","managerRating")));
        Sort.Order order=requested.getSort().stream().findFirst().orElse(null);String property=order!=null&&allowed.getOrDefault(type,Set.of()).contains(order.getProperty())?order.getProperty():defaults.getOrDefault(type,"id");
        Sort sort=Sort.by(order!=null&&order.isDescending()?Sort.Direction.DESC:Sort.Direction.ASC,property);
        return PageRequest.of(requested.getPageNumber(),Math.min(100,Math.max(1,requested.getPageSize())),sort);
    }
    private List<Object> attendanceRow(AttendanceRecord a){return row(a.getWorkDate(),a.getEmployee().getEmployeeNumber(),name(a.getEmployee()),department(a.getEmployee()),a.getStatus(),a.getCheckInAt(),a.getCheckOutAt(),a.getWorkedMinutes(),a.getLateMinutes(),a.getOvertimeMinutes());}
    private List<Object> leaveRow(LeaveRequest l){return row(l.getEmployee().getEmployeeNumber(),name(l.getEmployee()),department(l.getEmployee()),l.getLeaveType().getName(),l.getStartDate(),l.getEndDate(),l.getRequestedDays(),l.getStatus(),l.getCreatedAt());}
    private List<Object> balanceRow(LeaveBalance b){return row(b.getEmployee().getEmployeeNumber(),name(b.getEmployee()),department(b.getEmployee()),b.getLeaveType().getName(),b.getCalendarYear(),b.getEntitledDays(),b.getUsedDays(),b.getRemainingDays());}
    private List<Object> applicationRow(JobApplication a){return row(a.getAppliedAt(),a.getOpening().getTitle(),a.getOpening().getDepartment()==null?null:a.getOpening().getDepartment().getName(),a.getCandidate().getFirstName()+" "+a.getCandidate().getLastName(),a.getCandidate().getEmail(),a.getStatus());}
    private List<Object> onboardingRow(OnboardingPlan p){return row(p.getEmployee().getEmployeeNumber(),name(p.getEmployee()),department(p.getEmployee()),p.getTemplate().getName(),p.getStartDate(),p.getStatus());}
    private List<Object> payrollRow(PayrollItem i){return row(i.getRun().getPeriod(),i.getEmployee().getEmployeeNumber(),name(i.getEmployee()),department(i.getEmployee()),i.getCurrency(),i.getGrossPay(),i.getFixedDeductions(),i.getNetPay(),i.getRun().getStatus());}
    private List<Object> performanceRow(PerformanceReview r){return row(r.getCycleName(),r.getEmployee().getEmployeeNumber(),name(r.getEmployee()),department(r.getEmployee()),name(r.getReviewer()),r.getStatus(),r.getSelfRating(),r.getManagerRating());}
    private static List<Object> row(Object... values){return Arrays.asList(values);}
    private static String name(Employee e){return e.getFirstName()+" "+e.getLastName();}
    private static String department(Employee e){return e.getDepartment()==null?"Unassigned":e.getDepartment().getName();}
    private static String designation(Employee e){return e.getDesignation()==null?"Unassigned":e.getDesignation().getTitle();}
    private static String location(Employee e){return e.getLocation()==null?"Unassigned":e.getLocation().getName();}
    private static Instant fromStart(LocalDate d){return d==null?null:d.atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant();}
    private static Instant toExclusive(LocalDate d){return d==null?null:d.plusDays(1).atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant();}
    private static Integer periodFrom(LocalDate d){return d==null?null:d.getYear()*100+d.getMonthValue();}
    private static Integer periodTo(LocalDate d){return d==null?null:d.getYear()*100+d.getMonthValue();}
    private static void validateDates(LocalDate from,LocalDate to){if(from==null||to==null||to.isBefore(from))throw new ConflictException("Provide a valid date range");}
}
