package com.hrgenius.onboarding.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
public record OnboardingPlanRequest(@NotNull @Positive Long employeeId, @NotNull @Positive Long templateId,
        @NotNull LocalDate startDate) { }
