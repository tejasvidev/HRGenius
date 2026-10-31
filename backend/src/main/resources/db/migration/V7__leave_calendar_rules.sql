CREATE TABLE leave_calendar_policy (
    id TINYINT NOT NULL,
    working_days VARCHAR(64) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT ck_leave_calendar_policy_singleton CHECK (id = 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO leave_calendar_policy (id, working_days) VALUES (1, 'MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY');

CREATE TABLE leave_holidays (
    id BIGINT NOT NULL AUTO_INCREMENT,
    holiday_date DATE NOT NULL,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_leave_holiday_date UNIQUE (holiday_date),
    INDEX ix_leave_holiday_active_date (active, holiday_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
