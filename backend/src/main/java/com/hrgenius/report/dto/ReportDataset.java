package com.hrgenius.report.dto;
import java.util.List;
public record ReportDataset(String report,List<String> columns,List<List<Object>> rows,int page,int pageSize,long totalElements,int totalPages) { }
