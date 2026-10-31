package com.hrgenius.recruitment.dto;
import com.hrgenius.recruitment.entity.JobOpening;
import com.hrgenius.recruitment.entity.JobOpeningStatus;
import com.hrgenius.employee.entity.EmploymentType;
import java.math.BigDecimal;
import java.time.LocalDate;
public record JobOpeningResponse(Long id, String title, Long departmentId, String departmentName,
        Long designationId, String designationTitle, Long locationId, String locationName, int vacancies,
        String description, String requiredSkills, BigDecimal minExperienceYears, BigDecimal maxExperienceYears,
        EmploymentType employmentType, BigDecimal salaryMin, BigDecimal salaryMax, Long hiringManagerId,
        Long recruiterId, LocalDate openingDate, LocalDate closingDate, JobOpeningStatus status) {
    public static JobOpeningResponse from(JobOpening j) {
        return new JobOpeningResponse(j.getId(), j.getTitle(), j.getDepartment()==null?null:j.getDepartment().getId(),
                j.getDepartment()==null?null:j.getDepartment().getName(), j.getDesignation()==null?null:j.getDesignation().getId(),
                j.getDesignation()==null?null:j.getDesignation().getTitle(), j.getLocation()==null?null:j.getLocation().getId(),
                j.getLocation()==null?null:j.getLocation().getName(), j.getVacancies(), j.getDescription(), j.getRequiredSkills(),
                j.getMinExperienceYears(), j.getMaxExperienceYears(), j.getEmploymentType(), j.getSalaryMin(), j.getSalaryMax(),
                j.getHiringManager()==null?null:j.getHiringManager().getId(), j.getRecruiter()==null?null:j.getRecruiter().getId(),
                j.getOpeningDate(), j.getClosingDate(), j.getStatus());
    }
}
