package com.hrgenius.leave.service;

import com.hrgenius.leave.dto.LeaveAccrualRunResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.ZoneId;

@Component
public class LeaveAccrualScheduler {
    private static final Logger log = LoggerFactory.getLogger(LeaveAccrualScheduler.class);
    private final LeaveManagementService leave;
    private final ZoneId zone;

    public LeaveAccrualScheduler(LeaveManagementService leave, @Value("${app.leave.accrual-time-zone:Asia/Kolkata}") String zone) {
        this.leave = leave;
        this.zone = ZoneId.of(zone);
    }

    @Scheduled(cron = "${app.leave.accrual-cron:0 5 0 1 * *}", zone = "${app.leave.accrual-time-zone:Asia/Kolkata}")
    public void accrueCurrentMonth() {
        YearMonth period = YearMonth.now(zone);
        LeaveAccrualRunResponse result = leave.runAccrual(period);
        log.info("Leave accrual completed for {}: {} employees, {} days", period, result.processedEmployees(), result.totalGrantedDays());
    }
}
