package com.qosb.platform.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Aggregated KPI per (timestamp, network_type).
 * This is the table the LSTM models actually consume.
 */
@Entity
@Table(
    name = "kpi_aggregated",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_kpi_agg_ts_net",
        columnNames = {"timestamp", "network_type"}
    )
)
public class KpiAggregatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

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
    private double signalStrengthDbm;

    @Column(name = "active_users", nullable = false)
    private double activeUsers;

    @Column(name = "was_missing", nullable = false)
    private boolean wasMissing;

    public KpiAggregatedEntity() {}

    // Getters / setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

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

    public double getSignalStrengthDbm() { return signalStrengthDbm; }
    public void setSignalStrengthDbm(double signalStrengthDbm) { this.signalStrengthDbm = signalStrengthDbm; }

    public double getActiveUsers() { return activeUsers; }
    public void setActiveUsers(double activeUsers) { this.activeUsers = activeUsers; }

    public boolean isWasMissing() { return wasMissing; }
    public void setWasMissing(boolean wasMissing) { this.wasMissing = wasMissing; }
}
