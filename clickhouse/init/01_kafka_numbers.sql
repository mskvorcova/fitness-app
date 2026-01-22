CREATE DATABASE IF NOT EXISTS analytics;

CREATE TABLE IF NOT EXISTS analytics.kafka_numbers
(
    value Int32
)
    ENGINE = Kafka
    SETTINGS kafka_broker_list = 'kafka:9092',
    kafka_topic_list = 'numbers',
    kafka_group_name = 'clickhouse_numbers',
    kafka_format = 'CSV',
    kafka_row_delimiter = '\n',
    kafka_handle_error_mode = 'stream';

CREATE TABLE IF NOT EXISTS analytics.numbers
(
    value Int32,
    created_at DateTime DEFAULT now()
    )
    ENGINE = MergeTree
    ORDER BY created_at;

CREATE MATERIALIZED VIEW IF NOT EXISTS analytics.mv_numbers
TO analytics.numbers AS
SELECT value, now() AS created_at
FROM analytics.kafka_numbers;

CREATE TABLE IF NOT EXISTS analytics.numbers_dlq
(
    raw String,
    error String,
    created_at DateTime DEFAULT now()
    )
    ENGINE = MergeTree
    ORDER BY created_at;

CREATE MATERIALIZED VIEW IF NOT EXISTS analytics.mv_numbers_dlq
TO analytics.numbers_dlq AS
SELECT _raw_message AS raw, _error AS error, now() AS created_at
FROM analytics.kafka_numbers
WHERE _error != '';

CREATE TABLE IF NOT EXISTS analytics.numbers_sums
(
    id UInt8,
    sum_positive Int64,
    sum_negative Int64
)
    ENGINE = SummingMergeTree
    ORDER BY id;

CREATE MATERIALIZED VIEW IF NOT EXISTS analytics.mv_numbers_sums
TO analytics.numbers_sums AS
SELECT
    1 AS id,
    sumIf(toInt64(value), value > 0) AS sum_positive,
    sumIf(toInt64(value), value < 0) AS sum_negative
FROM analytics.numbers
GROUP BY id;
