package com.hrgenius.performance.dto;
import jakarta.validation.constraints.*;
public record PerformanceReviewCreateRequest(@NotBlank @Size(max=120) String cycleName, @NotNull @Positive long employeeId) { }
