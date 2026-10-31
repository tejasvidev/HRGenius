package com.hrgenius.employee.entity;

import com.hrgenius.organization.entity.Department;
import com.hrgenius.organization.entity.Designation;
import com.hrgenius.organization.entity.Location;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Locale;

@Entity
@Table(name = "employees", uniqueConstraints = {
        @UniqueConstraint(name = "uk_employees_number", columnNames = "employee_number"),
        @UniqueConstraint(name = "uk_employees_work_email", columnNames = "work_email")
})
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "employee_number", nullable = false, length = 32) private String employeeNumber;
    @Column(name = "first_name", nullable = false, length = 100) private String firstName;
    @Column(name = "last_name", nullable = false, length = 100) private String lastName;
    @Column(name = "work_email", nullable = false, length = 254) private String workEmail;
    @Column(name = "personal_email", length = 254) private String personalEmail;
    @Column(length = 40) private String phone;
    @Column(name = "date_of_birth") private LocalDate dateOfBirth;
    @Column(length = 24) private String gender;
    @Column(name = "address_line_1", length = 200) private String addressLine1;
    @Column(name = "address_line_2", length = 200) private String addressLine2;
    @Column(length = 100) private String city;
    @Column(length = 100) private String state;
    @Column(length = 100) private String country;
    @Column(name = "postal_code", length = 24) private String postalCode;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 24) private EmployeeStatus status = EmployeeStatus.ACTIVE;
    @Enumerated(EnumType.STRING) @Column(name = "employment_type", nullable = false, length = 24) private EmploymentType employmentType;
    @Column(name = "date_of_joining", nullable = false) private LocalDate dateOfJoining;
    @Column(name = "emergency_contact_name", length = 160) private String emergencyContactName;
    @Column(name = "emergency_contact_phone", length = 40) private String emergencyContactPhone;
    @Column(length = 1000) private String skills;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "department_id") private Department department;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "designation_id") private Designation designation;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "manager_id") private Employee manager;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "location_id") private Location location;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;

    protected Employee() { }
    public Employee(String employeeNumber, String firstName, String lastName, String workEmail, String personalEmail,
                    String phone, LocalDate dateOfBirth, String gender, String addressLine1, String addressLine2,
                    String city, String state, String country, String postalCode, EmploymentType employmentType,
                    LocalDate dateOfJoining, String emergencyContactName, String emergencyContactPhone, String skills,
                    Department department, Designation designation, Employee manager, Location location) {
        this.employeeNumber = employeeNumber.trim().toUpperCase(Locale.ROOT);
        update(firstName, lastName, workEmail, personalEmail, phone, dateOfBirth, gender, addressLine1, addressLine2,
                city, state, country, postalCode, employmentType, dateOfJoining, emergencyContactName,
                emergencyContactPhone, skills, department, designation, manager, location);
    }
    public void update(String firstName, String lastName, String workEmail, String personalEmail, String phone,
                       LocalDate dateOfBirth, String gender, String addressLine1, String addressLine2, String city,
                       String state, String country, String postalCode, EmploymentType employmentType,
                       LocalDate dateOfJoining, String emergencyContactName, String emergencyContactPhone, String skills,
                       Department department, Designation designation, Employee manager, Location location) {
        this.firstName = firstName.trim(); this.lastName = lastName.trim();
        this.workEmail = workEmail.trim().toLowerCase(Locale.ROOT);
        this.personalEmail = clean(personalEmail); this.phone = clean(phone); this.dateOfBirth = dateOfBirth;
        this.gender = clean(gender); this.addressLine1 = clean(addressLine1); this.addressLine2 = clean(addressLine2);
        this.city = clean(city); this.state = clean(state); this.country = clean(country); this.postalCode = clean(postalCode);
        this.employmentType = employmentType; this.dateOfJoining = dateOfJoining;
        this.emergencyContactName = clean(emergencyContactName); this.emergencyContactPhone = clean(emergencyContactPhone);
        this.skills = clean(skills); this.department = department; this.designation = designation;
        this.manager = manager; this.location = location;
    }
    public void setStatus(EmployeeStatus status) { this.status = status; }
    public void updatePersonalDetails(String personalEmail, String phone, String addressLine1, String addressLine2,
                                     String city, String state, String country, String postalCode) {
        this.personalEmail = clean(personalEmail); this.phone = clean(phone);
        this.addressLine1 = clean(addressLine1); this.addressLine2 = clean(addressLine2);
        this.city = clean(city); this.state = clean(state); this.country = clean(country); this.postalCode = clean(postalCode);
    }
    private static String clean(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    @PrePersist void onCreate() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
    public Long getId() { return id; } public String getEmployeeNumber() { return employeeNumber; }
    public String getFirstName() { return firstName; } public String getLastName() { return lastName; }
    public String getWorkEmail() { return workEmail; } public String getPersonalEmail() { return personalEmail; }
    public String getPhone() { return phone; } public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getGender() { return gender; } public String getAddressLine1() { return addressLine1; }
    public String getAddressLine2() { return addressLine2; } public String getCity() { return city; }
    public String getState() { return state; } public String getCountry() { return country; }
    public String getPostalCode() { return postalCode; } public EmployeeStatus getStatus() { return status; }
    public EmploymentType getEmploymentType() { return employmentType; } public LocalDate getDateOfJoining() { return dateOfJoining; }
    public String getEmergencyContactName() { return emergencyContactName; } public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public String getSkills() { return skills; } public Department getDepartment() { return department; }
    public Designation getDesignation() { return designation; } public Employee getManager() { return manager; }
    public Location getLocation() { return location; }
}
