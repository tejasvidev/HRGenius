package com.hrgenius.employee.service;

import com.hrgenius.common.ConflictException;
import com.hrgenius.common.PageRequests;
import com.hrgenius.common.PageResponse;
import com.hrgenius.common.ResourceNotFoundException;
import com.hrgenius.employee.dto.EmployeeRequest;
import com.hrgenius.employee.dto.EmployeeResponse;
import com.hrgenius.employee.dto.EmployeeSelfUpdateRequest;
import com.hrgenius.employee.entity.*;
import com.hrgenius.employee.repository.EmployeeHistoryRepository;
import com.hrgenius.employee.repository.EmployeeRepository;
import com.hrgenius.organization.entity.*;
import com.hrgenius.organization.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class EmployeeService {
    private final EmployeeRepository employees;
    private final EmployeeHistoryRepository history;
    private final DepartmentRepository departments;
    private final DesignationRepository designations;
    private final LocationRepository locations;
    public EmployeeService(EmployeeRepository employees, EmployeeHistoryRepository history, DepartmentRepository departments,
                           DesignationRepository designations, LocationRepository locations) {
        this.employees = employees; this.history = history; this.departments = departments;
        this.designations = designations; this.locations = locations;
    }

    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> list(String query, Long departmentId, Long designationId, Long locationId,
                                               EmployeeStatus status, Pageable pageable) {
        Specification<Employee> spec = (root, criteria, cb) -> cb.conjunction();
        if (query != null && !query.isBlank()) {
            String pattern = "%" + query.trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, criteria, cb) -> cb.or(cb.like(cb.lower(root.get("firstName")), pattern),
                    cb.like(cb.lower(root.get("lastName")), pattern), cb.like(cb.lower(root.get("workEmail")), pattern),
                    cb.like(cb.lower(root.get("employeeNumber")), pattern)));
        }
        if (departmentId != null) spec = spec.and((root, criteria, cb) -> cb.equal(root.get("department").get("id"), departmentId));
        if (designationId != null) spec = spec.and((root, criteria, cb) -> cb.equal(root.get("designation").get("id"), designationId));
        if (locationId != null) spec = spec.and((root, criteria, cb) -> cb.equal(root.get("location").get("id"), locationId));
        if (status != null) spec = spec.and((root, criteria, cb) -> cb.equal(root.get("status"), status));
        return PageResponse.from(employees.findAll(spec, PageRequests.capped(pageable)), EmployeeResponse::from);
    }

    @Transactional(readOnly = true)
    public Employee get(Long id) { return employees.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee", id)); }

    @Transactional(readOnly = true)
    public Employee getByEmail(String email) { return employees.findByWorkEmailIgnoreCase(email)
            .orElseThrow(() -> new ResourceNotFoundException("Employee profile", null)); }

    @Transactional
    public Employee updatePersonalDetails(String email, EmployeeSelfUpdateRequest request) {
        Employee employee = getByEmail(email);
        employee.updatePersonalDetails(request.personalEmail(), request.phone(), request.addressLine1(),
                request.addressLine2(), request.city(), request.state(), request.country(), request.postalCode());
        return employees.save(employee);
    }

    @Transactional
    public Employee create(EmployeeRequest request) {
        ensureUnique(request, null);
        Employee employee = new Employee(request.employeeNumber(), request.firstName(), request.lastName(), request.workEmail(),
                request.personalEmail(), request.phone(), request.dateOfBirth(), request.gender(), request.addressLine1(),
                request.addressLine2(), request.city(), request.state(), request.country(), request.postalCode(),
                request.employmentType(), request.dateOfJoining(), request.emergencyContactName(), request.emergencyContactPhone(),
                request.skills(), activeDepartment(request.departmentId()), activeDesignation(request.designationId(), request.departmentId()),
                activeManager(request.managerId()), activeLocation(request.locationId()));
        employee = employees.save(employee);
        history.save(new EmployeeHistory(employee, "HIRED", "Employee record created", request.dateOfJoining()));
        return employee;
    }

    @Transactional
    public Employee update(Long id, EmployeeRequest request) {
        Employee employee = get(id); ensureUnique(request, id);
        if (!employee.getEmployeeNumber().equalsIgnoreCase(request.employeeNumber().trim()))
            throw new ConflictException("Employee number cannot be changed after creation");
        Department department = activeDepartment(request.departmentId());
        Designation designation = activeDesignation(request.designationId(), request.departmentId());
        Employee manager = activeManager(request.managerId());
        validateManagerChain(id, manager);
        Location location = activeLocation(request.locationId());
        if (!Objects.equals(employee.getDepartment() == null ? null : employee.getDepartment().getId(), request.departmentId())
                || !Objects.equals(employee.getDesignation() == null ? null : employee.getDesignation().getId(), request.designationId())) {
            history.save(new EmployeeHistory(employee, "TRANSFER", "Department or designation changed", LocalDate.now()));
        }
        employee.update(request.firstName(), request.lastName(), request.workEmail(), request.personalEmail(), request.phone(),
                request.dateOfBirth(), request.gender(), request.addressLine1(), request.addressLine2(), request.city(),
                request.state(), request.country(), request.postalCode(), request.employmentType(), request.dateOfJoining(),
                request.emergencyContactName(), request.emergencyContactPhone(), request.skills(), department, designation, manager, location);
        return employees.save(employee);
    }

    @Transactional
    public Employee setStatus(Long id, EmployeeStatus status) {
        Employee employee = get(id);
        if (employee.getStatus() == status) return employee;
        if (employee.getStatus() == EmployeeStatus.SEPARATED) throw new ConflictException("A separated employee cannot be reactivated");
        employee.setStatus(status);
        history.save(new EmployeeHistory(employee, status.name(), "Employee status changed to " + status, LocalDate.now()));
        return employees.save(employee);
    }

    @Transactional(readOnly = true)
    public List<com.hrgenius.employee.dto.EmployeeHistoryResponse> history(Long id) {
        get(id);
        return history.findAllByEmployee_IdOrderByEffectiveDateDesc(id).stream()
                .map(com.hrgenius.employee.dto.EmployeeHistoryResponse::from).toList();
    }

    private void ensureUnique(EmployeeRequest request, Long id) {
        String number = request.employeeNumber().trim(); String email = request.workEmail().trim();
        boolean numberTaken = id == null ? employees.existsByEmployeeNumberIgnoreCase(number)
                : employees.existsByEmployeeNumberIgnoreCaseAndIdNot(number, id);
        boolean emailTaken = id == null ? employees.existsByWorkEmailIgnoreCase(email)
                : employees.existsByWorkEmailIgnoreCaseAndIdNot(email, id);
        if (numberTaken) throw new ConflictException("Employee number already exists");
        if (emailTaken) throw new ConflictException("Work email already belongs to an employee");
    }
    private Department activeDepartment(Long id) {
        if (id == null) return null;
        Department value = departments.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department", id));
        if (!value.isActive()) throw new ConflictException("Employee department must be active"); return value;
    }
    private Designation activeDesignation(Long id, Long departmentId) {
        if (id == null) return null;
        Designation value = designations.findById(id).orElseThrow(() -> new ResourceNotFoundException("Designation", id));
        if (!value.isActive()) throw new ConflictException("Employee designation must be active");
        if (!Objects.equals(value.getDepartment() == null ? null : value.getDepartment().getId(), departmentId))
            throw new ConflictException("Designation does not belong to the selected department");
        return value;
    }
    private Employee activeManager(Long id) {
        if (id == null) return null;
        Employee value = employees.findById(id).orElseThrow(() -> new ResourceNotFoundException("Manager", id));
        if (value.getStatus() != EmployeeStatus.ACTIVE) throw new ConflictException("Reporting manager must be active"); return value;
    }
    private void validateManagerChain(Long employeeId, Employee manager) {
        Employee current = manager;
        while (current != null) {
            if (current.getId().equals(employeeId)) throw new ConflictException("Reporting manager assignment would create a hierarchy cycle");
            current = current.getManager();
        }
    }
    private Location activeLocation(Long id) {
        if (id == null) return null;
        Location value = locations.findById(id).orElseThrow(() -> new ResourceNotFoundException("Location", id));
        if (!value.isActive()) throw new ConflictException("Employee location must be active"); return value;
    }
}
