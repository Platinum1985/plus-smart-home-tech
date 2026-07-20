CREATE TABLE IF NOT EXISTS deliveries
(
    id              UUID PRIMARY KEY,
    order_id        UUID        NOT NULL UNIQUE,
    from_address    TEXT        NOT NULL,
    to_address      TEXT        NOT NULL,
    delivery_state  VARCHAR(50) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);