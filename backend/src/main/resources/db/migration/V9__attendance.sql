CREATE TABLE attendance_records (
    id BIGINT NOT NULL AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    work_date DATE NOT NULL,
    check_in_at TIMESTAMP(6) NOT NULL,
    check_out_at TIMESTAMP(6) NULL,
    worked_minutes INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'PRESENT',
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_attendance_employee_work_date UNIQUE (employee_id, work_date),
    CONSTRAINT ck_attendance_worked_minutes CHECK (worked_minutes >= 0),
    CONSTRAINT ck_attendance_status CHECK (status IN ('PRESENT','HALF_DAY','ABSENT')),
    CONSTRAINT fk_attendance_employee FOREIGN KEY (employee_id) REFERENCES employees (id),
    INDEX ix_attendance_work_date (work_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE attendance_correction_requests (
    id BIGINT NOT NULL AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    work_date DATE NOT NULL,
    requested_check_in_at TIMESTAMP(6) NOT NULL,
    requested_check_out_at TIMESTAMP(6) NOT NULL,
    reason VARCHAR(1000) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    reviewer_id BIGINT NULL,
    reviewer_comment VARCHAR(1000) NULL,
    reviewed_at TIMESTAMP(6) NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_attendance_correction_employee_date UNIQUE (employee_id, work_date),
    CONSTRAINT ck_attendance_correction_status CHECK (status IN ('PENDING','APPROVED','REJECTED')),
    CONSTRAINT ck_attendance_correction_time CHECK (requested_check_out_at > requested_check_in_at),
    CONSTRAINT fk_attendance_correction_employee FOREIGN KEY (employee_id) REFERENCES employees (id),
    CONSTRAINT fk_attendance_correction_reviewer FOREIGN KEY (reviewer_id) REFERENCES employees (id),
    INDEX ix_attendance_correction_status (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
