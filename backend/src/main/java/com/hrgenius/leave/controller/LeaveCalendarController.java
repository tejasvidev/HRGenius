package com.hrgenius.leave.controller;

import com.hrgenius.leave.dto.*;
import com.hrgenius.leave.service.LeaveManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.Year;
import java.util.List;

@RestController @RequestMapping("/api/v1/leave/calendar")
public class LeaveCalendarController {
    private final LeaveManagementService leave;
    public LeaveCalendarController(LeaveManagementService leave){this.leave=leave;}
    @GetMapping("/policy") public LeaveCalendarPolicyResponse policy(){return leave.calendarPolicy();}
    @PutMapping("/policy") @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public LeaveCalendarPolicyResponse policy(@Valid @RequestBody LeaveCalendarPolicyRequest request){return leave.updateCalendarPolicy(request);}
    @GetMapping("/holidays") public List<LeaveHolidayResponse> holidays(@RequestParam(defaultValue="0") short year,@RequestParam(defaultValue="false") boolean includeInactive){return leave.listHolidays(year==0?(short)Year.now().getValue():year,includeInactive);}
    @PostMapping("/holidays") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public LeaveHolidayResponse createHoliday(@Valid @RequestBody LeaveHolidayRequest request){return leave.createHoliday(request);}
    @PatchMapping("/holidays/{id}/active") @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public LeaveHolidayResponse active(@PathVariable long id,@RequestParam boolean active){return leave.setHolidayActive(id,active);}
}
