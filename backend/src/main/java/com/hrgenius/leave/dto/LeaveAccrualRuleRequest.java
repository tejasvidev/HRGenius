package com.hrgenius.leave.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
public record LeaveAccrualRuleRequest(@NotNull @Positive Long leaveTypeId,
        @NotNull @DecimalMin("0.01") @Digits(integer=3,fraction=2) BigDecimal monthlyDays,
        @DecimalMin("0.00") @Digits(integer=4,fraction=2) BigDecimal annualCap,
        @NotNull LocalDate effectiveFrom) { }
