package com.hrgenius.announcement.controller;
import com.hrgenius.announcement.dto.*;
import com.hrgenius.announcement.service.AnnouncementService;
import com.hrgenius.common.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/announcements") public class AnnouncementController {
    private final AnnouncementService service; public AnnouncementController(AnnouncementService s){service=s;}
    @GetMapping("/me") public PageResponse<AnnouncementResponse> visible(@AuthenticationPrincipal Jwt jwt,@PageableDefault(size=20,sort="publishedAt",direction=Sort.Direction.DESC) Pageable p){return service.visible(jwt.getClaimAsString("email"),p);}
    @GetMapping @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')") public PageResponse<AnnouncementResponse> all(@PageableDefault(size=50,sort="createdAt",direction=Sort.Direction.DESC) Pageable p){return service.all(p);}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')") public AnnouncementResponse create(@Valid @RequestBody AnnouncementRequest r,@AuthenticationPrincipal Jwt jwt){return service.create(r,jwt.getClaimAsString("email"));}
    @PutMapping("/{id}") @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')") public AnnouncementResponse update(@PathVariable long id,@Valid @RequestBody AnnouncementRequest r){return service.update(id,r);}
    @PostMapping("/{id}/publish") @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')") public AnnouncementResponse publish(@PathVariable long id){return service.publish(id);}
}
