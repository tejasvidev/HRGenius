package com.hrgenius.recruitment.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
public record CandidateRequest(@NotBlank @Size(max=100) String firstName, @NotBlank @Size(max=100) String lastName,
        @NotBlank @Email @Size(max=254) String email, @Size(max=40) String phone,
        @URL(protocol="https") @Size(max=512) String resumeUrl, @Size(max=80) String source) { }
