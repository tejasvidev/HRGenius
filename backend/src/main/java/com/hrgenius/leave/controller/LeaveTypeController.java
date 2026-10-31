package com.hrgenius.leave.controller;

import com.hrgenius.leave.dto.LeaveTypeRequest;
import com.hrgenius.leave.dto.LeaveTypeResponse;
import com.hrgenius.leave.service.LeaveManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leave/types")
public class LeaveTypeController {
    private final LeaveManagementService leave;
    public LeaveTypeController(LeaveManagementService leave) { this.leave = leave; }

    @GetMapping
    public List<LeaveTypeResponse> list(@RequestParam(defaultValue = "false") boolean includeInactive) {
        return leave.listTypes(includeInactive);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public LeaveTypeResponse create(@Valid @RequestBody LeaveTypeRequest request) { return leave.createType(request); }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public LeaveTypeResponse setActive(@PathVariable Long id, @RequestParam boolean active) {
        return leave.setTypeActive(id, active);
    }
}
