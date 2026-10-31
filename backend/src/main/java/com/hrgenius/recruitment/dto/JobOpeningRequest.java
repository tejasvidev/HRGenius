package com.hrgenius.recruitment.dto;
import com.hrgenius.employee.entity.EmploymentType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
public record JobOpeningRequest(@NotBlank @Size(max=160) String title, @Positive Long departmentId,
        @Positive Long designationId, @Positive Long locationId, @Positive int vacancies,
        @NotBlank String description, @Size(max=1000) String requiredSkills,
        @DecimalMin("0.0") @Digits(integer=3,fraction=1) BigDecimal minExperienceYears,
        @DecimalMin("0.0") @Digits(integer=3,fraction=1) BigDecimal maxExperienceYears,
        @NotNull EmploymentType employmentType, @DecimalMin("0.0") @Digits(integer=11,fraction=2) BigDecimal salaryMin,
        @DecimalMin("0.0") @Digits(integer=11,fraction=2) BigDecimal salaryMax,
        @Positive Long hiringManagerId, @Positive Long recruiterId, @NotNull @FutureOrPresent LocalDate openingDate,
        LocalDate closingDate) { }
