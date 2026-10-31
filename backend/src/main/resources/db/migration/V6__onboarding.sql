CREATE TABLE onboarding_templates (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_onboarding_template_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE onboarding_template_tasks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    template_id BIGINT NOT NULL,
    title VARCHAR(160) NOT NULL,
    description VARCHAR(1000) NULL,
    assignee_role VARCHAR(16) NOT NULL,
    days_from_start SMALLINT NOT NULL DEFAULT 0,
    required BOOLEAN NOT NULL DEFAULT TRUE,
    display_order SMALLINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_onboarding_template_task_days CHECK (days_from_start BETWEEN 0 AND 365),
    CONSTRAINT fk_onboarding_template_task_template FOREIGN KEY (template_id) REFERENCES onboarding_templates (id),
    INDEX ix_onboarding_template_tasks (template_id, display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE onboarding_plans (
    id BIGINT NOT NULL AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    template_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'NOT_STARTED',
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_onboarding_plan_employee FOREIGN KEY (employee_id) REFERENCES employees (id),
    CONSTRAINT fk_onboarding_plan_template FOREIGN KEY (template_id) REFERENCES onboarding_templates (id),
    INDEX ix_onboarding_plans_employee_status (employee_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE onboarding_tasks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    plan_id BIGINT NOT NULL,
    template_task_id BIGINT NULL,
    title VARCHAR(160) NOT NULL,
    description VARCHAR(1000) NULL,
    assignee_role VARCHAR(16) NOT NULL,
    assignee_employee_id BIGINT NULL,
    due_date DATE NOT NULL,
    required BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    notes VARCHAR(1000) NULL,
    completed_at TIMESTAMP(6) NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_onboarding_task_plan FOREIGN KEY (plan_id) REFERENCES onboarding_plans (id),
    CONSTRAINT fk_onboarding_task_template_task FOREIGN KEY (template_task_id) REFERENCES onboarding_template_tasks (id),
    CONSTRAINT fk_onboarding_task_assignee FOREIGN KEY (assignee_employee_id) REFERENCES employees (id),
    INDEX ix_onboarding_tasks_plan_status (plan_id, status, due_date),
    INDEX ix_onboarding_tasks_assignee (assignee_employee_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE onboarding_documents (
    id BIGINT NOT NULL AUTO_INCREMENT,
    plan_id BIGINT NOT NULL,
    document_type VARCHAR(32) NOT NULL,
    document_name VARCHAR(160) NOT NULL,
    reference_url VARCHAR(512) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    reviewer_employee_id BIGINT NULL,
    review_comment VARCHAR(1000) NULL,
    submitted_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    reviewed_at TIMESTAMP(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_onboarding_document_plan FOREIGN KEY (plan_id) REFERENCES onboarding_plans (id),
    CONSTRAINT fk_onboarding_document_reviewer FOREIGN KEY (reviewer_employee_id) REFERENCES employees (id),
    INDEX ix_onboarding_documents_plan_status (plan_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
