package com.hrgenius.onboarding.dto;
import com.hrgenius.onboarding.entity.OnboardingPlan;
import com.hrgenius.onboarding.entity.OnboardingPlanStatus;
import java.time.LocalDate;
public record OnboardingPlanResponse(Long id, Long employeeId, String employeeName, Long templateId,
        String templateName, LocalDate startDate, OnboardingPlanStatus status) {
    public static OnboardingPlanResponse from(OnboardingPlan p) { return new OnboardingPlanResponse(p.getId(),p.getEmployee().getId(),p.getEmployee().getFirstName()+" "+p.getEmployee().getLastName(),p.getTemplate().getId(),p.getTemplate().getName(),p.getStartDate(),p.getStatus()); }
}
