package com.hrgenius.recruitment.dto;
import com.hrgenius.recruitment.entity.Interview;
import com.hrgenius.recruitment.entity.InterviewStatus;
import java.time.Instant;
public record InterviewResponse(Long id, Long applicationId, String candidateName, Long interviewerId,
        String interviewerName, Instant scheduledAt, short durationMinutes, String interviewType,
        InterviewStatus status, String feedback, Byte rating) {
    public static InterviewResponse from(Interview i) { return new InterviewResponse(i.getId(), i.getApplication().getId(),
            i.getApplication().getCandidate().getFirstName()+" "+i.getApplication().getCandidate().getLastName(),
            i.getInterviewer().getId(), i.getInterviewer().getFirstName()+" "+i.getInterviewer().getLastName(),
            i.getScheduledAt(), i.getDurationMinutes(), i.getInterviewType(), i.getStatus(), i.getFeedback(), i.getRating()); }
}
