package com.hrgenius.employee.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record EmployeeSelfUpdateRequest(@Email @Size(max = 254) String personalEmail,
        @Size(max = 40) String phone, @Size(max = 200) String addressLine1,
        @Size(max = 200) String addressLine2, @Size(max = 100) String city,
        @Size(max = 100) String state, @Size(max = 100) String country,
        @Size(max = 24) String postalCode) { }
