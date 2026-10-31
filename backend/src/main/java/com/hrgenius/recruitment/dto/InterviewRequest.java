package com.hrgenius.recruitment.dto;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.time.OffsetDateTime;
public record InterviewRequest(@NotNull @Positive Long applicationId, @NotNull @Positive Long interviewerId,
        @NotNull @Future OffsetDateTime scheduledAt, @Min(15) @Max(480) short durationMinutes,
        @NotBlank @Pattern(regexp="PHONE|VIDEO|ONSITE") String interviewType) { }
