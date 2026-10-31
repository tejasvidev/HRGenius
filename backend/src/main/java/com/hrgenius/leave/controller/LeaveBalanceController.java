package com.hrgenius.leave.controller;

import com.hrgenius.leave.dto.LeaveBalanceRequest;
import com.hrgenius.leave.dto.LeaveBalanceResponse;
import com.hrgenius.leave.service.LeaveManagementService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leave/balances")
public class LeaveBalanceController {
    private final LeaveManagementService leave;
    public LeaveBalanceController(LeaveManagementService leave) { this.leave = leave; }

    @GetMapping("/me")
    public List<LeaveBalanceResponse> myBalances(@AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") short year) {
        return leave.myBalances(jwt.getClaimAsString("email"), year == 0 ? (short) Year.now().getValue() : year);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER','HR_EXECUTIVE')")
    public List<LeaveBalanceResponse> forEmployee(@PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") short year) {
        return leave.balancesFor(employeeId, year == 0 ? (short) Year.now().getValue() : year);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public LeaveBalanceResponse setBalance(@Valid @RequestBody LeaveBalanceRequest request) { return leave.setBalance(request); }
}
