CREATE TABLE account
(
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    account_number VARCHAR(30)    NOT NULL UNIQUE,
    balance        NUMERIC(15, 2) NOT NULL DEFAULT 0,
    daily_limit    NUMERIC(15, 2) NOT NULL DEFAULT 1000.00,
    account_type   VARCHAR(20)    NOT NULL,
    customer_id    BIGINT         NOT NULL,
    status         VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE'
        CHECK (status IN ('ACTIVE', 'BLOCKED', 'CLOSED')),
    CONSTRAINT fk_account_customer
        FOREIGN KEY (customer_id)
            REFERENCES customer (id)
);