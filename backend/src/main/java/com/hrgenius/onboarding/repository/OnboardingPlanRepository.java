package com.hrgenius.onboarding.repository;
import com.hrgenius.onboarding.entity.OnboardingPlan;
import com.hrgenius.onboarding.entity.OnboardingPlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;
public interface OnboardingPlanRepository extends JpaRepository<OnboardingPlan,Long> {
    boolean existsByEmployee_IdAndStatusIn(Long employeeId, Collection<OnboardingPlanStatus> statuses);
    List<OnboardingPlan> findAllByEmployee_IdOrderByCreatedAtDesc(Long employeeId);
    List<OnboardingPlan> findAllByOrderByCreatedAtDesc();
}
