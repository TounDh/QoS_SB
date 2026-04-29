-- =============================================================
-- V1__initial_schema.sql
-- Initial schema for the QoSB Platform.
--
-- Tables:
--   1. kpi_raw          - raw HF dataset rows (per tower per minute)
--   2. kpi_aggregated   - mean per network type per minute (model input)
--   3. anomalies        - LSTM autoencoder output
--   4. forecasts        - LSTM forecaster output
--   5. allocations      - LLM allocator output (filled in step 6)
--   6. reports          - LLM report writer output (filled in step 6)
--   7. users            - authentication (used in later steps)
-- =============================================================


-- 1. Raw KPI measurements (one row per tower per minute) -------
CREATE TABLE kpi_raw (
    metric_id           VARCHAR(20)   PRIMARY KEY,
    timestamp           TIMESTAMPTZ   NOT NULL,
    tower_id            VARCHAR(20)   NOT NULL,
    city                VARCHAR(50)   NOT NULL,
    operator            VARCHAR(20)   NOT NULL,
    network_type        VARCHAR(10)   NOT NULL,
    latency_ms          DOUBLE PRECISION NOT NULL,
    jitter_ms           DOUBLE PRECISION NOT NULL,
    throughput_mbps     DOUBLE PRECISION NOT NULL,
    packet_loss_rate    DOUBLE PRECISION NOT NULL,
    error_rate          DOUBLE PRECISION NOT NULL,
    signal_strength_dbm INTEGER       NOT NULL,
    active_users        INTEGER       NOT NULL
);

CREATE INDEX idx_kpi_raw_timestamp ON kpi_raw(timestamp);
CREATE INDEX idx_kpi_raw_network   ON kpi_raw(network_type, timestamp);
CREATE INDEX idx_kpi_raw_city_op   ON kpi_raw(city, operator);


-- 2. Aggregated KPIs per network type per minute (model input) -
CREATE TABLE kpi_aggregated (
    id                  BIGSERIAL     PRIMARY KEY,
    timestamp           TIMESTAMPTZ   NOT NULL,
    network_type        VARCHAR(10)   NOT NULL,
    latency_ms          DOUBLE PRECISION NOT NULL,
    jitter_ms           DOUBLE PRECISION NOT NULL,
    throughput_mbps     DOUBLE PRECISION NOT NULL,
    packet_loss_rate    DOUBLE PRECISION NOT NULL,
    error_rate          DOUBLE PRECISION NOT NULL,
    signal_strength_dbm DOUBLE PRECISION NOT NULL,
    active_users        DOUBLE PRECISION NOT NULL,
    was_missing         BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_kpi_agg_ts_net UNIQUE (timestamp, network_type)
);

CREATE INDEX idx_kpi_agg_network_ts ON kpi_aggregated(network_type, timestamp);


-- 3. Anomalies (output of LSTM autoencoder) -------------------
CREATE TABLE anomalies (
    id                   VARCHAR(40)   PRIMARY KEY,
    timestamp            TIMESTAMPTZ   NOT NULL,
    network_type         VARCHAR(10)   NOT NULL,
    reconstruction_error DOUBLE PRECISION NOT NULL,
    threshold            DOUBLE PRECISION NOT NULL,
    severity_score       DOUBLE PRECISION NOT NULL,
    severity_label       VARCHAR(10)   NOT NULL,
    is_anomaly           BOOLEAN       NOT NULL,
    latency_ms           DOUBLE PRECISION NOT NULL,
    jitter_ms            DOUBLE PRECISION NOT NULL,
    throughput_mbps      DOUBLE PRECISION NOT NULL,
    packet_loss_rate     DOUBLE PRECISION NOT NULL,
    error_rate           DOUBLE PRECISION NOT NULL,
    signal_strength_dbm  DOUBLE PRECISION NOT NULL,
    active_users         INTEGER       NOT NULL,
    CONSTRAINT chk_severity_label CHECK (severity_label IN ('none','low','medium','high','critical'))
);

CREATE INDEX idx_anom_timestamp  ON anomalies(timestamp DESC);
CREATE INDEX idx_anom_is_anomaly ON anomalies(is_anomaly) WHERE is_anomaly = TRUE;
CREATE INDEX idx_anom_network    ON anomalies(network_type, timestamp DESC);
CREATE INDEX idx_anom_severity   ON anomalies(severity_label) WHERE is_anomaly = TRUE;


-- 4. Forecasts (output of LSTM forecaster) --------------------
CREATE TABLE forecasts (
    id              BIGSERIAL     PRIMARY KEY,
    timestamp       TIMESTAMPTZ   NOT NULL,
    network_type    VARCHAR(10)   NOT NULL,
    metric          VARCHAR(30)   NOT NULL,
    actual          DOUBLE PRECISION,
    predicted       DOUBLE PRECISION NOT NULL,
    generated_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_forecast UNIQUE (timestamp, network_type, metric, generated_at)
);

CREATE INDEX idx_forecast_lookup ON forecasts(network_type, metric, timestamp);


-- 5. Allocation recommendations (output of allocator agent) ---
CREATE TABLE allocations (
    recommendation_id     VARCHAR(40)   PRIMARY KEY,
    anomaly_id            VARCHAR(40),
    timestamp             TIMESTAMPTZ   NOT NULL,
    target_entity         VARCHAR(50)   NOT NULL,
    network_type          VARCHAR(10)   NOT NULL,
    recommended_action    TEXT          NOT NULL,
    priority              VARCHAR(10)   NOT NULL,
    confidence            DOUBLE PRECISION NOT NULL,
    issue_summary         TEXT,
    root_cause_hypothesis TEXT,
    expected_impact       TEXT,
    follow_up_actions     JSONB,
    parse_status          VARCHAR(20)   NOT NULL,
    model_name            VARCHAR(80)   NOT NULL,
    generated_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_priority     CHECK (priority IN ('low','medium','high','critical')),
    CONSTRAINT chk_parse_status CHECK (parse_status IN ('parsed','fallback')),
    CONSTRAINT chk_confidence   CHECK (confidence BETWEEN 0 AND 1),
    CONSTRAINT fk_alloc_anomaly FOREIGN KEY (anomaly_id) REFERENCES anomalies(id) ON DELETE SET NULL
);

CREATE INDEX idx_alloc_anomaly  ON allocations(anomaly_id);
CREATE INDEX idx_alloc_priority ON allocations(priority);
CREATE INDEX idx_alloc_network  ON allocations(network_type);


-- 6. Alert reports (output of report writer agent) ------------
CREATE TABLE reports (
    id             VARCHAR(40)   PRIMARY KEY,
    anomaly_id     VARCHAR(40),
    timestamp      TIMESTAMPTZ   NOT NULL,
    network_type   VARCHAR(10)   NOT NULL,
    severity_label VARCHAR(10)   NOT NULL,
    report_type    VARCHAR(30)   NOT NULL DEFAULT 'real_time_alert',
    content        TEXT          NOT NULL,
    metadata       JSONB         NOT NULL,
    evaluation     JSONB         NOT NULL,
    generated_at   TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_report_anomaly FOREIGN KEY (anomaly_id) REFERENCES anomalies(id) ON DELETE SET NULL
);

CREATE INDEX idx_report_anomaly ON reports(anomaly_id);
CREATE INDEX idx_report_network ON reports(network_type, timestamp DESC);


-- 7. Users (authentication, used in later steps) --------------
CREATE TABLE users (
    id             BIGSERIAL     PRIMARY KEY,
    email          VARCHAR(120)  NOT NULL UNIQUE,
    password_hash  VARCHAR(120)  NOT NULL,
    full_name      VARCHAR(120),
    role           VARCHAR(20)   NOT NULL DEFAULT 'OPERATOR',
    is_active      BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    last_login_at  TIMESTAMPTZ,
    CONSTRAINT chk_role CHECK (role IN ('ADMIN','OPERATOR','VIEWER'))
);

CREATE INDEX idx_users_email ON users(email);
