package com.hrgenius.onboarding.repository;
import com.hrgenius.onboarding.entity.OnboardingTask;
import com.hrgenius.onboarding.entity.OnboardingTaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface OnboardingTaskRepository extends JpaRepository<OnboardingTask,Long> {
    List<OnboardingTask> findAllByPlan_IdOrderByDueDateAscIdAsc(Long planId);
    boolean existsByPlan_IdAndRequiredTrueAndStatusNot(Long planId, OnboardingTaskStatus status);
}
