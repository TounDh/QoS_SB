package com.qosb.platform.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Raw KPI measurement (one row per tower per minute).
 * Mirrors the Hugging Face dataset rows verbatim.
 */
@Entity
@Table(name = "kpi_raw")
public class KpiRawEntity {

    @Id
    @Column(name = "metric_id", length = 20)
    private String metricId;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "tower_id", nullable = false, length = 20)
    private String towerId;

    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @Column(name = "operator", nullable = false, length = 20)
    private String operator;

    @Column(name = "network_type", nullable = false, length = 10)
    private String networkType;

    @Column(name = "latency_ms", nullable = false)
    private double latencyMs;

    @Column(name = "jitter_ms", nullable = false)
    private double jitterMs;

    @Column(name = "throughput_mbps", nullable = false)
    private double throughputMbps;

    @Column(name = "packet_loss_rate", nullable = false)
    private double packetLossRate;

    @Column(name = "error_rate", nullable = false)
    private double errorRate;

    @Column(name = "signal_strength_dbm", nullable = false)
    private int signalStrengthDbm;

    @Column(name = "active_users", nullable = false)
    private int activeUsers;

    public KpiRawEntity() {}

    // Getters / setters

    public String getMetricId() { return metricId; }
    public void setMetricId(String metricId) { this.metricId = metricId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getTowerId() { return towerId; }
    public void setTowerId(String towerId) { this.towerId = towerId; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }

    public String getNetworkType() { return networkType; }
    public void setNetworkType(String networkType) { this.networkType = networkType; }

    public double getLatencyMs() { return latencyMs; }
    public void setLatencyMs(double latencyMs) { this.latencyMs = latencyMs; }

    public double getJitterMs() { return jitterMs; }
    public void setJitterMs(double jitterMs) { this.jitterMs = jitterMs; }

    public double getThroughputMbps() { return throughputMbps; }
    public void setThroughputMbps(double throughputMbps) { this.throughputMbps = throughputMbps; }

    public double getPacketLossRate() { return packetLossRate; }
    public void setPacketLossRate(double packetLossRate) { this.packetLossRate = packetLossRate; }

    public double getErrorRate() { return errorRate; }
    public void setErrorRate(double errorRate) { this.errorRate = errorRate; }

    public int getSignalStrengthDbm() { return signalStrengthDbm; }
    public void setSignalStrengthDbm(int signalStrengthDbm) { this.signalStrengthDbm = signalStrengthDbm; }

    public int getActiveUsers() { return activeUsers; }
    public void setActiveUsers(int activeUsers) { this.activeUsers = activeUsers; }
}
