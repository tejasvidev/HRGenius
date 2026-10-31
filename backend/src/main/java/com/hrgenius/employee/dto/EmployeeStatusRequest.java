package com.hrgenius.employee.dto;

import com.hrgenius.employee.entity.EmployeeStatus;
import jakarta.validation.constraints.NotNull;

public record EmployeeStatusRequest(@NotNull EmployeeStatus status) { }
