package com.hrgenius.organization.dto;

import com.hrgenius.organization.entity.Location;

public record LocationResponse(Long id, String code, String name, String addressLine1, String addressLine2,
                               String city, String state, String country, String postalCode, boolean active) {
    public static LocationResponse from(Location location) {
        return new LocationResponse(location.getId(), location.getCode(), location.getName(), location.getAddressLine1(),
                location.getAddressLine2(), location.getCity(), location.getState(), location.getCountry(),
                location.getPostalCode(), location.isActive());
    }
}
