package com.hrgenius.onboarding.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
public record OnboardingTemplateRequest(@NotBlank @Size(max=120) String name, @Size(max=500) String description,
        @NotEmpty @Size(max=50) List<@Valid TemplateTaskInput> tasks) { }
