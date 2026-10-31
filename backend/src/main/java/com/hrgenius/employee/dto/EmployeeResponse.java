package com.hrgenius.employee.dto;

import com.hrgenius.employee.entity.Employee;
import com.hrgenius.employee.entity.EmployeeStatus;
import com.hrgenius.employee.entity.EmploymentType;
import java.time.LocalDate;

public record EmployeeResponse(Long id, String employeeNumber, String firstName, String lastName, String workEmail,
        String phone, EmployeeStatus status,
        EmploymentType employmentType, LocalDate dateOfJoining, String skills, Long departmentId, String departmentName,
        Long designationId, String designationTitle, Long managerId, String managerName, Long locationId,
        String locationName) {
    public static EmployeeResponse from(Employee e) {
        return new EmployeeResponse(e.getId(), e.getEmployeeNumber(), e.getFirstName(), e.getLastName(), e.getWorkEmail(),
                e.getPhone(), e.getStatus(), e.getEmploymentType(), e.getDateOfJoining(), e.getSkills(),
                e.getDepartment() == null ? null : e.getDepartment().getId(),
                e.getDepartment() == null ? null : e.getDepartment().getName(),
                e.getDesignation() == null ? null : e.getDesignation().getId(),
                e.getDesignation() == null ? null : e.getDesignation().getTitle(),
                e.getManager() == null ? null : e.getManager().getId(),
                e.getManager() == null ? null : e.getManager().getFirstName() + " " + e.getManager().getLastName(),
                e.getLocation() == null ? null : e.getLocation().getId(),
                e.getLocation() == null ? null : e.getLocation().getName());
    }
}
