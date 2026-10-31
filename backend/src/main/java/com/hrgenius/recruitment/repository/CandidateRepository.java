package com.hrgenius.recruitment.repository;
import com.hrgenius.recruitment.entity.Candidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    boolean existsByEmailIgnoreCase(String email);
    Page<Candidate> findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String first, String last, String email, Pageable pageable);
}
