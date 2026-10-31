package com.hrgenius.recruitment.repository;
import com.hrgenius.recruitment.entity.Interview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    Page<Interview> findAllByApplication_Id(Long applicationId, Pageable pageable);
    Page<Interview> findAllByInterviewer_Id(Long interviewerId, Pageable pageable);
    boolean existsByApplication_IdAndStatus(Long applicationId, com.hrgenius.recruitment.entity.InterviewStatus status);
}
