-- noinspection SqlNoDataSourceInspectionForFile
-- noinspection SqlDialectInspectionForFile

CREATE TABLE IF NOT EXISTS users (
  id         BIGSERIAL PRIMARY KEY,
  login      VARCHAR(30)  NOT NULL,
  email      VARCHAR(30)  NOT NULL,
  password   VARCHAR(30) NOT NULL
);