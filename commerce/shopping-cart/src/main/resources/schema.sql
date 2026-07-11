CREATE TABLE IF NOT EXISTS shopping_cart
(
    shopping_cart_id UUID PRIMARY KEY,
    username         VARCHAR(100) NOT NULL,
    active           BOOLEAN      NOT NULL,
    created_at       TIMESTAMP    NOT NULL,
    updated_at       TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS cart_item
(
    cart_item_id     BIGSERIAL PRIMARY KEY,
    shopping_cart_id UUID   NOT NULL,
    product_id       UUID   NOT NULL,
    quantity         BIGINT NOT NULL
);