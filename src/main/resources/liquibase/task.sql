- liquibase formatted sql
--changeSet bot:1
CREATE TABLE notification_task
(
    id                SERIAL PRIMARY KEY,
    chat_id           INTEGER,
    text_notification TEXT,
    date              TIMESTAMP
);