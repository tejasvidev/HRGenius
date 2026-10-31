package com.hrgenius.payroll.service;

import com.hrgenius.common.ConflictException;
import com.hrgenius.common.PageRequests;
import com.hrgenius.common.PageResponse;
import com.hrgenius.common.ResourceNotFoundException;
import com.hrgenius.employee.entity.EmployeeStatus;
import com.hrgenius.employee.repository.EmployeeRepository;
import com.hrgenius.payroll.dto.*;
import com.hrgenius.payroll.entity.*;
import com.hrgenius.payroll.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class PayrollService {
    private final EmployeeRepository employees;
    private final SalaryAssignmentRepository assignments;
    private final PayrollRunRepository runs;
    private final PayrollItemRepository items;
    public PayrollService(EmployeeRepository employees,SalaryAssignmentRepository assignments,PayrollRunRepository runs,PayrollItemRepository items){this.employees=employees;this.assignments=assignments;this.runs=runs;this.items=items;}

    @Transactional(readOnly=true)
    public PageResponse<SalaryAssignmentResponse> assignments(Pageable pageable){return PageResponse.from(assignments.findAllByOrderByEmployee_LastNameAsc(PageRequests.capped(pageable)),SalaryAssignmentResponse::from);}

    @Transactional
    public SalaryAssignmentResponse setAssignment(SalaryAssignmentRequest input){
        var employee=employees.findById(input.employeeId()).orElseThrow(()->new ResourceNotFoundException("Employee",input.employeeId()));
        if(employee.getStatus()!=EmployeeStatus.ACTIVE)throw new ConflictException("Salary can only be assigned to an active employee");
        if(input.fixedDeduction().compareTo(input.monthlyGross())>0)throw new ConflictException("Fixed deduction cannot exceed monthly gross pay");
        var assignment=assignments.findByEmployee_Id(employee.getId()).orElseGet(()->new SalaryAssignment(employee,input.currency(),input.monthlyGross(),input.fixedDeduction(),input.effectiveFrom()));
        assignment.update(input.currency(),input.monthlyGross(),input.fixedDeduction(),input.effectiveFrom());
        return SalaryAssignmentResponse.from(assignments.save(assignment));
    }

    @Transactional
    public PayrollRunResponse run(YearMonth period){
        if(period.isAfter(YearMonth.now()))throw new ConflictException("Cannot run payroll for a future period");
        short year=(short)period.getYear();byte month=(byte)period.getMonthValue();
        if(runs.existsByYearAndMonth(year,month))throw new ConflictException("Payroll already exists for this period");
        var eligibleDate=period.atDay(1);int employeeCount=0;String currency=null;int pageNumber=0;Page<SalaryAssignment> page;
        do{
            page=assignments.findAllByActiveTrueAndEffectiveFromLessThanEqualOrderByIdAsc(eligibleDate,org.springframework.data.domain.PageRequest.of(pageNumber++,500));
            for(var assignment:page.getContent())if(assignment.getEmployee().getStatus()==EmployeeStatus.ACTIVE){
                if(currency==null)currency=assignment.getCurrency();
                else if(!currency.equals(assignment.getCurrency()))throw new ConflictException("All active salary assignments in a payroll run must use the same currency");
                employeeCount++;
            }
        }while(page.hasNext());
        if(employeeCount==0)throw new ConflictException("No active employees have an effective salary assignment for this period");
        PayrollRun run=runs.save(new PayrollRun(period,currency));
        BigDecimal grossTotal=BigDecimal.ZERO,deductionTotal=BigDecimal.ZERO,netTotal=BigDecimal.ZERO;pageNumber=0;
        do{
            page=assignments.findAllByActiveTrueAndEffectiveFromLessThanEqualOrderByIdAsc(eligibleDate,org.springframework.data.domain.PageRequest.of(pageNumber++,500));
            List<PayrollItem> batch=new ArrayList<>();
            for(var assignment:page.getContent())if(assignment.getEmployee().getStatus()==EmployeeStatus.ACTIVE){
                batch.add(new PayrollItem(run,assignment.getEmployee(),currency,assignment.getMonthlyGross(),assignment.getFixedDeduction()));
                grossTotal=grossTotal.add(assignment.getMonthlyGross());deductionTotal=deductionTotal.add(assignment.getFixedDeduction());
                netTotal=netTotal.add(assignment.getMonthlyGross().subtract(assignment.getFixedDeduction()));
            }
            items.saveAll(batch);
        }while(page.hasNext());
        run.totals(employeeCount,grossTotal,deductionTotal,netTotal);
        return PayrollRunResponse.from(runs.save(run));
    }

    @Transactional(readOnly=true)
    public PageResponse<PayrollRunResponse> runs(Pageable pageable){return PageResponse.from(runs.findAllByOrderByYearDescMonthDesc(PageRequests.capped(pageable)),PayrollRunResponse::from);}

    @Transactional(readOnly=true)
    public PageResponse<PayrollItemResponse> items(long runId,Pageable pageable){
        if(!runs.existsById(runId))throw new ResourceNotFoundException("Payroll run",runId);
        return PageResponse.from(items.findAllByRun_IdOrderByEmployee_LastNameAsc(runId,PageRequests.capped(pageable)),PayrollItemResponse::from);
    }

    @Transactional
    public PayrollRunResponse approve(long id,String email){
        PayrollRun run=runs.lockById(id).orElseThrow(()->new ResourceNotFoundException("Payroll run",id));
        var approver=employees.findByWorkEmailIgnoreCase(email).orElse(null);
        try{run.approve(approver);}catch(IllegalStateException ex){throw new ConflictException(ex.getMessage());}
        return PayrollRunResponse.from(runs.save(run));
    }

    @Transactional(readOnly=true)
    public List<PayrollItemResponse> myPayslips(String email){
        var employee=employees.findByWorkEmailIgnoreCase(email).orElseThrow(()->new ResourceNotFoundException("Employee profile",null));
        return items.payslipsFor(employee.getId()).stream().filter(item->item.getRun().getStatus()==PayrollStatus.APPROVED).map(PayrollItemResponse::from).toList();
    }
}
