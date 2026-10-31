package com.hrgenius.organization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LocationRequest(
        @NotBlank @Pattern(regexp = "[A-Za-z0-9_-]{2,24}") String code,
        @NotBlank @Size(max = 120) String name,
        @Size(max = 200) String addressLine1,
        @Size(max = 200) String addressLine2,
        @Size(max = 100) String city,
        @Size(max = 100) String state,
        @NotBlank @Size(max = 100) String country,
        @Size(max = 24) String postalCode) { }
