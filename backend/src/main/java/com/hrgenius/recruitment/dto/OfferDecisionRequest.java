package com.hrgenius.recruitment.dto;
import jakarta.validation.constraints.NotNull;
public record OfferDecisionRequest(@NotNull Boolean accepted) { }
