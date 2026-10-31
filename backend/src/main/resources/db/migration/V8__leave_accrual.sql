CREATE TABLE leave_accrual_rules (
    id BIGINT NOT NULL AUTO_INCREMENT,
    leave_type_id BIGINT NOT NULL,
    monthly_days DECIMAL(5,2) NOT NULL,
    annual_cap DECIMAL(6,2) NULL,
    effective_from DATE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_leave_accrual_type UNIQUE (leave_type_id),
    CONSTRAINT ck_leave_accrual_monthly_days CHECK (monthly_days > 0),
    CONSTRAINT ck_leave_accrual_annual_cap CHECK (annual_cap IS NULL OR annual_cap >= monthly_days),
    CONSTRAINT fk_leave_accrual_rules_type FOREIGN KEY (leave_type_id) REFERENCES leave_types (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE leave_accrual_ledger (
    id BIGINT NOT NULL AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    leave_type_id BIGINT NOT NULL,
    accrual_year SMALLINT NOT NULL,
    accrual_month TINYINT NOT NULL,
    granted_days DECIMAL(5,2) NOT NULL,
    processed_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_leave_accrual_employee_type_period UNIQUE (employee_id, leave_type_id, accrual_year, accrual_month),
    CONSTRAINT ck_leave_accrual_month CHECK (accrual_month BETWEEN 1 AND 12),
    CONSTRAINT ck_leave_accrual_granted_days CHECK (granted_days >= 0),
    CONSTRAINT fk_leave_accrual_employee FOREIGN KEY (employee_id) REFERENCES employees (id),
    CONSTRAINT fk_leave_accrual_ledger_type FOREIGN KEY (leave_type_id) REFERENCES leave_types (id),
    INDEX ix_leave_accrual_period (accrual_year, accrual_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
