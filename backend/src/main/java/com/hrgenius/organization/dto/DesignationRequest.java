package com.hrgenius.organization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record DesignationRequest(
        @NotBlank @Pattern(regexp = "[A-Za-z0-9_-]{2,24}") String code,
        @NotBlank @Size(max = 120) String title,
        @Positive Long departmentId,
        @Size(max = 40) String jobGrade,
        @Size(max = 500) String description) { }
