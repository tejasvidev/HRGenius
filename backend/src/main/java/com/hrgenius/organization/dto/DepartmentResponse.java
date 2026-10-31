package com.hrgenius.organization.dto;

import com.hrgenius.organization.entity.Department;

public record DepartmentResponse(Long id, String code, String name, String description, boolean active) {
    public static DepartmentResponse from(Department department) {
        return new DepartmentResponse(department.getId(), department.getCode(), department.getName(),
                department.getDescription(), department.isActive());
    }
}
