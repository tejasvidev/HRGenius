package com.hrgenius.organization.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrganizationProfileRequest(
        @NotBlank @Size(max = 160) String name,
        @Size(max = 200) String legalName,
        @Email @Size(max = 254) String workEmail,
        @Size(max = 40) String phone,
        @Size(max = 255) String website,
        @Size(max = 512) String logoUrl,
        @Size(max = 100) String registrationNumber,
        @Size(max = 200) String addressLine1,
        @Size(max = 200) String addressLine2,
        @Size(max = 100) String city,
        @Size(max = 100) String state,
        @Size(max = 100) String country,
        @Size(max = 24) String postalCode) { }
