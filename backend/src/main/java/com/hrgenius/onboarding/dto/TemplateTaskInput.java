package com.hrgenius.onboarding.dto;
import com.hrgenius.onboarding.entity.OnboardingAssigneeRole;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public record TemplateTaskInput(@NotBlank @Size(max=160) String title, @Size(max=1000) String description,
        @NotNull OnboardingAssigneeRole assigneeRole, @Min(0) @Max(365) short daysFromStart, boolean required) { }
