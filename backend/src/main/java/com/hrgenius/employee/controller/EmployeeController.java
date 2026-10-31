package com.hrgenius.employee.controller;

import com.hrgenius.common.PageResponse;
import com.hrgenius.employee.dto.*;
import com.hrgenius.employee.entity.EmployeeStatus;
import com.hrgenius.employee.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private final EmployeeService employees;
    public EmployeeController(EmployeeService employees) { this.employees = employees; }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER','HR_EXECUTIVE')")
    public PageResponse<EmployeeResponse> list(@RequestParam(required = false) String q,
            @RequestParam(required = false) Long departmentId, @RequestParam(required = false) Long designationId,
            @RequestParam(required = false) Long locationId, @RequestParam(required = false) EmployeeStatus status,
            @PageableDefault(size = 20, sort = "lastName") Pageable pageable) {
        return employees.list(q, departmentId, designationId, locationId, status, pageable);
    }

    @GetMapping("/me")
    public EmployeeSelfProfileResponse me(@AuthenticationPrincipal Jwt jwt) {
        return EmployeeSelfProfileResponse.from(employees.getByEmail(jwt.getClaimAsString("email")));
    }

    @PutMapping("/me")
    public EmployeeSelfProfileResponse updateMe(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody EmployeeSelfUpdateRequest request) {
        return EmployeeSelfProfileResponse.from(employees.updatePersonalDetails(jwt.getClaimAsString("email"), request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER','HR_EXECUTIVE')")
    public EmployeeDetailResponse get(@PathVariable Long id) { return EmployeeDetailResponse.from(employees.get(id)); }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public List<EmployeeHistoryResponse> history(@PathVariable Long id) { return employees.history(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public EmployeeResponse create(@Valid @RequestBody EmployeeRequest request) {
        return EmployeeResponse.from(employees.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public EmployeeResponse update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return EmployeeResponse.from(employees.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public EmployeeResponse setStatus(@PathVariable Long id, @Valid @RequestBody EmployeeStatusRequest request) {
        return EmployeeResponse.from(employees.setStatus(id, request.status()));
    }
}
