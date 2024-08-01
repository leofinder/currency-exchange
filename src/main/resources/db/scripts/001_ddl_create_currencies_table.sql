CREATE TABLE IF NOT EXISTS currencies (
    id INTEGER PRIMARY KEY,
    code VARCHAR(3) UNIQUE,
    full_name VARCHAR(50),
    sign VARCHAR(3)
);
