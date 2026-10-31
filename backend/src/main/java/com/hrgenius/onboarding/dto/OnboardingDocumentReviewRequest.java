package com.hrgenius.onboarding.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public record OnboardingDocumentReviewRequest(@NotNull Boolean verified, @Size(max=1000) String comment) { }
