package com.hrgenius.leave.controller;

import com.hrgenius.leave.dto.LeaveAccrualRuleRequest;
import com.hrgenius.leave.dto.LeaveAccrualRuleResponse;
import com.hrgenius.leave.dto.LeaveAccrualRunResponse;
import com.hrgenius.leave.service.LeaveManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leave")
public class LeaveAccrualController {
    private final LeaveManagementService leave;
    public LeaveAccrualController(LeaveManagementService leave) { this.leave = leave; }

    @GetMapping("/accrual-rules")
    public List<LeaveAccrualRuleResponse> rules(@RequestParam(defaultValue = "false") boolean includeInactive) { return leave.listAccrualRules(includeInactive); }

    @PutMapping("/accrual-rules")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public LeaveAccrualRuleResponse save(@Valid @RequestBody LeaveAccrualRuleRequest request) { return leave.saveAccrualRule(request); }

    @PatchMapping("/accrual-rules/{id}/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public LeaveAccrualRuleResponse active(@PathVariable long id, @RequestParam boolean active) { return leave.setAccrualRuleActive(id, active); }

    @PostMapping("/accrual-runs")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public LeaveAccrualRunResponse run(@RequestParam YearMonth period) { return leave.runAccrual(period); }
}
