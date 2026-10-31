package com.hrgenius.onboarding.entity;

import com.hrgenius.employee.entity.Employee;
import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="onboarding_documents")
public class OnboardingDocument {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="plan_id", nullable=false) private OnboardingPlan plan;
    @Enumerated(EnumType.STRING) @Column(name="document_type", nullable=false, length=32) private OnboardingDocumentType documentType;
    @Column(name="document_name", nullable=false, length=160) private String documentName;
    @Column(name="reference_url", nullable=false, length=512) private String referenceUrl;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=16) private OnboardingDocumentStatus status=OnboardingDocumentStatus.PENDING;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="reviewer_employee_id") private Employee reviewer;
    @Column(name="review_comment", length=1000) private String reviewComment;
    @Column(name="submitted_at", nullable=false, updatable=false) private Instant submittedAt;
    @Column(name="reviewed_at") private Instant reviewedAt;
    protected OnboardingDocument() { }
    public OnboardingDocument(OnboardingPlan plan, OnboardingDocumentType type, String name, String referenceUrl) {
        this.plan=plan;this.documentType=type;this.documentName=name.trim();this.referenceUrl=referenceUrl;
    }
    public void review(Employee reviewer, boolean verified, String comment) {
        this.reviewer=reviewer; this.status=verified?OnboardingDocumentStatus.VERIFIED:OnboardingDocumentStatus.REJECTED;
        this.reviewComment=comment==null||comment.isBlank()?null:comment.trim(); this.reviewedAt=Instant.now();
    }
    @PrePersist void onCreate(){submittedAt=Instant.now();}
    public Long getId(){return id;} public OnboardingPlan getPlan(){return plan;} public OnboardingDocumentType getDocumentType(){return documentType;}
    public String getDocumentName(){return documentName;} public String getReferenceUrl(){return referenceUrl;}
    public OnboardingDocumentStatus getStatus(){return status;} public Employee getReviewer(){return reviewer;}
    public String getReviewComment(){return reviewComment;} public Instant getSubmittedAt(){return submittedAt;} public Instant getReviewedAt(){return reviewedAt;}
}
