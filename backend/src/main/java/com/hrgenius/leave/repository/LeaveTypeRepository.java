package com.hrgenius.leave.repository;

import com.hrgenius.leave.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {
    boolean existsByCodeIgnoreCase(String code);
    boolean existsByNameIgnoreCase(String name);
    List<LeaveType> findAllByActiveTrueOrderByNameAsc();
    List<LeaveType> findAllByOrderByNameAsc();
}
