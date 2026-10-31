package com.hrgenius.organization.controller;

import com.hrgenius.organization.dto.ActiveStatusRequest;
import com.hrgenius.organization.dto.DepartmentRequest;
import com.hrgenius.organization.dto.DepartmentResponse;
import com.hrgenius.common.PageResponse;
import com.hrgenius.organization.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {
    private final DepartmentService departments;
    public DepartmentController(DepartmentService departments) { this.departments = departments; }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public PageResponse<DepartmentResponse> list(@RequestParam(required = false) Boolean active, @PageableDefault(size = 20, sort = "name") Pageable page) {
        return departments.list(active, page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public DepartmentResponse get(@PathVariable Long id) { return DepartmentResponse.from(departments.get(id)); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public DepartmentResponse create(@Valid @RequestBody DepartmentRequest request) {
        return DepartmentResponse.from(departments.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public DepartmentResponse update(@PathVariable Long id, @Valid @RequestBody DepartmentRequest request) {
        return DepartmentResponse.from(departments.update(id, request));
    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public DepartmentResponse setActive(@PathVariable Long id, @Valid @RequestBody ActiveStatusRequest request) {
        return DepartmentResponse.from(departments.setActive(id, request.active()));
    }
}
