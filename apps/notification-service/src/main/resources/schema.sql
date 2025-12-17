-- Notification Table (R2DBC compatible schema)
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    channel VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP,
    metadata TEXT
);

-- Index for faster queries
CREATE INDEX IF NOT EXISTS idx_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_user_read ON notifications(user_id, is_read);
CREATE INDEX IF NOT EXISTS idx_created_at ON notifications(created_at);
