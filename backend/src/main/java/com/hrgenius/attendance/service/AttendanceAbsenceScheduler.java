package com.hrgenius.attendance.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class AttendanceAbsenceScheduler {
    private static final Logger log=LoggerFactory.getLogger(AttendanceAbsenceScheduler.class);
    private final AttendanceService attendance;
    private final ZoneId zone;
    public AttendanceAbsenceScheduler(AttendanceService attendance,@Value("${app.attendance.time-zone:Asia/Kolkata}") String zone){this.attendance=attendance;this.zone=ZoneId.of(zone);}
    @Scheduled(cron="${app.attendance.absence-cron:0 15 0 * * *}",zone="${app.attendance.time-zone:Asia/Kolkata}")
    public void markYesterdayAbsences(){var day=attendance.today().minusDays(1);int marked=attendance.markAbsences(day);log.info("Attendance rollup for {} marked {} absent employees in {}",day,marked,zone);}
}
