package com.hrgenius.recruitment.controller;

import com.hrgenius.common.PageResponse;
import com.hrgenius.recruitment.dto.*;
import com.hrgenius.recruitment.service.RecruitmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.Set;
import java.util.stream.Collectors;

@RestController @RequestMapping("/api/v1/recruitment/interviews")
public class InterviewController {
    private final RecruitmentService recruitment;
    public InterviewController(RecruitmentService recruitment) { this.recruitment = recruitment; }
    @GetMapping @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER','HR_EXECUTIVE','RECRUITER','MANAGER')")
    public PageResponse<InterviewResponse> list(@AuthenticationPrincipal Jwt jwt, Authentication auth,
            @PageableDefault(size=20, sort="scheduledAt", direction=Sort.Direction.ASC) Pageable pageable) {
        return recruitment.listInterviews(jwt.getClaimAsString("email"), authorities(auth), pageable);
    }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER','HR_EXECUTIVE','RECRUITER')")
    public InterviewResponse schedule(@Valid @RequestBody InterviewRequest request, @AuthenticationPrincipal Jwt jwt) {
        return recruitment.scheduleInterview(request, jwt.getClaimAsString("email"));
    }
    @PutMapping("/{id}/feedback") @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER','HR_EXECUTIVE','RECRUITER','MANAGER')")
    public InterviewResponse feedback(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt, Authentication auth,
            @Valid @RequestBody InterviewFeedbackRequest request) {
        return recruitment.recordFeedback(id, jwt.getClaimAsString("email"), authorities(auth), request);
    }
    private Set<String> authorities(Authentication auth) { return auth.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toSet()); }
}
