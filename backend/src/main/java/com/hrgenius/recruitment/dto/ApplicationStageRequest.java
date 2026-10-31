package com.hrgenius.recruitment.dto;
import com.hrgenius.recruitment.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public record ApplicationStageRequest(@NotNull ApplicationStatus status, @Size(max=1000) String note) { }
