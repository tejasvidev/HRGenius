package com.hrgenius.announcement.dto;
import com.hrgenius.announcement.entity.AnnouncementPriority;
import jakarta.validation.constraints.*;
import java.time.Instant;
public record AnnouncementRequest(@NotBlank @Size(max=180) String title,@NotBlank @Size(max=5000) String body,
        Long departmentId,@NotNull AnnouncementPriority priority,Instant expiresAt) { }
