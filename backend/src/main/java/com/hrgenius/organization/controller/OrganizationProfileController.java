package com.hrgenius.organization.controller;

import com.hrgenius.organization.dto.OrganizationProfileRequest;
import com.hrgenius.organization.dto.OrganizationProfileResponse;
import com.hrgenius.organization.service.OrganizationProfileService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/organization")
public class OrganizationProfileController {
    private final OrganizationProfileService profiles;
    public OrganizationProfileController(OrganizationProfileService profiles) { this.profiles = profiles; }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public OrganizationProfileResponse get() { return OrganizationProfileResponse.from(profiles.get()); }

    @PutMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public OrganizationProfileResponse save(@Valid @RequestBody OrganizationProfileRequest request) {
        return OrganizationProfileResponse.from(profiles.save(request));
    }
}
