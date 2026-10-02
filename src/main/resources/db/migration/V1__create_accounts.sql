CREATE TABLE accounts (
    id BINARY(16) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(100),
    created_at DATETIME(6) NOT NULL,

    CONSTRAINT pk_accounts PRIMARY KEY (id),
    CONSTRAINT uk_accounts_email UNIQUE (email)
);