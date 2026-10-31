CREATE TABLE announcements (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(180) NOT NULL,
    body VARCHAR(5000) NOT NULL,
    department_id BIGINT NULL,
    priority VARCHAR(16) NOT NULL DEFAULT 'NORMAL',
    status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
    published_at TIMESTAMP NULL,
    expires_at TIMESTAMP NULL,
    created_by_email VARCHAR(254) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_announcements_department FOREIGN KEY (department_id) REFERENCES departments(id),
    CONSTRAINT ck_announcement_priority CHECK (priority IN ('LOW','NORMAL','HIGH')),
    CONSTRAINT ck_announcement_status CHECK (status IN ('DRAFT','PUBLISHED')),
    INDEX idx_announcement_audience (status, department_id, published_at, expires_at)
);
