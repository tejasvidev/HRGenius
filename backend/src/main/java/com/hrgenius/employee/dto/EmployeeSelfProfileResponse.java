package com.hrgenius.employee.dto;

import com.hrgenius.employee.entity.Employee;

public record EmployeeSelfProfileResponse(Long id, String employeeNumber, String firstName, String lastName,
        String workEmail, String personalEmail, String phone, String addressLine1, String addressLine2,
        String city, String state, String country, String postalCode) {
    public static EmployeeSelfProfileResponse from(Employee e) {
        return new EmployeeSelfProfileResponse(e.getId(), e.getEmployeeNumber(), e.getFirstName(), e.getLastName(),
                e.getWorkEmail(), e.getPersonalEmail(), e.getPhone(), e.getAddressLine1(), e.getAddressLine2(),
                e.getCity(), e.getState(), e.getCountry(), e.getPostalCode());
    }
}
