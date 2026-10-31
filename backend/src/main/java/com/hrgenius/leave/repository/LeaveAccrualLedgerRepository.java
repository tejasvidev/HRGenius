package com.hrgenius.leave.repository;
import com.hrgenius.leave.entity.LeaveAccrualLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface LeaveAccrualLedgerRepository extends JpaRepository<LeaveAccrualLedger,Long> {
    boolean existsByEmployee_IdAndLeaveType_IdAndYearAndMonth(Long employeeId,Long leaveTypeId,short year,byte month);
    List<LeaveAccrualLedger> findAllByEmployee_IdAndYearOrderByMonthAsc(Long employeeId,short year);
}
