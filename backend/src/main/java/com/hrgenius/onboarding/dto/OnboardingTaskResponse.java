package com.hrgenius.onboarding.dto;
import com.hrgenius.onboarding.entity.OnboardingTask;
import com.hrgenius.onboarding.entity.OnboardingAssigneeRole;
import com.hrgenius.onboarding.entity.OnboardingTaskStatus;
import java.time.Instant;
import java.time.LocalDate;
public record OnboardingTaskResponse(Long id, Long planId, String title, String description,
        OnboardingAssigneeRole assigneeRole, Long assigneeId, String assigneeName, LocalDate dueDate,
        boolean required, OnboardingTaskStatus status, String notes, Instant completedAt) {
    public static OnboardingTaskResponse from(OnboardingTask t) { return new OnboardingTaskResponse(t.getId(),t.getPlan().getId(),t.getTitle(),t.getDescription(),t.getAssigneeRole(),t.getAssignee()==null?null:t.getAssignee().getId(),t.getAssignee()==null?null:t.getAssignee().getFirstName()+" "+t.getAssignee().getLastName(),t.getDueDate(),t.isRequired(),t.getStatus(),t.getNotes(),t.getCompletedAt()); }
}
