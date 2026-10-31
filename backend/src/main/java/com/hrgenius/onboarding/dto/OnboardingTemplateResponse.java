package com.hrgenius.onboarding.dto;
import com.hrgenius.onboarding.entity.OnboardingTemplate;
import java.util.List;
public record OnboardingTemplateResponse(Long id, String name, String description, boolean active,
        List<OnboardingTemplateTaskResponse> tasks) {
    public static OnboardingTemplateResponse from(OnboardingTemplate t) { return new OnboardingTemplateResponse(t.getId(),t.getName(),t.getDescription(),t.isActive(),t.getTasks().stream().map(OnboardingTemplateTaskResponse::from).toList()); }
}
