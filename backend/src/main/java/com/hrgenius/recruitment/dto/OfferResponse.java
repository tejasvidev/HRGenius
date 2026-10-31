package com.hrgenius.recruitment.dto;
import com.hrgenius.recruitment.entity.Offer;
import com.hrgenius.recruitment.entity.OfferStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
public record OfferResponse(Long id, Long applicationId, BigDecimal salary, String currency,
        LocalDate startDate, LocalDate expiresOn, OfferStatus status, Instant sentAt) {
    public static OfferResponse from(Offer o) { return new OfferResponse(o.getId(), o.getApplication().getId(), o.getSalary(), o.getCurrency(), o.getStartDate(), o.getExpiresOn(), o.getStatus(), o.getSentAt()); }
}
