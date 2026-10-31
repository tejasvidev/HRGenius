package com.hrgenius.recruitment.controller;

import com.hrgenius.common.PageResponse;
import com.hrgenius.recruitment.dto.*;
import com.hrgenius.recruitment.entity.ApplicationStatus;
import com.hrgenius.recruitment.service.RecruitmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/recruitment/applications")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER','HR_EXECUTIVE','RECRUITER')")
public class JobApplicationController {
    private final RecruitmentService recruitment;
    public JobApplicationController(RecruitmentService recruitment) { this.recruitment = recruitment; }
    @GetMapping public PageResponse<ApplicationResponse> list(@RequestParam(required=false) Long openingId,
            @RequestParam(required=false) ApplicationStatus status, @PageableDefault(size=20, sort="appliedAt", direction=Sort.Direction.DESC) Pageable pageable) {
        return recruitment.listApplications(openingId, status, pageable);
    }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ApplicationResponse apply(@Valid @RequestBody ApplicationCreateRequest request,
            @AuthenticationPrincipal Jwt jwt) { return recruitment.apply(request, jwt.getClaimAsString("email")); }
    @PatchMapping("/{id}/stage") public ApplicationResponse stage(@PathVariable Long id,
            @Valid @RequestBody ApplicationStageRequest request, @AuthenticationPrincipal Jwt jwt) {
        return recruitment.changeStage(id, request, jwt.getClaimAsString("email"));
    }
}
