package com.hrgenius.organization.service;

import com.hrgenius.common.ResourceNotFoundException;
import com.hrgenius.organization.dto.OrganizationProfileRequest;
import com.hrgenius.organization.entity.OrganizationProfile;
import com.hrgenius.organization.repository.OrganizationProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizationProfileService {
    private static final long ORGANIZATION_ID = 1L;
    private final OrganizationProfileRepository profiles;

    public OrganizationProfileService(OrganizationProfileRepository profiles) { this.profiles = profiles; }

    @Transactional(readOnly = true)
    public OrganizationProfile get() {
        return profiles.findById(ORGANIZATION_ID)
                .orElseThrow(() -> new ResourceNotFoundException("Organization profile", ORGANIZATION_ID));
    }

    @Transactional
    public OrganizationProfile save(OrganizationProfileRequest request) {
        OrganizationProfile profile = profiles.findById(ORGANIZATION_ID).orElseGet(() -> new OrganizationProfile(
                request.name(), request.legalName(), request.workEmail(), request.phone(), request.website(),
                request.logoUrl(), request.registrationNumber(), request.addressLine1(), request.addressLine2(),
                request.city(), request.state(), request.country(), request.postalCode()));
        if (profiles.existsById(ORGANIZATION_ID)) {
            profile.update(request.name(), request.legalName(), request.workEmail(), request.phone(), request.website(),
                    request.logoUrl(), request.registrationNumber(), request.addressLine1(), request.addressLine2(),
                    request.city(), request.state(), request.country(), request.postalCode());
        }
        return profiles.save(profile);
    }
}
