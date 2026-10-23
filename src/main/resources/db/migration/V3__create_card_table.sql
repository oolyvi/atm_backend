CREATE TABLE card
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    card_number VARCHAR(30)  NOT NULL UNIQUE,
    pin_hash    VARCHAR(255) NOT NULL,
    card_type   VARCHAR(20)  NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    customer_id BIGINT       NOT NULL,
    account_id  BIGINT       NOT NULL,
    CONSTRAINT fk_card_customer
        FOREIGN KEY (customer_id)
            REFERENCES customer (id),
    CONSTRAINT fk_card_account
        FOREIGN KEY (account_id)
            REFERENCES account (id)
            ON DELETE CASCADE
);