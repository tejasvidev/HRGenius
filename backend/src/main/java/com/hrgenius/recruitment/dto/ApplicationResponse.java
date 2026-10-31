package com.hrgenius.recruitment.dto;
import com.hrgenius.recruitment.entity.JobApplication;
import com.hrgenius.recruitment.entity.ApplicationStatus;
import java.time.Instant;
public record ApplicationResponse(Long id, Long openingId, String openingTitle, Long candidateId, String candidateName,
        String candidateEmail, ApplicationStatus status, Instant appliedAt, Long employeeId) {
    public static ApplicationResponse from(JobApplication a) { return new ApplicationResponse(a.getId(), a.getOpening().getId(),
            a.getOpening().getTitle(), a.getCandidate().getId(), a.getCandidate().getFirstName()+" "+a.getCandidate().getLastName(),
            a.getCandidate().getEmail(), a.getStatus(), a.getAppliedAt(), a.getEmployee()==null?null:a.getEmployee().getId()); }
}
