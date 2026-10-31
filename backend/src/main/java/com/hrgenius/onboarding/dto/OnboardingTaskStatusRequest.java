package com.hrgenius.onboarding.dto;
import com.hrgenius.onboarding.entity.OnboardingTaskStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public record OnboardingTaskStatusRequest(@NotNull OnboardingTaskStatus status, @Size(max=1000) String notes) { }
