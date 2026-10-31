CREATE TABLE leave_types (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(24) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_leave_types_code UNIQUE (code),
    CONSTRAINT uk_leave_types_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE leave_balances (
    id BIGINT NOT NULL AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    leave_type_id BIGINT NOT NULL,
    calendar_year SMALLINT NOT NULL,
    entitled_days DECIMAL(6,2) NOT NULL,
    used_days DECIMAL(6,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_leave_balance_employee_type_year UNIQUE (employee_id, leave_type_id, calendar_year),
    CONSTRAINT ck_leave_balance_entitled CHECK (entitled_days >= 0),
    CONSTRAINT ck_leave_balance_used CHECK (used_days >= 0 AND used_days <= entitled_days),
    CONSTRAINT fk_leave_balance_employee FOREIGN KEY (employee_id) REFERENCES employees (id),
    CONSTRAINT fk_leave_balance_type FOREIGN KEY (leave_type_id) REFERENCES leave_types (id),
    INDEX ix_leave_balance_employee_year (employee_id, calendar_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE leave_requests (
    id BIGINT NOT NULL AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    leave_type_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    requested_days DECIMAL(6,2) NOT NULL,
    reason VARCHAR(1000) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    reviewer_id BIGINT NULL,
    reviewer_comment VARCHAR(1000) NULL,
    decided_at TIMESTAMP(6) NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT ck_leave_request_dates CHECK (end_date >= start_date),
    CONSTRAINT ck_leave_request_days CHECK (requested_days > 0),
    CONSTRAINT fk_leave_request_employee FOREIGN KEY (employee_id) REFERENCES employees (id),
    CONSTRAINT fk_leave_request_type FOREIGN KEY (leave_type_id) REFERENCES leave_types (id),
    CONSTRAINT fk_leave_request_reviewer FOREIGN KEY (reviewer_id) REFERENCES employees (id),
    INDEX ix_leave_requests_employee_status (employee_id, status, start_date),
    INDEX ix_leave_requests_reviewer (reviewer_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
