CREATE TABLE IF NOT EXISTS account (
    id INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT,
    account_name TEXT NOT NULL UNIQUE CHECK (4 <= LENGTH(account_name) AND LENGTH(account_name) <= 50),
    cp_number TEXT NOT NULL UNIQUE CHECK (LENGTH(cp_number) = 11),
    mpin_hashed TEXT NOT NULL,
    balance NUMERIC NOT NULL DEFAULT 0.00,
    account_type TEXT NOT NULL DEFAULT 'BASIC',
    created_at TEXT NOT NULL,
    business_name TEXT CHECK (business_name IS NULL OR (4 <= LENGTH(business_name) AND LENGTH(business_name) <= 50))
);

CREATE TABLE IF NOT EXISTS "transaction" (
    id INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT,
    sender_id INTEGER NOT NULL,
    receiver_id INTEGER NOT NULL,
    amount NUMERIC NOT NULL,
    fee NUMERIC NOT NULL,
    transaction_type TEXT NOT NULL DEFAULT 'SEND',
    created_at TEXT NOT NULL,
    reference_number TEXT NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES account(id),
    FOREIGN KEY (receiver_id) REFERENCES account(id)
);

INSERT INTO account (id, account_name, cp_number, mpin_hashed, balance, account_type, created_at, business_name)
SELECT id, account_name, cp_number, mpin_hashed, balance, account_type, created_at, business_name
FROM accounts;

INSERT INTO "transaction" (id, sender_id, receiver_id, amount, fee, transaction_type, created_at, reference_number)
SELECT id, sender_id, receiver_id, amount, fee, transaction_type, created_at, reference_number
FROM transactions;