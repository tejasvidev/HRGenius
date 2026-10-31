package com.hrgenius.attendance.repository;

import com.hrgenius.attendance.entity.AttendanceCorrectionRequest;
import com.hrgenius.attendance.entity.CorrectionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceCorrectionRepository extends JpaRepository<AttendanceCorrectionRequest,Long> {
    boolean existsByEmployee_IdAndWorkDate(Long employeeId,LocalDate day);
    Page<AttendanceCorrectionRequest> findAllByStatusOrderByCreatedAtAsc(CorrectionStatus status,Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from AttendanceCorrectionRequest c where c.id=:id")
    Optional<AttendanceCorrectionRequest> lockById(@Param("id") Long id);
}
