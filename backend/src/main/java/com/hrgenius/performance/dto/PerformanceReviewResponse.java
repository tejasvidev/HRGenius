package com.hrgenius.performance.dto;
import com.hrgenius.performance.entity.PerformanceReview;
import com.hrgenius.performance.entity.PerformanceReviewStatus;
import java.time.Instant;
public record PerformanceReviewResponse(long id, String cycleName, long employeeId, String employeeName, long reviewerId, String reviewerName,
        PerformanceReviewStatus status, String selfSummary, Integer selfRating, String managerFeedback, Integer managerRating, Instant createdAt) {
    public static PerformanceReviewResponse from(PerformanceReview r){return new PerformanceReviewResponse(r.getId(),r.getCycleName(),r.getEmployee().getId(),r.getEmployee().getFirstName()+" "+r.getEmployee().getLastName(),r.getReviewer().getId(),r.getReviewer().getFirstName()+" "+r.getReviewer().getLastName(),r.getStatus(),r.getSelfSummary(),r.getSelfRating(),r.getManagerFeedback(),r.getManagerRating(),r.getCreatedAt());}
}
