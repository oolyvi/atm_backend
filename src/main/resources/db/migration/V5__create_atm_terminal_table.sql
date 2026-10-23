CREATE TABLE IF NOT EXISTS atm_terminal
(
    id
    BIGINT
    GENERATED
    ALWAYS AS
    IDENTITY
    PRIMARY
    KEY,
    terminal_code
    VARCHAR
(
    50
) NOT NULL UNIQUE,
    location VARCHAR
(
    255
) NOT NULL,
    status VARCHAR
(
    50
) NOT NULL, -- ACTIVE, MAINTENANCE, OFFLINE
    ip_address VARCHAR
(
    45
),
    software_version VARCHAR
(
    50
),
    total_money INT NOT NULL DEFAULT 0,
    cassette_1_count INT NOT NULL DEFAULT 0,
    cassette_5_count INT NOT NULL DEFAULT 0,
    cassette_10_count INT NOT NULL DEFAULT 0,
    cassette_20_count INT NOT NULL DEFAULT 0,
    cassette_50_count INT NOT NULL DEFAULT 0,
    cassette_100_count INT NOT NULL DEFAULT 0,
    cassette_200_count INT NOT NULL DEFAULT 0,
    cassette_500_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );