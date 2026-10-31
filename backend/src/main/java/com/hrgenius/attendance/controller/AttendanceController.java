package com.hrgenius.attendance.controller;

import com.hrgenius.attendance.dto.*;
import com.hrgenius.attendance.entity.CorrectionStatus;
import com.hrgenius.attendance.service.AttendanceService;
import com.hrgenius.common.PageResponse;
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

import java.time.LocalDate;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceController {
    private final AttendanceService attendance;
    public AttendanceController(AttendanceService attendance){this.attendance=attendance;}

    @GetMapping("/policy") public AttendancePolicyResponse policy(){return attendance.policy();}
    @PutMapping("/policy") @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public AttendancePolicyResponse updatePolicy(@Valid @RequestBody AttendancePolicyRequest request){return attendance.updatePolicy(request);}

    @PostMapping("/check-in") @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','HR_EXECUTIVE','HR_MANAGER','ADMIN','SUPER_ADMIN')")
    public AttendanceResponse checkIn(@AuthenticationPrincipal Jwt jwt){return attendance.checkIn(jwt.getClaimAsString("email"));}

    @PostMapping("/check-out") @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','HR_EXECUTIVE','HR_MANAGER','ADMIN','SUPER_ADMIN')")
    public AttendanceResponse checkOut(@AuthenticationPrincipal Jwt jwt){return attendance.checkOut(jwt.getClaimAsString("email"));}

    @GetMapping("/me")
    public PageResponse<AttendanceResponse> mine(@AuthenticationPrincipal Jwt jwt,@RequestParam(required=false) LocalDate from,
            @RequestParam(required=false) LocalDate to,@PageableDefault(size=31,sort="workDate",direction=Sort.Direction.DESC) Pageable pageable){
        LocalDate end=to==null?attendance.today():to;return attendance.mine(jwt.getClaimAsString("email"),from==null?end.withDayOfMonth(1):from,end,pageable);
    }

    @GetMapping("/records") @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER','HR_EXECUTIVE')")
    public PageResponse<AttendanceResponse> records(@RequestParam LocalDate from,@RequestParam LocalDate to,@PageableDefault(size=50,sort="workDate",direction=Sort.Direction.DESC) Pageable pageable){return attendance.allRecords(from,to,pageable);}

    @GetMapping("/employees/{employeeId}") @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER','HR_EXECUTIVE')")
    public PageResponse<AttendanceResponse> employee(@PathVariable long employeeId,@RequestParam LocalDate from,@RequestParam LocalDate to,@PageableDefault(size=31,sort="workDate",direction=Sort.Direction.DESC) Pageable pageable){return attendance.employeeRecords(employeeId,from,to,pageable);}

    @PostMapping("/corrections") @ResponseStatus(HttpStatus.CREATED)
    public AttendanceCorrectionResponse requestCorrection(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody AttendanceCorrectionRequestBody body){return attendance.requestCorrection(jwt.getClaimAsString("email"),body);}

    @GetMapping("/corrections") @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER','HR_EXECUTIVE')")
    public PageResponse<AttendanceCorrectionResponse> corrections(@RequestParam(defaultValue="PENDING") CorrectionStatus status,@PageableDefault(size=50,sort="createdAt",direction=Sort.Direction.ASC) Pageable pageable){return attendance.correctionRequests(status,pageable);}

    @PatchMapping("/corrections/{id}/decision")
    public AttendanceCorrectionResponse decide(@PathVariable long id,@AuthenticationPrincipal Jwt jwt,Authentication authentication,@Valid @RequestBody AttendanceCorrectionDecision body){
        Set<String> roles=authentication.getAuthorities().stream().map(a->a.getAuthority()).collect(java.util.stream.Collectors.toSet());
        return attendance.decideCorrection(id,jwt.getClaimAsString("email"),roles,body);
    }
}
