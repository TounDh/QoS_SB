package com.qosb.platform.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Anomaly produced by the LSTM autoencoder.
 */
@Entity
@Table(name = "anomalies")
public class AnomalyEntity {

    @Id
    @Column(name = "id", length = 40)
    private String id;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "network_type", nullable = false, length = 10)
    private String networkType;

    @Column(name = "reconstruction_error", nullable = false)
    private double reconstructionError;

    @Column(name = "threshold", nullable = false)
    private double threshold;

    @Column(name = "severity_score", nullable = false)
    private double severityScore;

    @Column(name = "severity_label", nullable = false, length = 10)
    private String severityLabel;

    @Column(name = "is_anomaly", nullable = false)
    private boolean isAnomaly;

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
    private int activeUsers;

    public AnomalyEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getNetworkType() { return networkType; }
    public void setNetworkType(String networkType) { this.networkType = networkType; }

    public double getReconstructionError() { return reconstructionError; }
    public void setReconstructionError(double reconstructionError) { this.reconstructionError = reconstructionError; }

    public double getThreshold() { return threshold; }
    public void setThreshold(double threshold) { this.threshold = threshold; }

    public double getSeverityScore() { return severityScore; }
    public void setSeverityScore(double severityScore) { this.severityScore = severityScore; }

    public String getSeverityLabel() { return severityLabel; }
    public void setSeverityLabel(String severityLabel) { this.severityLabel = severityLabel; }

    public boolean isAnomaly() { return isAnomaly; }
    public void setAnomaly(boolean anomaly) { isAnomaly = anomaly; }

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

    public int getActiveUsers() { return activeUsers; }
    public void setActiveUsers(int activeUsers) { this.activeUsers = activeUsers; }
}
