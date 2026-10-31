package com.hrgenius.recruitment.dto;
import com.hrgenius.employee.entity.EmploymentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
public record CandidateConversionRequest(@NotBlank @Pattern(regexp="[A-Za-z0-9_-]{2,32}") String employeeNumber,
        @NotBlank @Email @Size(max=254) String workEmail, @NotNull EmploymentType employmentType,
        @NotNull @FutureOrPresent LocalDate dateOfJoining, @NotNull @jakarta.validation.constraints.Positive Long onboardingTemplateId) { }
