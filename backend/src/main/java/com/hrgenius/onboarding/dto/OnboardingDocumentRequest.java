package com.hrgenius.onboarding.dto;
import com.hrgenius.onboarding.entity.OnboardingDocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
public record OnboardingDocumentRequest(@NotNull OnboardingDocumentType documentType,
        @NotBlank @Size(max=160) String documentName, @NotBlank @URL(protocol="https") @Size(max=512) String referenceUrl) { }
