CREATE TABLE job_openings (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(160) NOT NULL,
    department_id BIGINT NULL,
    designation_id BIGINT NULL,
    location_id BIGINT NULL,
    vacancies INT NOT NULL,
    description TEXT NOT NULL,
    required_skills VARCHAR(1000) NULL,
    min_experience_years DECIMAL(4,1) NULL,
    max_experience_years DECIMAL(4,1) NULL,
    employment_type VARCHAR(24) NOT NULL,
    salary_min DECIMAL(13,2) NULL,
    salary_max DECIMAL(13,2) NULL,
    hiring_manager_id BIGINT NULL,
    recruiter_id BIGINT NULL,
    opening_date DATE NOT NULL,
    closing_date DATE NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT ck_job_opening_vacancies CHECK (vacancies > 0),
    CONSTRAINT ck_job_opening_experience CHECK (min_experience_years IS NULL OR max_experience_years IS NULL OR max_experience_years >= min_experience_years),
    CONSTRAINT ck_job_opening_salary CHECK (salary_min IS NULL OR salary_max IS NULL OR salary_max >= salary_min),
    CONSTRAINT fk_job_opening_department FOREIGN KEY (department_id) REFERENCES departments (id),
    CONSTRAINT fk_job_opening_designation FOREIGN KEY (designation_id) REFERENCES designations (id),
    CONSTRAINT fk_job_opening_location FOREIGN KEY (location_id) REFERENCES locations (id),
    CONSTRAINT fk_job_opening_manager FOREIGN KEY (hiring_manager_id) REFERENCES employees (id),
    CONSTRAINT fk_job_opening_recruiter FOREIGN KEY (recruiter_id) REFERENCES employees (id),
    INDEX ix_job_openings_status_date (status, opening_date, closing_date),
    INDEX ix_job_openings_department (department_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE candidates (
    id BIGINT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(254) NOT NULL,
    phone VARCHAR(40) NULL,
    resume_url VARCHAR(512) NULL,
    source VARCHAR(80) NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_candidates_email UNIQUE (email),
    INDEX ix_candidates_name (last_name, first_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE job_applications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    opening_id BIGINT NOT NULL,
    candidate_id BIGINT NOT NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'APPLIED',
    applied_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    employee_id BIGINT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_application_candidate_opening UNIQUE (opening_id, candidate_id),
    CONSTRAINT fk_application_opening FOREIGN KEY (opening_id) REFERENCES job_openings (id),
    CONSTRAINT fk_application_candidate FOREIGN KEY (candidate_id) REFERENCES candidates (id),
    CONSTRAINT fk_application_employee FOREIGN KEY (employee_id) REFERENCES employees (id),
    INDEX ix_applications_status (status, applied_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE application_history (
    id BIGINT NOT NULL AUTO_INCREMENT,
    application_id BIGINT NOT NULL,
    from_status VARCHAR(24) NULL,
    to_status VARCHAR(24) NOT NULL,
    note VARCHAR(1000) NULL,
    changed_by_employee_id BIGINT NULL,
    changed_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_application_history_application FOREIGN KEY (application_id) REFERENCES job_applications (id),
    CONSTRAINT fk_application_history_actor FOREIGN KEY (changed_by_employee_id) REFERENCES employees (id),
    INDEX ix_application_history (application_id, changed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE interviews (
    id BIGINT NOT NULL AUTO_INCREMENT,
    application_id BIGINT NOT NULL,
    interviewer_id BIGINT NOT NULL,
    scheduled_at TIMESTAMP(6) NOT NULL,
    duration_minutes SMALLINT NOT NULL,
    interview_type VARCHAR(24) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'SCHEDULED',
    feedback VARCHAR(2000) NULL,
    rating TINYINT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT ck_interview_duration CHECK (duration_minutes BETWEEN 15 AND 480),
    CONSTRAINT ck_interview_rating CHECK (rating IS NULL OR rating BETWEEN 1 AND 5),
    CONSTRAINT fk_interview_application FOREIGN KEY (application_id) REFERENCES job_applications (id),
    CONSTRAINT fk_interview_interviewer FOREIGN KEY (interviewer_id) REFERENCES employees (id),
    INDEX ix_interviews_schedule (interviewer_id, scheduled_at, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE offers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    application_id BIGINT NOT NULL,
    salary DECIMAL(13,2) NOT NULL,
    currency CHAR(3) NOT NULL,
    start_date DATE NOT NULL,
    expires_on DATE NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
    sent_at TIMESTAMP(6) NULL,
    decided_at TIMESTAMP(6) NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_offer_application UNIQUE (application_id),
    CONSTRAINT ck_offer_salary CHECK (salary > 0),
    CONSTRAINT ck_offer_dates CHECK (start_date >= expires_on),
    CONSTRAINT fk_offer_application FOREIGN KEY (application_id) REFERENCES job_applications (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
