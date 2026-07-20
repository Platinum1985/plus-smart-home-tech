CREATE TABLE IF NOT EXISTS orders
(
    id               UUID PRIMARY KEY,
    username         VARCHAR(255) NOT NULL,
    shopping_cart_id UUID,
    payment_id       UUID,
    delivery_id      UUID,
    order_state      VARCHAR(50)  NOT NULL,
    delivery_weight  DOUBLE PRECISION,
    delivery_volume  DOUBLE PRECISION,
    fragile          BOOLEAN,
    total_price      DOUBLE PRECISION,
    delivery_price   DOUBLE PRECISION,
    product_price    DOUBLE PRECISION,
    delivery_address TEXT,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица товаров в заказе
CREATE TABLE IF NOT EXISTS order_products
(
    id         BIGINT PRIMARY KEY,
    order_id   UUID   NOT NULL,
    product_id UUID   NOT NULL,
    quantity   BIGINT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);