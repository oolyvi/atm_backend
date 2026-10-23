CREATE TABLE cash
(
    id            BIGSERIAL PRIMARY KEY,
    atm_id        BIGINT NOT NULL,
    denomination  INT    NOT NULL, -- e.g., 1, 5, 10, 20, 50, 100, 200, 500
    quantity      INT    NOT NULL,
    low_threshold INT    NOT NULL DEFAULT 10,
    CONSTRAINT fk_atm_cash
        FOREIGN KEY (atm_id)
            REFERENCES atm_terminal (id)
);