package com.hrgenius.announcement.dto;
import com.hrgenius.announcement.entity.*;
import java.time.Instant;
public record AnnouncementResponse(long id,String title,String body,Long departmentId,String departmentName,
        AnnouncementPriority priority,AnnouncementStatus status,Instant publishedAt,Instant expiresAt,String createdByEmail,Instant createdAt) {
    public static AnnouncementResponse from(Announcement a){return new AnnouncementResponse(a.getId(),a.getTitle(),a.getBody(),a.getDepartment()==null?null:a.getDepartment().getId(),a.getDepartment()==null?"All employees":a.getDepartment().getName(),a.getPriority(),a.getStatus(),a.getPublishedAt(),a.getExpiresAt(),a.getCreatedByEmail(),a.getCreatedAt());}
}
