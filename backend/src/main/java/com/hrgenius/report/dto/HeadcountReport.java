package com.hrgenius.report.dto;
import java.time.LocalDate;
import java.util.List;
public record HeadcountReport(long totalEmployees,long activeEmployees,long inactiveEmployees,long newJoiners,LocalDate from,LocalDate to,List<DepartmentHeadcount> departments) { }
