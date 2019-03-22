CREATE DATABASE irma;
\connect irma;
CREATE SCHEMA irma;

CREATE USER irma WITH ENCRYPTED PASSWORD 'changeme';
GRANT USAGE ON SCHEMA irma TO irma;

-- DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS irma.users
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
CREATE UNIQUE INDEX username_index ON irma.users (username);
GRANT ALL PRIVILEGES ON TABLE irma.users TO irma;

-- DROP TABLE IF EXISTS email_addresses;
CREATE TABLE IF NOT EXISTS irma.email_addresses
(
    id serial PRIMARY KEY,
    user_id int,
    emailAddress varchar(128)
);
CREATE INDEX emailAddress_index ON irma.email_addresses (emailAddress);
GRANT ALL PRIVILEGES ON TABLE irma.email_addresses TO irma;

-- DROP TABLE IF EXISTS log_entry_records;
CREATE TABLE IF NOT EXISTS irma.log_entry_records
(
    id serial PRIMARY KEY,
    time bigint,
    event varchar(256),
    param int,
    user_id int
);
GRANT ALL PRIVILEGES ON TABLE irma.log_entry_records TO irma;

-- DROP TABLE IF EXISTS email_verification_records;
CREATE TABLE IF NOT EXISTS irma.email_verification_records
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
GRANT ALL PRIVILEGES ON TABLE irma.email_verification_records TO irma;

GRANT ALL ON ALL SEQUENCES IN SCHEMA irma TO irma;
