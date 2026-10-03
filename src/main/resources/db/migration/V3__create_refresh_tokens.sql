CREATE TABLE refresh_tokens (
    id BINARY(16) NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    account_id BINARY(16) NOT NULL,
    created_at DATETIME(6) NOT NULL,

    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    CONSTRAINT uk_refresh_tokens_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_refresh_tokens_account
        FOREIGN KEY (account_id)
        REFERENCES accounts (id)
        ON DELETE CASCADE
);

CREATE INDEX ix_refresh_tokens_account_id
    ON refresh_tokens (account_id);