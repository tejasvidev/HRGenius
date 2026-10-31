package com.hrgenius.payroll.repository;

import com.hrgenius.payroll.entity.PayrollItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PayrollItemRepository extends JpaRepository<PayrollItem,Long> {
    Page<PayrollItem> findAllByRun_IdOrderByEmployee_LastNameAsc(Long runId,Pageable pageable);
    @Query("select i from PayrollItem i join fetch i.run where i.employee.id=:employeeId order by i.run.year desc,i.run.month desc")
    List<PayrollItem> payslipsFor(@Param("employeeId") Long employeeId);
}
