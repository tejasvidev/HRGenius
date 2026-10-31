package com.hrgenius.recruitment.controller;

import com.hrgenius.common.PageResponse;
import com.hrgenius.recruitment.dto.*;
import com.hrgenius.recruitment.entity.JobOpeningStatus;
import com.hrgenius.recruitment.service.RecruitmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/recruitment/openings")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER','HR_EXECUTIVE','RECRUITER')")
public class JobOpeningController {
    private final RecruitmentService recruitment;
    public JobOpeningController(RecruitmentService recruitment) { this.recruitment = recruitment; }
    @GetMapping public PageResponse<JobOpeningResponse> list(@RequestParam(required=false) JobOpeningStatus status,
            @PageableDefault(size=20, sort="createdAt", direction=Sort.Direction.DESC) Pageable pageable) { return recruitment.listOpenings(status, pageable); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public JobOpeningResponse create(@Valid @RequestBody JobOpeningRequest request) { return recruitment.createOpening(request); }
    @PutMapping("/{id}") public JobOpeningResponse update(@PathVariable Long id, @Valid @RequestBody JobOpeningRequest request) { return recruitment.updateOpening(id, request); }
    @PatchMapping("/{id}/status") public JobOpeningResponse status(@PathVariable Long id, @Valid @RequestBody OpeningStatusRequest request) { return recruitment.setOpeningStatus(id, request.status()); }
}
