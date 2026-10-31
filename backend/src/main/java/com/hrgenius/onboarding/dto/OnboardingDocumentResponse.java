package com.hrgenius.onboarding.dto;
import com.hrgenius.onboarding.entity.OnboardingDocument;
import com.hrgenius.onboarding.entity.OnboardingDocumentStatus;
import com.hrgenius.onboarding.entity.OnboardingDocumentType;
import java.time.Instant;
public record OnboardingDocumentResponse(Long id, Long planId, OnboardingDocumentType documentType,
        String documentName, String referenceUrl, OnboardingDocumentStatus status, String reviewerName,
        String reviewComment, Instant submittedAt, Instant reviewedAt) {
    public static OnboardingDocumentResponse from(OnboardingDocument d) { return new OnboardingDocumentResponse(d.getId(),d.getPlan().getId(),d.getDocumentType(),d.getDocumentName(),d.getReferenceUrl(),d.getStatus(),d.getReviewer()==null?null:d.getReviewer().getFirstName()+" "+d.getReviewer().getLastName(),d.getReviewComment(),d.getSubmittedAt(),d.getReviewedAt()); }
}
