CREATE TABLE product (
    id          UUID PRIMARY KEY,
    event_id    UUID         NOT NULL REFERENCES event (id),
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    price_cents INTEGER      NOT NULL,
    category    VARCHAR(100),
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_product_event ON product (event_id);
