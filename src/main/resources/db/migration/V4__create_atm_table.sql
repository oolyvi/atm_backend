CREATE TABLE atm
(
    id              BIGSERIAL PRIMARY KEY,
    idempotency_key VARCHAR(100) UNIQUE NOT NULL,
    type            VARCHAR(20)         NOT NULL,
    amount          NUMERIC(19, 2)      NOT NULL,
    account_id      BIGINT,
    timestamp       TIMESTAMP           NOT NULL,
    CONSTRAINT fk_atm_transaction_account
        FOREIGN KEY (account_id)
            REFERENCES account (id)
            ON DELETE SET NULL
);