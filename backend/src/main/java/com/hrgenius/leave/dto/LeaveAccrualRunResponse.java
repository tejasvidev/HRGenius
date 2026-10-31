package com.hrgenius.leave.dto;
import java.math.BigDecimal;
import java.time.YearMonth;
public record LeaveAccrualRunResponse(YearMonth period,int processedEmployees,BigDecimal totalGrantedDays) { }
