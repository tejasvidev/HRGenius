package com.hrgenius.onboarding.repository;
import com.hrgenius.onboarding.entity.OnboardingDocument;
import com.hrgenius.onboarding.entity.OnboardingDocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface OnboardingDocumentRepository extends JpaRepository<OnboardingDocument,Long> {
    List<OnboardingDocument> findAllByPlan_IdOrderBySubmittedAtDesc(Long planId);
    boolean existsByPlan_IdAndStatus(Long planId, OnboardingDocumentStatus status);
}
