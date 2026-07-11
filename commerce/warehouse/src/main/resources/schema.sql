CREATE TABLE IF NOT EXISTS warehouse_product
(
    product_id UUID PRIMARY KEY,
    quantity   BIGINT           NOT NULL,
    fragile    BOOLEAN          NOT NULL,
    weight     DOUBLE PRECISION NOT NULL,
    width      DOUBLE PRECISION NOT NULL,
    height     DOUBLE PRECISION NOT NULL,
    depth      DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP        NOT NULL,
    updated_at TIMESTAMP        NOT NULL
);

CREATE TABLE IF NOT EXISTS warehouse_address
(
    id      BIGSERIAL PRIMARY KEY,
    country VARCHAR(100),
    city    VARCHAR(100),
    street  VARCHAR(200),
    house   VARCHAR(20),
    flat    VARCHAR(20)
);