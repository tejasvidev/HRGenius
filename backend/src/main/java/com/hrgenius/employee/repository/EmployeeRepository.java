package com.hrgenius.employee.repository;

import com.hrgenius.employee.entity.Employee;
import com.hrgenius.employee.entity.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    boolean existsByEmployeeNumberIgnoreCase(String employeeNumber);
    boolean existsByWorkEmailIgnoreCase(String workEmail);
    Optional<Employee> findByWorkEmailIgnoreCase(String workEmail);
    boolean existsByEmployeeNumberIgnoreCaseAndIdNot(String employeeNumber, Long id);
    boolean existsByWorkEmailIgnoreCaseAndIdNot(String workEmail, Long id);
    Page<Employee> findAllByStatus(EmployeeStatus status, Pageable pageable);
    Page<Employee> findAllByStatusAndDepartment_Id(EmployeeStatus status, Long departmentId, Pageable pageable);
    long countByStatus(EmployeeStatus status);
    long countByDateOfJoiningBetween(java.time.LocalDate from, java.time.LocalDate to);
    @Query("select coalesce(d.name,'Unassigned'), count(e) from Employee e left join e.department d group by d.id,d.name order by d.name")
    java.util.List<Object[]> departmentHeadcount();
}
