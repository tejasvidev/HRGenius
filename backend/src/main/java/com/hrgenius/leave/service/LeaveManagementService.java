package com.hrgenius.leave.service;

import com.hrgenius.common.ConflictException;
import com.hrgenius.common.PageRequests;
import com.hrgenius.common.PageResponse;
import com.hrgenius.common.ResourceNotFoundException;
import com.hrgenius.employee.entity.Employee;
import com.hrgenius.employee.entity.EmployeeStatus;
import com.hrgenius.employee.repository.EmployeeRepository;
import com.hrgenius.leave.dto.*;
import com.hrgenius.leave.entity.*;
import com.hrgenius.leave.repository.*;
import com.hrgenius.notification.OutboundEmail;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LeaveManagementService {
    private static final Set<LeaveRequestStatus> BLOCKING = Set.of(LeaveRequestStatus.PENDING, LeaveRequestStatus.APPROVED);
    private static final Set<String> HR_ROLES = Set.of("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_HR_MANAGER", "ROLE_HR_EXECUTIVE");
    private final LeaveTypeRepository types;
    private final LeaveBalanceRepository balances;
    private final LeaveRequestRepository requests;
    private final EmployeeRepository employees;
    private final LeaveCalendarPolicyRepository calendarPolicies;
    private final LeaveHolidayRepository holidays;
    private final LeaveAccrualRuleRepository accrualRules;
    private final LeaveAccrualLedgerRepository accrualLedger;
    private final ApplicationEventPublisher events;

    public LeaveManagementService(LeaveTypeRepository types, LeaveBalanceRepository balances,
            LeaveRequestRepository requests, EmployeeRepository employees, LeaveCalendarPolicyRepository calendarPolicies,
            LeaveHolidayRepository holidays, LeaveAccrualRuleRepository accrualRules, LeaveAccrualLedgerRepository accrualLedger,
            ApplicationEventPublisher events) {
        this.types = types; this.balances = balances; this.requests = requests; this.employees = employees;
        this.calendarPolicies=calendarPolicies;this.holidays=holidays;
        this.accrualRules=accrualRules;this.accrualLedger=accrualLedger;
        this.events=events;
    }

    @Transactional(readOnly = true)
    public List<LeaveTypeResponse> listTypes(boolean includeInactive) {
        return (includeInactive ? types.findAllByOrderByNameAsc() : types.findAllByActiveTrueOrderByNameAsc())
                .stream().map(LeaveTypeResponse::from).toList();
    }

    @Transactional
    public LeaveTypeResponse createType(LeaveTypeRequest request) {
        if (types.existsByCodeIgnoreCase(request.code())) throw new ConflictException("Leave type code already exists");
        if (types.existsByNameIgnoreCase(request.name())) throw new ConflictException("Leave type name already exists");
        return LeaveTypeResponse.from(types.save(new LeaveType(request.code(), request.name(), request.description())));
    }

    @Transactional
    public LeaveTypeResponse setTypeActive(Long id, boolean active) {
        LeaveType type = types.findById(id).orElseThrow(() -> new ResourceNotFoundException("Leave type", id));
        type.setActive(active); return LeaveTypeResponse.from(types.save(type));
    }

    @Transactional
    public LeaveBalanceResponse setBalance(LeaveBalanceRequest request) {
        Employee employee = employee(request.employeeId());
        LeaveType type = type(request.leaveTypeId());
        if (!type.isActive()) throw new ConflictException("Cannot assign balance to an inactive leave type");
        LeaveBalance balance = balances.lockForUpdate(employee.getId(), type.getId(), request.year())
                .orElseGet(() -> new LeaveBalance(employee, type, request.year(), request.entitledDays()));
        if (balance.getId() != null) {
            try { balance.setEntitlement(request.entitledDays()); }
            catch (IllegalArgumentException exception) { throw new ConflictException(exception.getMessage()); }
        }
        return LeaveBalanceResponse.from(balances.save(balance));
    }

    @Transactional(readOnly = true)
    public List<LeaveBalanceResponse> myBalances(String email, short year) {
        Employee owner = employees.findByWorkEmailIgnoreCase(email).orElseThrow(() -> new ResourceNotFoundException("Employee profile", null));
        if (owner.getStatus() == EmployeeStatus.SEPARATED) throw new ConflictException("Separated employees cannot submit leave requests");
        return balances.findAllByEmployee_IdAndCalendarYearOrderByLeaveType_Name(owner.getId(), year)
                .stream().map(LeaveBalanceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<LeaveBalanceResponse> balancesFor(Long employeeId, short year) {
        employee(employeeId);
        return balances.findAllByEmployee_IdAndCalendarYearOrderByLeaveType_Name(employeeId, year)
                .stream().map(LeaveBalanceResponse::from).toList();
    }

    @Transactional
    public LeaveRequestResponse submit(String email, LeaveRequestCreate input) {
        Employee owner = employees.findByWorkEmailIgnoreCase(email).orElseThrow(() -> new ResourceNotFoundException("Employee profile", null));
        LeaveType type = type(input.leaveTypeId());
        if (!type.isActive()) throw new ConflictException("This leave type is not active");
        validateDates(input.startDate(), input.endDate());
        if (requests.existsByEmployee_IdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                owner.getId(), BLOCKING, input.endDate(), input.startDate()))
            throw new ConflictException("This request overlaps an existing pending or approved leave request");
        short year = (short) input.startDate().getYear();
        LeaveBalance balance = balances.findByEmployee_IdAndLeaveType_IdAndCalendarYear(owner.getId(), type.getId(), year)
                .orElseThrow(() -> new ConflictException("No balance is configured for this leave type and year"));
        BigDecimal days = days(input.startDate(), input.endDate());
        if (balance.getRemainingDays().compareTo(days) < 0) throw new ConflictException("Requested days exceed the remaining balance");
        LeaveRequest saved=requests.save(new LeaveRequest(owner, type, input.startDate(), input.endDate(), days, input.reason()));
        if(owner.getManager()!=null)events.publishEvent(new OutboundEmail(owner.getManager().getWorkEmail(),"Leave request awaiting review",
                owner.getFirstName()+" "+owner.getLastName()+" submitted a "+type.getName()+" request for "+input.startDate()+" to "+input.endDate()+" ("+days+" working days)."));
        return LeaveRequestResponse.from(saved);
    }

    @Transactional(readOnly=true)
    public LeaveCalendarPolicyResponse calendarPolicy(){return LeaveCalendarPolicyResponse.from(calendarPolicies.findById((byte)1).orElseGet(LeaveCalendarPolicy::defaults));}

    @Transactional
    public LeaveCalendarPolicyResponse updateCalendarPolicy(LeaveCalendarPolicyRequest request){
        LeaveCalendarPolicy p=calendarPolicies.findById((byte)1).orElseGet(LeaveCalendarPolicy::defaults);
        p.setWorkingDays(request.workingDays());return LeaveCalendarPolicyResponse.from(calendarPolicies.save(p));
    }

    @Transactional(readOnly=true)
    public List<LeaveHolidayResponse> listHolidays(short year,boolean includeInactive){LocalDate start=LocalDate.of(year,1,1),end=LocalDate.of(year,12,31);
        return (includeInactive?holidays.findAllByDateBetweenOrderByDateAsc(start,end):holidays.findAllByActiveTrueAndDateBetweenOrderByDateAsc(start,end)).stream().map(LeaveHolidayResponse::from).toList();}

    @Transactional
    public LeaveHolidayResponse createHoliday(LeaveHolidayRequest request){if(holidays.existsByDate(request.date()))throw new ConflictException("A holiday is already configured for this date");return LeaveHolidayResponse.from(holidays.save(new LeaveHoliday(request.date(),request.name(),request.description())));}

    @Transactional
    public LeaveHolidayResponse setHolidayActive(long id,boolean active){LeaveHoliday h=holidays.findById(id).orElseThrow(()->new ResourceNotFoundException("Leave holiday",id));h.setActive(active);return LeaveHolidayResponse.from(holidays.save(h));}

    @Transactional(readOnly=true)
    public List<LeaveAccrualRuleResponse> listAccrualRules(boolean includeInactive){
        var values=includeInactive?accrualRules.findAllByOrderByLeaveType_NameAsc():accrualRules.findAllByActiveTrueOrderByLeaveType_NameAsc();
        return values.stream().map(LeaveAccrualRuleResponse::from).toList();
    }

    @Transactional
    public LeaveAccrualRuleResponse saveAccrualRule(LeaveAccrualRuleRequest input){
        LeaveType type=type(input.leaveTypeId());
        if(!type.isActive())throw new ConflictException("Accrual rules require an active leave type");
        if(input.annualCap()!=null&&input.annualCap().compareTo(input.monthlyDays())<0)throw new ConflictException("Annual accrual cap cannot be below one monthly grant");
        LeaveAccrualRule rule=accrualRules.findByLeaveType_Id(type.getId()).orElseGet(()->new LeaveAccrualRule(type,input.monthlyDays(),input.annualCap(),input.effectiveFrom()));
        rule.update(input.monthlyDays(),input.annualCap(),input.effectiveFrom());rule.setActive(true);
        return LeaveAccrualRuleResponse.from(accrualRules.save(rule));
    }

    @Transactional
    public LeaveAccrualRuleResponse setAccrualRuleActive(long id,boolean active){LeaveAccrualRule r=accrualRules.findById(id).orElseThrow(()->new ResourceNotFoundException("Leave accrual rule",id));r.setActive(active);return LeaveAccrualRuleResponse.from(accrualRules.save(r));}

    @Transactional
    public LeaveAccrualRunResponse runAccrual(YearMonth period){
        if(period.isAfter(YearMonth.now()))throw new ConflictException("Cannot accrue a future period");
        BigDecimal total=BigDecimal.ZERO;int processed=0;
        for(LeaveAccrualRule rule:accrualRules.findAllByActiveTrueOrderByLeaveType_NameAsc()){
            if(period.atDay(1).isBefore(rule.getEffectiveFrom())||!rule.getLeaveType().isActive())continue;
            int pageNumber=0;Page<Employee> employeePage;
            do {
                employeePage=employees.findAllByStatus(EmployeeStatus.ACTIVE,PageRequest.of(pageNumber,500,Sort.by("id").ascending()));
                for(Employee owner:employeePage.getContent()){
                    short year=(short)period.getYear();byte month=(byte)period.getMonthValue();
                    if(accrualLedger.existsByEmployee_IdAndLeaveType_IdAndYearAndMonth(owner.getId(),rule.getLeaveType().getId(),year,month))continue;
                    LeaveBalance balance=balances.lockForUpdate(owner.getId(),rule.getLeaveType().getId(),year)
                            .orElseGet(()->balances.save(new LeaveBalance(owner,rule.getLeaveType(),year,BigDecimal.ZERO)));
                    BigDecimal grant=rule.getMonthlyDays();
                    if(rule.getAnnualCap()!=null)grant=grant.min(rule.getAnnualCap().subtract(balance.getEntitledDays()).max(BigDecimal.ZERO));
                    balance.grant(grant);balances.save(balance);
                    accrualLedger.save(new LeaveAccrualLedger(owner,rule.getLeaveType(),year,month,grant));
                    total=total.add(grant);processed++;
                }
                pageNumber++;
            } while(employeePage.hasNext());
        }
        return new LeaveAccrualRunResponse(period,processed,total);
    }

    @Transactional(readOnly = true)
    public PageResponse<LeaveRequestResponse> listRequests(String email, Set<String> authorities, Pageable pageable) {
        var bounded = PageRequests.capped(pageable);
        if (authorities.stream().anyMatch(HR_ROLES::contains))
            return PageResponse.from(requests.findAll(bounded), LeaveRequestResponse::from);
        Employee viewer = employees.findByWorkEmailIgnoreCase(email).orElseThrow(() -> new ResourceNotFoundException("Employee profile", null));
        if (authorities.contains("ROLE_MANAGER"))
            return PageResponse.from(requests.findAllByEmployee_Manager_Id(viewer.getId(), bounded), LeaveRequestResponse::from);
        return PageResponse.from(requests.findAllByEmployee_Id(viewer.getId(), bounded), LeaveRequestResponse::from);
    }

    @Transactional
    public LeaveRequestResponse decide(Long id, String reviewerEmail, Set<String> authorities, LeaveDecisionRequest input) {
        LeaveRequest request = requests.findById(id).orElseThrow(() -> new ResourceNotFoundException("Leave request", id));
        if (request.getStatus() != LeaveRequestStatus.PENDING) throw new ConflictException("Only pending requests can be reviewed");
        if (request.getEmployee().getWorkEmail().equalsIgnoreCase(reviewerEmail))
            throw new AccessDeniedException("Employees cannot approve their own leave requests");
        Employee reviewer = employees.findByWorkEmailIgnoreCase(reviewerEmail).orElse(null);
        boolean hr = authorities.stream().anyMatch(HR_ROLES::contains);
        boolean managerOfOwner = authorities.contains("ROLE_MANAGER") && reviewer != null
                && request.getEmployee().getManager() != null
                && request.getEmployee().getManager().getId().equals(reviewer.getId());
        if (!hr && !managerOfOwner) throw new AccessDeniedException("You cannot review this leave request");
        boolean approve = input.approved();
        if (approve) {
            short year = (short) request.getStartDate().getYear();
            LeaveBalance balance = balances.lockForUpdate(request.getEmployee().getId(), request.getLeaveType().getId(), year)
                    .orElseThrow(() -> new ConflictException("No leave balance is configured for this request"));
            try { balance.consume(request.getRequestedDays()); }
            catch (IllegalStateException exception) { throw new ConflictException(exception.getMessage()); }
        }
        request.decide(reviewer, approve, input.comment());
        LeaveRequest saved=requests.save(request);
        events.publishEvent(new OutboundEmail(request.getEmployee().getWorkEmail(),"Leave request "+(approve?"approved":"declined"),
                "Your "+request.getLeaveType().getName()+" request for "+request.getStartDate()+" to "+request.getEndDate()+" was "+(approve?"approved":"declined by HR or your manager")+"."));
        return LeaveRequestResponse.from(saved);
    }

    @Transactional
    public LeaveRequestResponse cancel(Long id, String email) {
        LeaveRequest request = requests.findById(id).orElseThrow(() -> new ResourceNotFoundException("Leave request", id));
        if (!request.getEmployee().getWorkEmail().equalsIgnoreCase(email)) throw new AccessDeniedException("You can only cancel your own request");
        if (request.getStatus() != LeaveRequestStatus.PENDING) throw new ConflictException("Only pending requests can be cancelled");
        request.cancel(); return LeaveRequestResponse.from(requests.save(request));
    }

    private Employee employee(Long id) { return employees.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee", id)); }
    private LeaveType type(Long id) { return types.findById(id).orElseThrow(() -> new ResourceNotFoundException("Leave type", id)); }
    private void validateDates(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) throw new ConflictException("Leave end date must be on or after the start date");
        if (start.getYear() != end.getYear()) throw new ConflictException("Split leave requests across calendar years");
    }
    private BigDecimal days(LocalDate start, LocalDate end) {
        Set<DayOfWeek> working=calendarPolicies.findById((byte)1).orElseGet(LeaveCalendarPolicy::defaults).getWorkingDays();
        Set<LocalDate> holidayDates=new HashSet<>(holidays.findAllByActiveTrueAndDateBetweenOrderByDateAsc(start,end).stream().map(LeaveHoliday::getDate).toList());
        long count=0;for(LocalDate d=start;!d.isAfter(end);d=d.plusDays(1))if(working.contains(d.getDayOfWeek())&&!holidayDates.contains(d))count++;
        if(count==0)throw new ConflictException("This date range contains no working days");
        return BigDecimal.valueOf(count);
    }
}
