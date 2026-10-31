package com.hrgenius.performance.dto;
import jakarta.validation.constraints.*;
public record PerformanceSelfReviewRequest(@NotBlank @Size(max=3000) String summary, @NotNull @Min(1) @Max(5) Integer rating) { }
