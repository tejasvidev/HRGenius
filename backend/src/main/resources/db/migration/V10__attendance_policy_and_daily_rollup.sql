ALTER TABLE attendance_records
    MODIFY COLUMN check_in_at TIMESTAMP(6) NULL,
    ADD COLUMN late_minutes INT NOT NULL DEFAULT 0 AFTER worked_minutes,
    ADD COLUMN overtime_minutes INT NOT NULL DEFAULT 0 AFTER late_minutes,
    ADD CONSTRAINT ck_attendance_late_minutes CHECK (late_minutes >= 0),
    ADD CONSTRAINT ck_attendance_overtime_minutes CHECK (overtime_minutes >= 0),
    ADD CONSTRAINT ck_attendance_punch_pair CHECK (
        (check_in_at IS NULL AND check_out_at IS NULL AND status = 'ABSENT') OR
        (check_in_at IS NOT NULL AND status <> 'ABSENT' AND
            (check_out_at IS NULL OR check_out_at > check_in_at))
    );

CREATE TABLE attendance_policy (
    id TINYINT NOT NULL,
    workday_start TIME NOT NULL DEFAULT '09:00:00',
    standard_work_minutes INT NOT NULL DEFAULT 480,
    late_grace_minutes INT NOT NULL DEFAULT 15,
    half_day_threshold_minutes INT NOT NULL DEFAULT 240,
    overtime_after_minutes INT NOT NULL DEFAULT 480,
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT ck_attendance_policy_singleton CHECK (id = 1),
    CONSTRAINT ck_attendance_policy_standard CHECK (standard_work_minutes BETWEEN 1 AND 1440),
    CONSTRAINT ck_attendance_policy_grace CHECK (late_grace_minutes BETWEEN 0 AND 360),
    CONSTRAINT ck_attendance_policy_half_day CHECK (half_day_threshold_minutes BETWEEN 1 AND 1440),
    CONSTRAINT ck_attendance_policy_overtime CHECK (overtime_after_minutes BETWEEN 1 AND 2880)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO attendance_policy (id) VALUES (1);
