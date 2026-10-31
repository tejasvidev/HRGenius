package com.hrgenius.recruitment.repository;
import com.hrgenius.recruitment.entity.JobApplication;
import com.hrgenius.recruitment.entity.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    boolean existsByOpening_IdAndCandidate_Id(Long openingId, Long candidateId);
    Page<JobApplication> findAllByOpening_Id(Long openingId, Pageable pageable);
    Page<JobApplication> findAllByStatus(ApplicationStatus status, Pageable pageable);
    long countByOpening_IdAndStatusIn(Long openingId, java.util.Collection<ApplicationStatus> statuses);
}
