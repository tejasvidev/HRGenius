package com.hrgenius.organization.repository;

import com.hrgenius.organization.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
    boolean existsByCodeIgnoreCase(String code);
    boolean existsByNameIgnoreCase(String name);
    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    Page<Location> findAllByActiveTrue(Pageable pageable);
    Page<Location> findAllByActiveFalse(Pageable pageable);
}
