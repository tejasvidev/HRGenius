package com.hrgenius.organization.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.Locale;

@Entity
@Table(name = "locations", uniqueConstraints = {
        @UniqueConstraint(name = "uk_locations_code", columnNames = "code"),
        @UniqueConstraint(name = "uk_locations_name", columnNames = "name")
})
public class Location {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 24) private String code;
    @Column(nullable = false, length = 120) private String name;
    @Column(name = "address_line_1", length = 200) private String addressLine1;
    @Column(name = "address_line_2", length = 200) private String addressLine2;
    @Column(length = 100) private String city;
    @Column(length = 100) private String state;
    @Column(nullable = false, length = 100) private String country;
    @Column(name = "postal_code", length = 24) private String postalCode;
    @Column(nullable = false) private boolean active = true;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;

    protected Location() { }
    public Location(String code, String name, String addressLine1, String addressLine2, String city,
                    String state, String country, String postalCode) {
        update(code, name, addressLine1, addressLine2, city, state, country, postalCode);
    }
    public void update(String code, String name, String addressLine1, String addressLine2, String city,
                       String state, String country, String postalCode) {
        this.code = code.trim().toUpperCase(Locale.ROOT);
        this.name = name.trim();
        this.addressLine1 = trimToNull(addressLine1);
        this.addressLine2 = trimToNull(addressLine2);
        this.city = trimToNull(city);
        this.state = trimToNull(state);
        this.country = country.trim();
        this.postalCode = trimToNull(postalCode);
    }
    public void deactivate() { active = false; }
    public void activate() { active = true; }
    private static String trimToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    @PrePersist void onCreate() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getAddressLine1() { return addressLine1; }
    public String getAddressLine2() { return addressLine2; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getCountry() { return country; }
    public String getPostalCode() { return postalCode; }
    public boolean isActive() { return active; }
}
