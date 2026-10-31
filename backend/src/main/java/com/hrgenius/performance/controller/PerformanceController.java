package com.hrgenius.performance.controller;
import com.hrgenius.common.PageResponse;
import com.hrgenius.performance.dto.*;
import com.hrgenius.performance.service.PerformanceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/performance")
public class PerformanceController {
    private final PerformanceService performance;
    public PerformanceController(PerformanceService performance){this.performance=performance;}
    @GetMapping("/me") public PageResponse<PerformanceReviewResponse> mine(@AuthenticationPrincipal Jwt jwt,@PageableDefault(size=20,sort="createdAt",direction=Sort.Direction.DESC) Pageable pageable){return performance.mine(jwt.getClaimAsString("email"),pageable);}
    @GetMapping("/team") @PreAuthorize("hasAnyRole('MANAGER','SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public PageResponse<PerformanceReviewResponse> team(@AuthenticationPrincipal Jwt jwt,@PageableDefault(size=50,sort="createdAt",direction=Sort.Direction.DESC) Pageable pageable){return performance.team(jwt.getClaimAsString("email"),pageable);}
    @GetMapping @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public PageResponse<PerformanceReviewResponse> all(@PageableDefault(size=50,sort="createdAt",direction=Sort.Direction.DESC) Pageable pageable){return performance.all(pageable);}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public PerformanceReviewResponse create(@Valid @RequestBody PerformanceReviewCreateRequest body){return performance.create(body);}
    @PatchMapping("/{id}/self") public PerformanceReviewResponse self(@PathVariable long id,@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody PerformanceSelfReviewRequest body){return performance.self(jwt.getClaimAsString("email"),id,body);}
    @PatchMapping("/{id}/manager") @PreAuthorize("hasAnyRole('MANAGER','SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public PerformanceReviewResponse manager(@PathVariable long id,@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody PerformanceManagerReviewRequest body){return performance.manager(jwt.getClaimAsString("email"),id,body);}
}
