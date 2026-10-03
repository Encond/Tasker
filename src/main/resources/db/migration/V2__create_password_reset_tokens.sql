CREATE TABLE password_reset_tokens (
    id BINARY(16) NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    is_used BOOLEAN NOT NULL,
    account_id BINARY(16) NOT NULL,

    CONSTRAINT pk_password_reset_tokens PRIMARY KEY (id),
    CONSTRAINT uk_password_reset_tokens_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_password_reset_tokens_account
        FOREIGN KEY (account_id)
        REFERENCES accounts (id)
        ON DELETE CASCADE
);

CREATE INDEX ix_password_reset_tokens_account_id
    ON password_reset_tokens (account_id);