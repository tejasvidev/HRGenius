package com.hrgenius.organization.dto;

import com.hrgenius.organization.entity.OrganizationProfile;

public record OrganizationProfileResponse(Long id, String name, String legalName, String workEmail, String phone,
                                          String website, String logoUrl, String registrationNumber,
                                          String addressLine1, String addressLine2, String city, String state,
                                          String country, String postalCode) {
    public static OrganizationProfileResponse from(OrganizationProfile profile) {
        return new OrganizationProfileResponse(profile.getId(), profile.getName(), profile.getLegalName(),
                profile.getWorkEmail(), profile.getPhone(), profile.getWebsite(), profile.getLogoUrl(),
                profile.getRegistrationNumber(), profile.getAddressLine1(), profile.getAddressLine2(),
                profile.getCity(), profile.getState(), profile.getCountry(), profile.getPostalCode());
    }
}
