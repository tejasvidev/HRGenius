package com.hrgenius.leave.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LeaveDecisionRequest(@NotNull Boolean approved, @Size(max = 1000) String comment) { }
