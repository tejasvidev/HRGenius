package com.hrgenius.recruitment.dto;
import com.hrgenius.recruitment.entity.Candidate;
public record CandidateResponse(Long id, String firstName, String lastName, String email, String phone,
        String resumeUrl, String source) {
    public static CandidateResponse from(Candidate c) { return new CandidateResponse(c.getId(), c.getFirstName(), c.getLastName(), c.getEmail(), c.getPhone(), c.getResumeUrl(), c.getSource()); }
}
