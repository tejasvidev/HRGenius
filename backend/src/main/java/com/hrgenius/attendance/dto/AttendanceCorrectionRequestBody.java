package com.hrgenius.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

public record AttendanceCorrectionRequestBody(@NotNull LocalDate workDate,@NotNull LocalTime checkIn,@NotNull LocalTime checkOut,
        @NotBlank @Size(max=1000) String reason) { }
