package com.hrgenius.leave.repository;
import com.hrgenius.leave.entity.LeaveHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
public interface LeaveHolidayRepository extends JpaRepository<LeaveHoliday,Long> {
    boolean existsByDate(LocalDate date);
    List<LeaveHoliday> findAllByActiveTrueAndDateBetweenOrderByDateAsc(LocalDate start,LocalDate end);
    List<LeaveHoliday> findAllByDateBetweenOrderByDateAsc(LocalDate start,LocalDate end);
}
