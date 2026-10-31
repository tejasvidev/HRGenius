package com.hrgenius.organization.service;

import com.hrgenius.common.ConflictException;
import com.hrgenius.common.PageRequests;
import com.hrgenius.common.PageResponse;
import com.hrgenius.common.ResourceNotFoundException;
import com.hrgenius.organization.dto.LocationRequest;
import com.hrgenius.organization.dto.LocationResponse;
import com.hrgenius.organization.entity.Location;
import com.hrgenius.organization.repository.LocationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class LocationService {
    private final LocationRepository locations;
    public LocationService(LocationRepository locations) { this.locations = locations; }

    @Transactional(readOnly = true)
    public PageResponse<LocationResponse> list(Boolean active, Pageable pageable) {
        Pageable bounded = PageRequests.capped(pageable);
        Page<Location> page = active == null ? locations.findAll(bounded)
                : active ? locations.findAllByActiveTrue(bounded) : locations.findAllByActiveFalse(bounded);
        return PageResponse.from(page, LocationResponse::from);
    }

    @Transactional(readOnly = true)
    public Location get(Long id) {
        return locations.findById(id).orElseThrow(() -> new ResourceNotFoundException("Location", id));
    }

    @Transactional
    public Location create(LocationRequest request) {
        ensureUnique(request.code(), request.name(), null);
        return locations.save(new Location(request.code(), request.name(), request.addressLine1(), request.addressLine2(),
                request.city(), request.state(), request.country(), request.postalCode()));
    }

    @Transactional
    public Location update(Long id, LocationRequest request) {
        Location location = get(id);
        ensureUnique(request.code(), request.name(), id);
        location.update(request.code(), request.name(), request.addressLine1(), request.addressLine2(),
                request.city(), request.state(), request.country(), request.postalCode());
        return locations.save(location);
    }

    @Transactional
    public Location setActive(Long id, boolean active) {
        Location location = get(id);
        if (active) location.activate(); else location.deactivate();
        return locations.save(location);
    }

    private void ensureUnique(String rawCode, String rawName, Long currentId) {
        String code = rawCode.trim().toUpperCase(Locale.ROOT);
        boolean codeTaken = currentId == null ? locations.existsByCodeIgnoreCase(code)
                : locations.existsByCodeIgnoreCaseAndIdNot(code, currentId);
        boolean nameTaken = currentId == null ? locations.existsByNameIgnoreCase(rawName.trim())
                : locations.existsByNameIgnoreCaseAndIdNot(rawName.trim(), currentId);
        if (codeTaken) throw new ConflictException("Location code already exists");
        if (nameTaken) throw new ConflictException("Location name already exists");
    }
}
