package com.hrgenius.report.controller;
import com.hrgenius.report.dto.HeadcountReport;
import com.hrgenius.report.service.ReportService;
import com.hrgenius.report.service.ReportType;
import com.hrgenius.report.service.ReportExportWriter;
import com.hrgenius.report.dto.ReportDataset;
import com.hrgenius.report.dto.ReportExportData;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Set;
@RestController @RequestMapping("/api/v1/reports") public class ReportController {
    private final ReportService reports; public ReportController(ReportService reports){this.reports=reports;}
    @GetMapping("/headcount")
    public HeadcountReport headcount(@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to,Authentication auth){authorize(ReportType.HEADCOUNT,auth);return reports.headcount(from,to);}

    @GetMapping("/headcount/data")
    public ReportDataset headcountData(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required=false) Long departmentId,@RequestParam(required=false) Long employeeId,@PageableDefault(size=50) Pageable pageable,Authentication auth){
        authorize(ReportType.HEADCOUNT,auth);return reports.report("headcount",from,to,departmentId,employeeId,pageable);
    }

    @GetMapping("/{type}")
    public ReportDataset report(@PathVariable String type,@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to,@RequestParam(required=false) Long departmentId,
            @RequestParam(required=false) Long employeeId,@PageableDefault(size=50) Pageable pageable,Authentication auth){
        ReportType reportType=parse(type);authorize(reportType,auth);return reports.report(type,from,to,departmentId,employeeId,pageable);
    }

    @GetMapping("/{type}/export")
    public ResponseEntity<byte[]> export(@PathVariable String type,@RequestParam(defaultValue="csv") String format,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required=false) Long departmentId,@RequestParam(required=false) Long employeeId,@PageableDefault(size=50) Pageable pageable,Authentication auth){
        ReportType reportType=parse(type);authorize(reportType,auth);ReportExportData export=reports.exportData(type,from,to,departmentId,employeeId,pageable);
        String normalized=format.toLowerCase(java.util.Locale.ROOT);byte[] bytes;String mime,extension;
        if(normalized.equals("csv")){bytes=ReportExportWriter.csv(export.dataset());mime="text/csv;charset=UTF-8";extension="csv";}
        else if(normalized.equals("xlsx")){bytes=ReportExportWriter.xlsx(export.dataset());mime="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";extension="xlsx";}
        else throw new com.hrgenius.common.ConflictException("Export format must be csv or xlsx");
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(mime)).header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"hrgenius-"+reportType.path()+"."+extension+"\"")
                .header("X-Export-Truncated",Boolean.toString(export.truncated())).body(bytes);
    }
    private static ReportType parse(String type){try{return ReportType.from(type);}catch(IllegalArgumentException ex){throw new com.hrgenius.common.ConflictException(ex.getMessage());}}
    private static void authorize(ReportType type,Authentication authentication){
        Set<String> roles=authentication.getAuthorities().stream().map(a->a.getAuthority()).collect(java.util.stream.Collectors.toSet());
        boolean payroll=type==ReportType.PAYROLL,performance=type==ReportType.PERFORMANCE,recruitment=type==ReportType.RECRUITMENT||type==ReportType.HIRING_FUNNEL;
        Set<String> allowed=payroll||performance?Set.of("ROLE_SUPER_ADMIN","ROLE_ADMIN","ROLE_HR_MANAGER"):
                recruitment?Set.of("ROLE_SUPER_ADMIN","ROLE_ADMIN","ROLE_HR_MANAGER","ROLE_HR_EXECUTIVE","ROLE_RECRUITER"):
                        Set.of("ROLE_SUPER_ADMIN","ROLE_ADMIN","ROLE_HR_MANAGER","ROLE_HR_EXECUTIVE");
        if(roles.stream().noneMatch(allowed::contains))throw new AccessDeniedException("You do not have access to this report");
    }
}
