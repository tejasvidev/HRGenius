package com.hrgenius.recruitment.repository;
import com.hrgenius.recruitment.entity.JobOpening;
import com.hrgenius.recruitment.entity.JobOpeningStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface JobOpeningRepository extends JpaRepository<JobOpening, Long> {
    Page<JobOpening> findAllByStatus(JobOpeningStatus status, Pageable pageable);
}
