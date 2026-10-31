package com.hrgenius.performance.dto;
import jakarta.validation.constraints.*;
public record PerformanceManagerReviewRequest(@NotBlank @Size(max=3000) String feedback, @NotNull @Min(1) @Max(5) Integer rating) { }
