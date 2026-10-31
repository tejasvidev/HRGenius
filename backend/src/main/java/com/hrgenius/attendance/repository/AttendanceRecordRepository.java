package com.hrgenius.attendance.repository;

import com.hrgenius.attendance.entity.AttendanceRecord;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord,Long> {
    Optional<AttendanceRecord> findByEmployee_IdAndWorkDate(Long employeeId,LocalDate date);
    Page<AttendanceRecord> findAllByEmployee_IdAndWorkDateBetweenOrderByWorkDateDesc(Long employeeId,LocalDate from,LocalDate to,Pageable pageable);
    Page<AttendanceRecord> findAllByWorkDateBetweenOrderByWorkDateDesc(LocalDate from,LocalDate to,Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AttendanceRecord a where a.employee.id=:employeeId and a.workDate=:day")
    Optional<AttendanceRecord> lockByEmployeeAndDate(@Param("employeeId") Long employeeId,@Param("day") LocalDate day);
}
