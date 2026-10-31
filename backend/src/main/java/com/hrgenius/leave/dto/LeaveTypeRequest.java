package com.hrgenius.leave.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LeaveTypeRequest(@NotBlank @Pattern(regexp = "[A-Za-z0-9_-]{2,24}") String code,
        @NotBlank @Size(max = 100) String name, @Size(max = 500) String description) { }
