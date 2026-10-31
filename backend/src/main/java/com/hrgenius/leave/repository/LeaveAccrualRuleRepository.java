package com.hrgenius.leave.repository;
import com.hrgenius.leave.entity.LeaveAccrualRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface LeaveAccrualRuleRepository extends JpaRepository<LeaveAccrualRule,Long> {
    Optional<LeaveAccrualRule> findByLeaveType_Id(Long typeId);
    List<LeaveAccrualRule> findAllByOrderByLeaveType_NameAsc();
    List<LeaveAccrualRule> findAllByActiveTrueOrderByLeaveType_NameAsc();
}
