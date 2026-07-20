CREATE TABLE IF NOT EXISTS payments
(
    id                  UUID PRIMARY KEY,
    order_id            UUID        NOT NULL UNIQUE,
    payment_state       VARCHAR(50) NOT NULL,
    total_payment       DOUBLE PRECISION,
    delivery_total      DOUBLE PRECISION,
    fee_total           DOUBLE PRECISION,
    product_total       DOUBLE PRECISION,
    payment_description TEXT,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);