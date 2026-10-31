package com.hrgenius.leave.repository;

import com.hrgenius.leave.entity.LeaveBalance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    Optional<LeaveBalance> findByEmployee_IdAndLeaveType_IdAndCalendarYear(Long employeeId, Long leaveTypeId, short year);
    List<LeaveBalance> findAllByEmployee_IdAndCalendarYearOrderByLeaveType_Name(Long employeeId, short year);
    List<LeaveBalance> findAllByCalendarYearOrderByEmployee_LastName(short year);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from LeaveBalance b where b.employee.id = :employeeId and b.leaveType.id = :typeId and b.calendarYear = :year")
    Optional<LeaveBalance> lockForUpdate(@Param("employeeId") Long employeeId, @Param("typeId") Long typeId, @Param("year") short year);
}
