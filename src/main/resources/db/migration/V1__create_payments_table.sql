CREATE TABLE payments
(
    id              BIGSERIAL PRIMARY KEY,
    amount          NUMERIC(38, 2),
    currency        VARCHAR(255),
    status          VARCHAR(255) CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED')),
    created_at      TIMESTAMP(6),
    idempotency_key VARCHAR(255) UNIQUE
);
