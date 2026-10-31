package com.hrgenius.leave.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public record LeaveBalanceRequest(@NotNull @Positive Long employeeId, @NotNull @Positive Long leaveTypeId,
        @Min(2000) @Max(2200) short year, @NotNull @PositiveOrZero @Digits(integer = 4, fraction = 2) BigDecimal entitledDays) { }
