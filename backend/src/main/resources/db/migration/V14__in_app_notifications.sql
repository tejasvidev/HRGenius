CREATE TABLE in_app_notifications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    announcement_id BIGINT NULL,
    title VARCHAR(180) NOT NULL,
    body VARCHAR(1000) NOT NULL,
    notification_type VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_notification_employee_announcement UNIQUE (employee_id, announcement_id),
    CONSTRAINT fk_notification_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT fk_notification_announcement FOREIGN KEY (announcement_id) REFERENCES announcements(id),
    INDEX idx_notification_employee_read (employee_id, read_at, created_at)
);
