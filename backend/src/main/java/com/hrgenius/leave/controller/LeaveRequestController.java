package com.hrgenius.leave.controller;

import com.hrgenius.common.PageResponse;
import com.hrgenius.leave.dto.LeaveDecisionRequest;
import com.hrgenius.leave.dto.LeaveRequestCreate;
import com.hrgenius.leave.dto.LeaveRequestResponse;
import com.hrgenius.leave.service.LeaveManagementService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/leave/requests")
public class LeaveRequestController {
    private final LeaveManagementService leave;
    public LeaveRequestController(LeaveManagementService leave) { this.leave = leave; }

    @GetMapping
    public PageResponse<LeaveRequestResponse> list(@AuthenticationPrincipal Jwt jwt, Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return leave.listRequests(jwt.getClaimAsString("email"), authorities(authentication), pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveRequestResponse submit(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody LeaveRequestCreate request) {
        return leave.submit(jwt.getClaimAsString("email"), request);
    }

    @PostMapping("/{id}/decision")
    public LeaveRequestResponse decide(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt,
            Authentication authentication, @Valid @RequestBody LeaveDecisionRequest request) {
        return leave.decide(id, jwt.getClaimAsString("email"), authorities(authentication), request);
    }

    @PostMapping("/{id}/cancel")
    public LeaveRequestResponse cancel(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return leave.cancel(id, jwt.getClaimAsString("email"));
    }

    private Set<String> authorities(Authentication authentication) {
        return authentication.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toSet());
    }
}
