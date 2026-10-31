package com.hrgenius.notification.dto;
import com.hrgenius.notification.entity.InAppNotification;
import java.time.Instant;
public record InAppNotificationResponse(long id,String title,String body,String type,Instant createdAt,Instant readAt){public static InAppNotificationResponse from(InAppNotification n){return new InAppNotificationResponse(n.getId(),n.getTitle(),n.getBody(),n.getType(),n.getCreatedAt(),n.getReadAt());}}
