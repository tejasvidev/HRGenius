package com.hrgenius.employee.repository;

import com.hrgenius.employee.entity.EmployeeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmployeeHistoryRepository extends JpaRepository<EmployeeHistory, Long> {
    List<EmployeeHistory> findAllByEmployee_IdOrderByEffectiveDateDesc(Long employeeId);
}
