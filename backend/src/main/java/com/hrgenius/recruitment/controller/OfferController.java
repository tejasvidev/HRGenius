package com.hrgenius.recruitment.controller;

import com.hrgenius.employee.dto.EmployeeResponse;
import com.hrgenius.recruitment.dto.*;
import com.hrgenius.recruitment.service.RecruitmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/v1/recruitment")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER','HR_EXECUTIVE','RECRUITER')")
public class OfferController {
    private final RecruitmentService recruitment;
    public OfferController(RecruitmentService recruitment) { this.recruitment = recruitment; }
    @GetMapping("/applications/{applicationId}/offers")
    public List<OfferResponse> list(@PathVariable Long applicationId) { return recruitment.listOffers(applicationId); }
    @PostMapping("/offers") @ResponseStatus(HttpStatus.CREATED)
    public OfferResponse create(@Valid @RequestBody OfferRequest request) { return recruitment.createOffer(request); }
    @PostMapping("/offers/{id}/send") public OfferResponse send(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return recruitment.sendOffer(id, jwt.getClaimAsString("email"));
    }
    @PostMapping("/offers/{id}/decision") public OfferResponse decide(@PathVariable Long id,
            @Valid @RequestBody OfferDecisionRequest request, @AuthenticationPrincipal Jwt jwt) {
        return recruitment.decideOffer(id, request.accepted(), jwt.getClaimAsString("email"));
    }
    @PostMapping("/applications/{applicationId}/convert")
    public EmployeeResponse convert(@PathVariable Long applicationId, @Valid @RequestBody CandidateConversionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return recruitment.convertCandidate(applicationId, request, jwt.getClaimAsString("email"));
    }
}
