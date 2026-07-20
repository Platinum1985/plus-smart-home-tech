CREATE TABLE IF NOT EXISTS product
(
    product_id       UUID PRIMARY KEY,
    product_name     VARCHAR(255)   NOT NULL,
    description      TEXT           NOT NULL,
    image_src        VARCHAR(512),
    quantity_state   VARCHAR(10)    NOT NULL,
    product_state    VARCHAR(10)    NOT NULL,
    product_category VARCHAR(20)    NOT NULL,
    price            DECIMAL(12, 2) NOT NULL,
    created_at       TIMESTAMP      NOT NULL,
    updated_at       TIMESTAMP      NOT NULL
);