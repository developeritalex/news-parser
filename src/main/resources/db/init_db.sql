CREATE DATABASE news;

CREATE SCHEMA IF NOT EXISTS news_schema;
CREATE TABLE IF NOT EXISTS newz
(
    id       BIGSERIAL PRIMARY KEY,
    time     TIMESTAMP NOT NULL,
    keywords TEXT      NOT NULL,
    text     TEXT      NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS outbox
(
    id       BIGSERIAL PRIMARY KEY,
    time     TIMESTAMP NOT NULL,
    keywords TEXT      NOT NULL,
    text     TEXT      NOT NULL UNIQUE
);