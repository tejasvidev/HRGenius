package com.hrgenius.organization.repository;

import com.hrgenius.organization.entity.Designation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignationRepository extends JpaRepository<Designation, Long> {
    boolean existsByCodeIgnoreCase(String code);
    boolean existsByDepartment_IdAndTitleIgnoreCase(Long departmentId, String title);
    boolean existsByDepartmentIsNullAndTitleIgnoreCase(String title);
    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);
    boolean existsByDepartment_IdAndTitleIgnoreCaseAndIdNot(Long departmentId, String title, Long id);
    boolean existsByDepartmentIsNullAndTitleIgnoreCaseAndIdNot(String title, Long id);
    Page<Designation> findAllByActiveTrue(Pageable pageable);
    Page<Designation> findAllByActiveFalse(Pageable pageable);
    Page<Designation> findAllByDepartment_Id(Long departmentId, Pageable pageable);
    Page<Designation> findAllByDepartment_IdAndActive(Long departmentId, boolean active, Pageable pageable);
}
