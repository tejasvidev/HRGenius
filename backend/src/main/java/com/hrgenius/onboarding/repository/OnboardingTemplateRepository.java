package com.hrgenius.onboarding.repository;
import com.hrgenius.onboarding.entity.OnboardingTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface OnboardingTemplateRepository extends JpaRepository<OnboardingTemplate,Long> {
    boolean existsByNameIgnoreCase(String name);
    List<OnboardingTemplate> findAllByActiveTrueOrderByNameAsc();
    List<OnboardingTemplate> findAllByOrderByNameAsc();
}
