package com.hrgenius.leave.repository;

import com.hrgenius.leave.entity.LeaveRequest;
import com.hrgenius.leave.entity.LeaveRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Collection;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    Page<LeaveRequest> findAllByEmployee_Id(Long employeeId, Pageable pageable);
    Page<LeaveRequest> findAllByEmployee_Manager_Id(Long managerId, Pageable pageable);
    boolean existsByEmployee_IdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long employeeId, Collection<LeaveRequestStatus> statuses, LocalDate endDate, LocalDate startDate);
}
