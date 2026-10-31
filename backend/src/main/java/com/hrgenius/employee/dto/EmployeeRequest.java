package com.hrgenius.employee.dto;

import com.hrgenius.employee.entity.EmploymentType;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record EmployeeRequest(
        @NotBlank @Pattern(regexp = "[A-Za-z0-9_-]{2,32}") String employeeNumber,
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotBlank @Email @Size(max = 254) String workEmail,
        @Email @Size(max = 254) String personalEmail,
        @Size(max = 40) String phone,
        @Past LocalDate dateOfBirth,
        @Size(max = 24) String gender,
        @Size(max = 200) String addressLine1,
        @Size(max = 200) String addressLine2,
        @Size(max = 100) String city,
        @Size(max = 100) String state,
        @Size(max = 100) String country,
        @Size(max = 24) String postalCode,
        @NotNull EmploymentType employmentType,
        @NotNull LocalDate dateOfJoining,
        @Size(max = 160) String emergencyContactName,
        @Size(max = 40) String emergencyContactPhone,
        @Size(max = 1000) String skills,
        @Positive Long departmentId,
        @Positive Long designationId,
        @Positive Long managerId,
        @Positive Long locationId) { }
