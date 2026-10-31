package com.hrgenius.recruitment.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
public record OfferRequest(@NotNull @Positive Long applicationId, @NotNull @DecimalMin("0.01") @Digits(integer=11,fraction=2) BigDecimal salary,
        @NotBlank @Pattern(regexp="[A-Z]{3}") String currency, @NotNull @FutureOrPresent LocalDate startDate,
        @NotNull @FutureOrPresent LocalDate expiresOn) { }
