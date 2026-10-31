package com.hrgenius.report.service;
import java.util.Arrays;
public enum ReportType {
    HEADCOUNT("headcount"), EMPLOYEE_DIRECTORY("employee-directory"), DEPARTMENT_WISE("department-wise"), DESIGNATION_WISE("designation-wise"), ATTENDANCE("attendance"), LATE_ARRIVALS("late-arrivals"), ABSENCES("absences"), OVERTIME("overtime"), LEAVE("leave"), LEAVE_BALANCES("leave-balances"), RECRUITMENT("recruitment"), HIRING_FUNNEL("hiring-funnel"), ONBOARDING("onboarding"), PAYROLL("payroll"), PERFORMANCE("performance");
    private final String path; ReportType(String path){this.path=path;} public String path(){return path;}
    public static ReportType from(String path){return Arrays.stream(values()).filter(t->t.path.equalsIgnoreCase(path)).findFirst().orElseThrow(()->new IllegalArgumentException("Unknown report type: "+path));}
}
