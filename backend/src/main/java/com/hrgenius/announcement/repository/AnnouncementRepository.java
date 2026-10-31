package com.hrgenius.announcement.repository;
import com.hrgenius.announcement.entity.Announcement;
import com.hrgenius.announcement.entity.AnnouncementStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
public interface AnnouncementRepository extends JpaRepository<Announcement,Long> {
    @Query("select a from Announcement a where a.status=:status and (a.department is null or a.department.id=:departmentId) and a.publishedAt<=:now and (a.expiresAt is null or a.expiresAt>:now)")
    Page<Announcement> findVisible(@Param("status") AnnouncementStatus status,@Param("departmentId") Long departmentId,@Param("now") Instant now,Pageable pageable);
}
