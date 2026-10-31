package com.hrgenius.payroll.controller;

import com.hrgenius.common.PageResponse;
import com.hrgenius.payroll.dto.*;
import com.hrgenius.payroll.service.PayrollService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController @RequestMapping("/api/v1/payroll")
public class PayrollController {
    private final PayrollService payroll;
    public PayrollController(PayrollService payroll){this.payroll=payroll;}
    @GetMapping("/me") public List<PayrollItemResponse> mine(@AuthenticationPrincipal Jwt jwt){return payroll.myPayslips(jwt.getClaimAsString("email"));}
    @GetMapping("/assignments") @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public PageResponse<SalaryAssignmentResponse> assignments(@PageableDefault(size=50,sort="employee.lastName") Pageable pageable){return payroll.assignments(pageable);}
    @PutMapping("/assignments") @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public SalaryAssignmentResponse assignment(@Valid @RequestBody SalaryAssignmentRequest request){return payroll.setAssignment(request);}
    @GetMapping("/runs") @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public PageResponse<PayrollRunResponse> runs(@PageableDefault(size=50) Pageable pageable){return payroll.runs(pageable);}
    @PostMapping("/runs") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public PayrollRunResponse run(@RequestParam YearMonth period){return payroll.run(period);}
    @GetMapping("/runs/{id}/items") @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public PageResponse<PayrollItemResponse> items(@PathVariable long id,@PageableDefault(size=100,sort="employee.lastName") Pageable pageable){return payroll.items(id,pageable);}
    @PostMapping("/runs/{id}/approve") @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public PayrollRunResponse approve(@PathVariable long id,@AuthenticationPrincipal Jwt jwt){return payroll.approve(id,jwt.getClaimAsString("email"));}
}
