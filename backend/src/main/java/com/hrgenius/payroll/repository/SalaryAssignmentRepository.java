package com.hrgenius.payroll.repository;

import com.hrgenius.payroll.entity.SalaryAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface SalaryAssignmentRepository extends JpaRepository<SalaryAssignment,Long> {
    Optional<SalaryAssignment> findByEmployee_Id(Long employeeId);
    Page<SalaryAssignment> findAllByOrderByEmployee_LastNameAsc(Pageable pageable);
    Page<SalaryAssignment> findAllByActiveTrueAndEffectiveFromLessThanEqualOrderByIdAsc(LocalDate date,Pageable pageable);
}
