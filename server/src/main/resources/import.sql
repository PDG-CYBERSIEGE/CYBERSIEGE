-- This file allow to write SQL commands that will be emitted in test and dev.

-- Create users table
CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  username VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL
);

-- Reset sequence
ALTER SEQUENCE users_id_seq RESTART WITH 1;