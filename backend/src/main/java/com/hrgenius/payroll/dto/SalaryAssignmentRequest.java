package com.hrgenius.payroll.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

public record SalaryAssignmentRequest(@NotNull @Positive Long employeeId,@NotNull @Pattern(regexp="[A-Z]{3}") String currency,
        @NotNull @DecimalMin("0.01") @Digits(integer=11,fraction=2) BigDecimal monthlyGross,
        @NotNull @DecimalMin("0.00") @Digits(integer=11,fraction=2) BigDecimal fixedDeduction,
        @NotNull LocalDate effectiveFrom) { }
