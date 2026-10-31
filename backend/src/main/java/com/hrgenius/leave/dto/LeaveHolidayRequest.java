package com.hrgenius.leave.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
public record LeaveHolidayRequest(@NotNull LocalDate date,@NotBlank @Size(max=120) String name,@Size(max=500) String description) { }
