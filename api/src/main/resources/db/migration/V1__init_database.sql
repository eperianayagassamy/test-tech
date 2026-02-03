CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          label VARCHAR(255) NOT NULL
);

CREATE TABLE offers (
                        id BIGSERIAL PRIMARY KEY,
                        price DECIMAL(19, 2),
                        discount_percent INTEGER NOT NULL,
                        product_id BIGINT NOT NULL,
                        stock_qty INTEGER NOT NULL DEFAULT 0,
                        state VARCHAR(50) NOT NULL,
                        CONSTRAINT check_discount_range CHECK (discount_percent BETWEEN 0 AND 100),
                        CONSTRAINT fk_product_offer FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE TABLE carts (
                       id BIGSERIAL PRIMARY KEY,
                       user_id BIGINT NOT NULL
);

CREATE TABLE cart_lines (
                            id BIGSERIAL PRIMARY KEY,
                            cart_id BIGINT NOT NULL,
                            product_id BIGINT NOT NULL,
                            offer_id BIGINT NOT NULL,
                            quantity INTEGER NOT NULL,
                            CONSTRAINT uk_cart_product_offer UNIQUE (cart_id, product_id, offer_id),
                            CONSTRAINT check_positive_quantity CHECK (quantity > 0),
                            CONSTRAINT fk_line_cart FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
                            CONSTRAINT fk_line_product FOREIGN KEY (product_id) REFERENCES products(id),
                            CONSTRAINT fk_line_offer FOREIGN KEY (offer_id) REFERENCES offers(id)
);

CREATE INDEX idx_offers_product_id ON offers(product_id);
CREATE INDEX idx_cart_lines_cart_id ON cart_lines(cart_id);