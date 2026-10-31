package com.hrgenius.organization.service;

import com.hrgenius.common.ConflictException;
import com.hrgenius.common.PageRequests;
import com.hrgenius.common.PageResponse;
import com.hrgenius.common.ResourceNotFoundException;
import com.hrgenius.organization.dto.DesignationRequest;
import com.hrgenius.organization.dto.DesignationResponse;
import com.hrgenius.organization.entity.Department;
import com.hrgenius.organization.entity.Designation;
import com.hrgenius.organization.repository.DepartmentRepository;
import com.hrgenius.organization.repository.DesignationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class DesignationService {
    private final DesignationRepository designations;
    private final DepartmentRepository departments;
    public DesignationService(DesignationRepository designations, DepartmentRepository departments) {
        this.designations = designations;
        this.departments = departments;
    }

    @Transactional(readOnly = true)
    public PageResponse<DesignationResponse> list(Boolean active, Long departmentId, Pageable pageable) {
        Pageable bounded = PageRequests.capped(pageable);
        Page<Designation> page;
        if (departmentId != null && active != null) page = designations.findAllByDepartment_IdAndActive(departmentId, active, bounded);
        else if (departmentId != null) page = designations.findAllByDepartment_Id(departmentId, bounded);
        else if (active == null) page = designations.findAll(bounded);
        else if (active) page = designations.findAllByActiveTrue(bounded);
        else page = designations.findAllByActiveFalse(bounded);
        return PageResponse.from(page, DesignationResponse::from);
    }

    @Transactional(readOnly = true)
    public Designation get(Long id) {
        return designations.findById(id).orElseThrow(() -> new ResourceNotFoundException("Designation", id));
    }

    @Transactional
    public Designation create(DesignationRequest request) {
        Department department = resolveDepartment(request.departmentId());
        ensureUnique(request.code(), request.title(), request.departmentId(), null);
        return designations.save(new Designation(department, request.code(), request.title(), request.jobGrade(), request.description()));
    }

    @Transactional
    public Designation update(Long id, DesignationRequest request) {
        Designation designation = get(id);
        Department department = resolveDepartment(request.departmentId());
        ensureUnique(request.code(), request.title(), request.departmentId(), id);
        designation.update(department, request.code(), request.title(), request.jobGrade(), request.description());
        return designations.save(designation);
    }

    @Transactional
    public Designation setActive(Long id, boolean active) {
        Designation designation = get(id);
        if (active) designation.activate(); else designation.deactivate();
        return designations.save(designation);
    }

    private Department resolveDepartment(Long departmentId) {
        if (departmentId == null) return null;
        Department department = departments.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", departmentId));
        if (!department.isActive()) throw new ConflictException("Cannot assign a designation to an inactive department");
        return department;
    }

    private void ensureUnique(String rawCode, String rawTitle, Long departmentId, Long currentId) {
        String code = rawCode.trim().toUpperCase(Locale.ROOT);
        boolean codeTaken = currentId == null ? designations.existsByCodeIgnoreCase(code)
                : designations.existsByCodeIgnoreCaseAndIdNot(code, currentId);
        boolean titleTaken = departmentId == null
                ? (currentId == null ? designations.existsByDepartmentIsNullAndTitleIgnoreCase(rawTitle.trim())
                : designations.existsByDepartmentIsNullAndTitleIgnoreCaseAndIdNot(rawTitle.trim(), currentId))
                : (currentId == null ? designations.existsByDepartment_IdAndTitleIgnoreCase(departmentId, rawTitle.trim())
                : designations.existsByDepartment_IdAndTitleIgnoreCaseAndIdNot(departmentId, rawTitle.trim(), currentId));
        if (codeTaken) throw new ConflictException("Designation code already exists");
        if (titleTaken) throw new ConflictException("Designation title already exists in this department");
    }
}
