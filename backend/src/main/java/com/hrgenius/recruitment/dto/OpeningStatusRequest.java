package com.hrgenius.recruitment.dto;
import com.hrgenius.recruitment.entity.JobOpeningStatus;
import jakarta.validation.constraints.NotNull;
public record OpeningStatusRequest(@NotNull JobOpeningStatus status) { }
