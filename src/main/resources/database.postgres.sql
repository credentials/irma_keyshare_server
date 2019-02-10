CREATE DATABASE irma;
CREATE USER irma WITH ENCRYPTED PASSWORD 'changeme';

-- DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users
(
    id serial PRIMARY KEY,
    username varchar(128),
    password varchar(256),
    sessionToken varchar(256),
    lastSeen bigint,
    pin varchar(256),
    pinCounter int,
    pinBlockDate bigint,
    keyshare varchar(256),
    publicKey varchar(4096),
    enrolled boolean,
    enabled boolean,
    email_issued boolean,
    language varchar(256)
);
CREATE UNIQUE INDEX username_index ON users (username);
GRANT SELECT, INSERT, UPDATE, DELETE ON users TO irma;

-- DROP TABLE IF EXISTS email_addresses;
CREATE TABLE IF NOT EXISTS email_addresses
(
    id serial PRIMARY KEY,
    user_id int,
    emailAddress varchar(128)
);
CREATE UNIQUE INDEX emailAddress_index ON email_addresses (emailAddress);
GRANT SELECT, INSERT, UPDATE, DELETE ON email_addresses TO irma;

-- DROP TABLE IF EXISTS log_entry_records;
CREATE TABLE IF NOT EXISTS log_entry_records
(
    id serial PRIMARY KEY,
    time bigint,
    event varchar(256),
    param int,
    user_id int
);
GRANT SELECT, INSERT, UPDATE, DELETE ON log_entry_records TO irma;

-- DROP TABLE IF EXISTS email_verification_records;
CREATE TABLE IF NOT EXISTS email_verification_records
(
    id serial PRIMARY KEY,
    email varchar(256) NOT NULL,
    token varchar(64) NOT NULL,
    timeout int NOT NULL,
    validity int NOT NULL,
    time_created bigint NOT NULL,
    time_verified bigint,
    user_id int
);
GRANT SELECT, INSERT, UPDATE, DELETE ON email_verification_records TO irma;
