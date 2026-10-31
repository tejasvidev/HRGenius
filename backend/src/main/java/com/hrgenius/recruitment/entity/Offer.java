package com.hrgenius.recruitment.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name = "offers", uniqueConstraints = @UniqueConstraint(name = "uk_offer_application", columnNames = "application_id"))
public class Offer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "application_id", nullable = false, unique = true) private JobApplication application;
    @Column(nullable = false, precision = 13, scale = 2) private BigDecimal salary;
    @Column(nullable = false, length = 3, columnDefinition = "char(3)") private String currency;
    @Column(name = "start_date", nullable = false) private LocalDate startDate;
    @Column(name = "expires_on", nullable = false) private LocalDate expiresOn;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private OfferStatus status = OfferStatus.DRAFT;
    @Column(name = "sent_at") private Instant sentAt;
    @Column(name = "decided_at") private Instant decidedAt;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    protected Offer() { }
    public Offer(JobApplication application, BigDecimal salary, String currency, LocalDate startDate, LocalDate expiresOn) {
        this.application = application; this.salary = salary; this.currency = currency.toUpperCase(); this.startDate = startDate; this.expiresOn = expiresOn;
    }
    public void send() { if (status != OfferStatus.DRAFT) throw new IllegalStateException("Only draft offers can be sent"); status = OfferStatus.SENT; sentAt = Instant.now(); }
    public void decide(boolean accept) { if (status != OfferStatus.SENT) throw new IllegalStateException("Only sent offers can be decided"); status = accept ? OfferStatus.ACCEPTED : OfferStatus.DECLINED; decidedAt = Instant.now(); }
    @PrePersist void onCreate() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
    public Long getId() { return id; } public JobApplication getApplication() { return application; }
    public BigDecimal getSalary() { return salary; } public String getCurrency() { return currency; }
    public LocalDate getStartDate() { return startDate; } public LocalDate getExpiresOn() { return expiresOn; }
    public OfferStatus getStatus() { return status; } public Instant getSentAt() { return sentAt; }
}
