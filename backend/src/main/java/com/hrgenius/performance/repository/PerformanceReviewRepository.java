package com.hrgenius.performance.repository;
import com.hrgenius.performance.entity.PerformanceReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    Page<PerformanceReview> findAllByEmployee_WorkEmailIgnoreCase(String email, Pageable pageable);
    Page<PerformanceReview> findAllByReviewer_WorkEmailIgnoreCase(String email, Pageable pageable);
    Optional<PerformanceReview> findByIdAndEmployee_WorkEmailIgnoreCase(long id, String email);
    Optional<PerformanceReview> findByIdAndReviewer_WorkEmailIgnoreCase(long id, String email);
    boolean existsByCycleNameIgnoreCaseAndEmployee_Id(String cycleName, long employeeId);
}
