package com.hrgenius.organization.service;

import com.hrgenius.common.ConflictException;
import com.hrgenius.common.PageRequests;
import com.hrgenius.common.PageResponse;
import com.hrgenius.common.ResourceNotFoundException;
import com.hrgenius.organization.dto.DepartmentRequest;
import com.hrgenius.organization.dto.DepartmentResponse;
import com.hrgenius.organization.entity.Department;
import com.hrgenius.organization.repository.DepartmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class DepartmentService {
    private final DepartmentRepository departments;
    public DepartmentService(DepartmentRepository departments) { this.departments = departments; }

    @Transactional(readOnly = true)
    public PageResponse<DepartmentResponse> list(Boolean active, Pageable pageable) {
        Pageable bounded = PageRequests.capped(pageable);
        Page<Department> page = active == null ? departments.findAll(bounded)
                : active ? departments.findAllByActiveTrue(bounded) : departments.findAllByActiveFalse(bounded);
        return PageResponse.from(page, DepartmentResponse::from);
    }

    @Transactional(readOnly = true)
    public Department get(Long id) {
        return departments.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department", id));
    }

    @Transactional
    public Department create(DepartmentRequest request) {
        ensureUnique(request.code(), request.name(), null);
        return departments.save(new Department(request.code(), request.name(), request.description()));
    }

    @Transactional
    public Department update(Long id, DepartmentRequest request) {
        Department department = get(id);
        ensureUnique(request.code(), request.name(), id);
        department.update(request.code(), request.name(), request.description());
        return departments.save(department);
    }

    @Transactional
    public Department setActive(Long id, boolean active) {
        Department department = get(id);
        if (active) department.activate(); else department.deactivate();
        return departments.save(department);
    }

    private void ensureUnique(String rawCode, String rawName, Long currentId) {
        String code = rawCode.trim().toUpperCase(Locale.ROOT);
        boolean codeTaken = currentId == null ? departments.existsByCodeIgnoreCase(code)
                : departments.existsByCodeIgnoreCaseAndIdNot(code, currentId);
        boolean nameTaken = currentId == null ? departments.existsByNameIgnoreCase(rawName.trim())
                : departments.existsByNameIgnoreCaseAndIdNot(rawName.trim(), currentId);
        if (codeTaken) throw new ConflictException("Department code already exists");
        if (nameTaken) throw new ConflictException("Department name already exists");
    }
}
