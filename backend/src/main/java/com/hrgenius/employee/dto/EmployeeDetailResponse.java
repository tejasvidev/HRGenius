package com.hrgenius.employee.dto;

import com.hrgenius.employee.entity.Employee;
import com.hrgenius.employee.entity.EmployeeStatus;
import com.hrgenius.employee.entity.EmploymentType;
import java.time.LocalDate;

public record EmployeeDetailResponse(Long id, String employeeNumber, String firstName, String lastName,
        String workEmail, String personalEmail, String phone, LocalDate dateOfBirth, String gender,
        String addressLine1, String addressLine2, String city, String state, String country, String postalCode,
        EmployeeStatus status, EmploymentType employmentType, LocalDate dateOfJoining,
        String emergencyContactName, String emergencyContactPhone, String skills,
        Long departmentId, String departmentName, Long designationId, String designationTitle,
        Long managerId, String managerName, Long locationId, String locationName) {
    public static EmployeeDetailResponse from(Employee e) {
        return new EmployeeDetailResponse(e.getId(), e.getEmployeeNumber(), e.getFirstName(), e.getLastName(), e.getWorkEmail(),
                e.getPersonalEmail(), e.getPhone(), e.getDateOfBirth(), e.getGender(), e.getAddressLine1(), e.getAddressLine2(),
                e.getCity(), e.getState(), e.getCountry(), e.getPostalCode(), e.getStatus(), e.getEmploymentType(), e.getDateOfJoining(),
                e.getEmergencyContactName(), e.getEmergencyContactPhone(), e.getSkills(),
                e.getDepartment()==null?null:e.getDepartment().getId(), e.getDepartment()==null?null:e.getDepartment().getName(),
                e.getDesignation()==null?null:e.getDesignation().getId(), e.getDesignation()==null?null:e.getDesignation().getTitle(),
                e.getManager()==null?null:e.getManager().getId(), e.getManager()==null?null:e.getManager().getFirstName()+" "+e.getManager().getLastName(),
                e.getLocation()==null?null:e.getLocation().getId(), e.getLocation()==null?null:e.getLocation().getName());
    }
}
