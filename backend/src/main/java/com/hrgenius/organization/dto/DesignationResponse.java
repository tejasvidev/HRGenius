package com.hrgenius.organization.dto;

import com.hrgenius.organization.entity.Designation;

public record DesignationResponse(Long id, String code, String title, Long departmentId, String departmentName,
                                  String jobGrade, String description, boolean active) {
    public static DesignationResponse from(Designation designation) {
        return new DesignationResponse(designation.getId(), designation.getCode(), designation.getTitle(),
                designation.getDepartment() == null ? null : designation.getDepartment().getId(),
                designation.getDepartment() == null ? null : designation.getDepartment().getName(),
                designation.getJobGrade(), designation.getDescription(), designation.isActive());
    }
}
