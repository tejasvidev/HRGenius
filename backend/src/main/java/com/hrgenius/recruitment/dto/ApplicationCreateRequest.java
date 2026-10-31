package com.hrgenius.recruitment.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
public record ApplicationCreateRequest(@NotNull @Positive Long openingId, @NotNull @Positive Long candidateId) { }
