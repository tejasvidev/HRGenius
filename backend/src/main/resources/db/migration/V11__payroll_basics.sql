CREATE TABLE salary_assignments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    currency CHAR(3) NOT NULL DEFAULT 'INR',
    monthly_gross DECIMAL(13,2) NOT NULL,
    fixed_deduction DECIMAL(13,2) NOT NULL DEFAULT 0,
    effective_from DATE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_salary_assignment_employee UNIQUE (employee_id),
    CONSTRAINT ck_salary_monthly_gross CHECK (monthly_gross > 0),
    CONSTRAINT ck_salary_fixed_deduction CHECK (fixed_deduction >= 0 AND fixed_deduction <= monthly_gross),
    CONSTRAINT fk_salary_assignment_employee FOREIGN KEY (employee_id) REFERENCES employees (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE payroll_runs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    period_year SMALLINT NOT NULL,
    period_month TINYINT NOT NULL,
    currency CHAR(3) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
    employee_count INT NOT NULL DEFAULT 0,
    total_gross DECIMAL(15,2) NOT NULL DEFAULT 0,
    total_deductions DECIMAL(15,2) NOT NULL DEFAULT 0,
    total_net DECIMAL(15,2) NOT NULL DEFAULT 0,
    approved_by BIGINT NULL,
    approved_at TIMESTAMP(6) NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_payroll_period UNIQUE (period_year, period_month),
    CONSTRAINT ck_payroll_period_month CHECK (period_month BETWEEN 1 AND 12),
    CONSTRAINT ck_payroll_run_status CHECK (status IN ('DRAFT','APPROVED')),
    CONSTRAINT fk_payroll_run_approver FOREIGN KEY (approved_by) REFERENCES employees (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE payroll_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    payroll_run_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    currency CHAR(3) NOT NULL,
    gross_pay DECIMAL(13,2) NOT NULL,
    fixed_deductions DECIMAL(13,2) NOT NULL,
    net_pay DECIMAL(13,2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_payroll_item_employee UNIQUE (payroll_run_id, employee_id),
    CONSTRAINT ck_payroll_item_amounts CHECK (gross_pay > 0 AND fixed_deductions >= 0 AND net_pay = gross_pay - fixed_deductions),
    CONSTRAINT fk_payroll_item_run FOREIGN KEY (payroll_run_id) REFERENCES payroll_runs (id),
    CONSTRAINT fk_payroll_item_employee FOREIGN KEY (employee_id) REFERENCES employees (id),
    INDEX ix_payroll_item_employee (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
