CREATE TABLE transaction_logs (
    id  BIGSERIAL PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    previous_status VARCHAR(255) CHECK (previous_status IN ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED')),
    new_status VARCHAR(255) CHECK (new_status IN ('PENDING','SUCCESS','FAILED','REFUNDED')),
    changed_at TIMESTAMP(6),
    FOREIGN KEY (payment_id) REFERENCES payments(id)
);