package com.hrgenius.notification.controller;
import com.hrgenius.common.PageResponse;
import com.hrgenius.notification.dto.InAppNotificationResponse;
import com.hrgenius.notification.service.InAppNotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/v1/notifications") public class InAppNotificationController {
    private final InAppNotificationService notifications; public InAppNotificationController(InAppNotificationService n){notifications=n;}
    @GetMapping("/me") public PageResponse<InAppNotificationResponse> mine(@AuthenticationPrincipal Jwt jwt,@PageableDefault(size=50,sort="createdAt",direction=Sort.Direction.DESC) Pageable p){return notifications.mine(jwt.getClaimAsString("email"),p);}
    @GetMapping("/me/unread-count") public Map<String,Long> unreadCount(@AuthenticationPrincipal Jwt jwt){return Map.of("count",notifications.unreadCount(jwt.getClaimAsString("email")));}
    @PatchMapping("/me/{id}/read") @ResponseStatus(HttpStatus.NO_CONTENT) public void markRead(@PathVariable long id,@AuthenticationPrincipal Jwt jwt){notifications.markRead(jwt.getClaimAsString("email"),id);}
    @PostMapping("/me/read-all") public Map<String,Integer> markAllRead(@AuthenticationPrincipal Jwt jwt){return Map.of("updated",notifications.markAllRead(jwt.getClaimAsString("email")));}
}
