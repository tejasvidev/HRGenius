package com.hrgenius.attendance.repository;

import com.hrgenius.attendance.entity.AttendancePolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendancePolicyRepository extends JpaRepository<AttendancePolicy,Byte> { }
