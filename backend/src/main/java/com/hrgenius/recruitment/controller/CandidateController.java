package com.hrgenius.recruitment.controller;

import com.hrgenius.common.PageResponse;
import com.hrgenius.recruitment.dto.CandidateRequest;
import com.hrgenius.recruitment.dto.CandidateResponse;
import com.hrgenius.recruitment.service.RecruitmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/recruitment/candidates")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER','HR_EXECUTIVE','RECRUITER')")
public class CandidateController {
    private final RecruitmentService recruitment;
    public CandidateController(RecruitmentService recruitment) { this.recruitment = recruitment; }
    @GetMapping public PageResponse<CandidateResponse> list(@RequestParam(required=false) String q,
            @PageableDefault(size=20, sort="lastName") Pageable pageable) { return recruitment.listCandidates(q, pageable); }
    @GetMapping("/{id}") public CandidateResponse get(@PathVariable Long id) { return recruitment.candidate(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public CandidateResponse create(@Valid @RequestBody CandidateRequest request) { return recruitment.createCandidate(request); }
}
