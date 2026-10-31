package com.hrgenius.notification.repository;
import com.hrgenius.notification.entity.InAppNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.time.Instant;
public interface InAppNotificationRepository extends JpaRepository<InAppNotification,Long>{
    Page<InAppNotification> findAllByEmployee_WorkEmailIgnoreCaseOrderByCreatedAtDesc(String email,Pageable pageable);
    long countByEmployee_WorkEmailIgnoreCaseAndReadAtIsNull(String email);
    Optional<InAppNotification> findByIdAndEmployee_WorkEmailIgnoreCase(long id,String email);
    @Modifying @Query("update InAppNotification n set n.readAt=:now where lower(n.employee.workEmail)=lower(:email) and n.readAt is null")
    int markAllRead(@Param("email") String email,@Param("now") Instant now);
}
