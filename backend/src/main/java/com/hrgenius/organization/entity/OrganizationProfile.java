package com.hrgenius.organization.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "organization_profile")
public class OrganizationProfile {
    @Id
    private Long id = 1L;
    @Column(nullable = false, length = 160) private String name;
    @Column(name = "legal_name", length = 200) private String legalName;
    @Column(name = "work_email", length = 254) private String workEmail;
    @Column(length = 40) private String phone;
    @Column(length = 255) private String website;
    @Column(name = "logo_url", length = 512) private String logoUrl;
    @Column(name = "registration_number", length = 100) private String registrationNumber;
    @Column(name = "address_line_1", length = 200) private String addressLine1;
    @Column(name = "address_line_2", length = 200) private String addressLine2;
    @Column(length = 100) private String city;
    @Column(length = 100) private String state;
    @Column(length = 100) private String country;
    @Column(name = "postal_code", length = 24) private String postalCode;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;

    protected OrganizationProfile() { }

    public OrganizationProfile(String name, String legalName, String workEmail, String phone, String website,
                               String logoUrl, String registrationNumber, String addressLine1, String addressLine2,
                               String city, String state, String country, String postalCode) {
        this.id = 1L;
        update(name, legalName, workEmail, phone, website, logoUrl, registrationNumber,
                addressLine1, addressLine2, city, state, country, postalCode);
    }

    public void update(String name, String legalName, String workEmail, String phone, String website,
                       String logoUrl, String registrationNumber, String addressLine1, String addressLine2,
                       String city, String state, String country, String postalCode) {
        this.name = name.trim();
        this.legalName = trimToNull(legalName);
        this.workEmail = trimToNull(workEmail);
        this.phone = trimToNull(phone);
        this.website = trimToNull(website);
        this.logoUrl = trimToNull(logoUrl);
        this.registrationNumber = trimToNull(registrationNumber);
        this.addressLine1 = trimToNull(addressLine1);
        this.addressLine2 = trimToNull(addressLine2);
        this.city = trimToNull(city);
        this.state = trimToNull(state);
        this.country = trimToNull(country);
        this.postalCode = trimToNull(postalCode);
    }

    private static String trimToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    @PrePersist void onCreate() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getLegalName() { return legalName; }
    public String getWorkEmail() { return workEmail; }
    public String getPhone() { return phone; }
    public String getWebsite() { return website; }
    public String getLogoUrl() { return logoUrl; }
    public String getRegistrationNumber() { return registrationNumber; }
    public String getAddressLine1() { return addressLine1; }
    public String getAddressLine2() { return addressLine2; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getCountry() { return country; }
    public String getPostalCode() { return postalCode; }
}
