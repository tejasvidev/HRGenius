package com.hrgenius.leave.dto;

import com.hrgenius.leave.entity.LeaveType;

public record LeaveTypeResponse(Long id, String code, String name, String description, boolean active) {
    public static LeaveTypeResponse from(LeaveType type) {
        return new LeaveTypeResponse(type.getId(), type.getCode(), type.getName(), type.getDescription(), type.isActive());
    }
}
