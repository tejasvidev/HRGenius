package com.hrgenius.organization.controller;

import com.hrgenius.organization.dto.ActiveStatusRequest;
import com.hrgenius.organization.dto.DesignationRequest;
import com.hrgenius.organization.dto.DesignationResponse;
import com.hrgenius.common.PageResponse;
import com.hrgenius.organization.service.DesignationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/designations")
public class DesignationController {
    private final DesignationService designations;
    public DesignationController(DesignationService designations) { this.designations = designations; }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public PageResponse<DesignationResponse> list(@RequestParam(required = false) Boolean active, @RequestParam(required = false) Long departmentId,
                       @PageableDefault(size = 20, sort = "title") Pageable page) {
        return designations.list(active, departmentId, page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public DesignationResponse get(@PathVariable Long id) { return DesignationResponse.from(designations.get(id)); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public DesignationResponse create(@Valid @RequestBody DesignationRequest request) {
        return DesignationResponse.from(designations.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public DesignationResponse update(@PathVariable Long id, @Valid @RequestBody DesignationRequest request) {
        return DesignationResponse.from(designations.update(id, request));
    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public DesignationResponse setActive(@PathVariable Long id, @Valid @RequestBody ActiveStatusRequest request) {
        return DesignationResponse.from(designations.setActive(id, request.active()));
    }
}
