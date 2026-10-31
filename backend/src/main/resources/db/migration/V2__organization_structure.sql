CREATE TABLE organization_profile (
    id BIGINT NOT NULL,
    name VARCHAR(160) NOT NULL,
    legal_name VARCHAR(200) NULL,
    work_email VARCHAR(254) NULL,
    phone VARCHAR(40) NULL,
    website VARCHAR(255) NULL,
    logo_url VARCHAR(512) NULL,
    registration_number VARCHAR(100) NULL,
    address_line_1 VARCHAR(200) NULL,
    address_line_2 VARCHAR(200) NULL,
    city VARCHAR(100) NULL,
    state VARCHAR(100) NULL,
    country VARCHAR(100) NULL,
    postal_code VARCHAR(24) NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT ck_organization_singleton CHECK (id = 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE departments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(24) NOT NULL,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_departments_code UNIQUE (code),
    CONSTRAINT uk_departments_name UNIQUE (name),
    INDEX ix_departments_active_name (active, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE designations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    department_id BIGINT NULL,
    code VARCHAR(24) NOT NULL,
    title VARCHAR(120) NOT NULL,
    job_grade VARCHAR(40) NULL,
    description VARCHAR(500) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_designations_code UNIQUE (code),
    CONSTRAINT uk_designations_department_title UNIQUE (department_id, title),
    CONSTRAINT fk_designations_department FOREIGN KEY (department_id) REFERENCES departments (id),
    INDEX ix_designations_active_title (active, title),
    INDEX ix_designations_department (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE locations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(24) NOT NULL,
    name VARCHAR(120) NOT NULL,
    address_line_1 VARCHAR(200) NULL,
    address_line_2 VARCHAR(200) NULL,
    city VARCHAR(100) NULL,
    state VARCHAR(100) NULL,
    country VARCHAR(100) NOT NULL,
    postal_code VARCHAR(24) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_locations_code UNIQUE (code),
    CONSTRAINT uk_locations_name UNIQUE (name),
    INDEX ix_locations_active_name (active, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
