package com.hrgenius.payroll.repository;

import com.hrgenius.payroll.entity.PayrollRun;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface PayrollRunRepository extends JpaRepository<PayrollRun,Long> {
    boolean existsByYearAndMonth(short year,byte month);
    Page<PayrollRun> findAllByOrderByYearDescMonthDesc(Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from PayrollRun r where r.id=:id")
    Optional<PayrollRun> lockById(@Param("id") Long id);
}
