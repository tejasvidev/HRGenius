package com.hrgenius.organization.controller;

import com.hrgenius.organization.dto.ActiveStatusRequest;
import com.hrgenius.organization.dto.LocationRequest;
import com.hrgenius.organization.dto.LocationResponse;
import com.hrgenius.common.PageResponse;
import com.hrgenius.organization.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {
    private final LocationService locations;
    public LocationController(LocationService locations) { this.locations = locations; }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public PageResponse<LocationResponse> list(@RequestParam(required = false) Boolean active, @PageableDefault(size = 20, sort = "name") Pageable page) {
        return locations.list(active, page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','HR_MANAGER')")
    public LocationResponse get(@PathVariable Long id) { return LocationResponse.from(locations.get(id)); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public LocationResponse create(@Valid @RequestBody LocationRequest request) {
        return LocationResponse.from(locations.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public LocationResponse update(@PathVariable Long id, @Valid @RequestBody LocationRequest request) {
        return LocationResponse.from(locations.update(id, request));
    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public LocationResponse setActive(@PathVariable Long id, @Valid @RequestBody ActiveStatusRequest request) {
        return LocationResponse.from(locations.setActive(id, request.active()));
    }
}
