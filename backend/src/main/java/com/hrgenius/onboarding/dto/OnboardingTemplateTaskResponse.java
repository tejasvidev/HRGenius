package com.hrgenius.onboarding.dto;
import com.hrgenius.onboarding.entity.OnboardingTemplateTask;
import com.hrgenius.onboarding.entity.OnboardingAssigneeRole;
public record OnboardingTemplateTaskResponse(Long id, String title, String description, OnboardingAssigneeRole assigneeRole,
        short daysFromStart, boolean required, short displayOrder) {
    public static OnboardingTemplateTaskResponse from(OnboardingTemplateTask t) { return new OnboardingTemplateTaskResponse(t.getId(),t.getTitle(),t.getDescription(),t.getAssigneeRole(),t.getDaysFromStart(),t.isRequired(),t.getDisplayOrder()); }
}
