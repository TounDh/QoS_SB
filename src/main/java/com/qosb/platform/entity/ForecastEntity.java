package com.qosb.platform.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Forecast point produced by the LSTM forecaster.
 * One row per (timestamp, network_type, metric, generated_at).
 */
@Entity
@Table(
    name = "forecasts",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_forecast",
        columnNames = {"timestamp", "network_type", "metric", "generated_at"}
    )
)
public class ForecastEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "network_type", nullable = false, length = 10)
    private String networkType;

    @Column(name = "metric", nullable = false, length = 30)
    private String metric;

    @Column(name = "actual")
    private Double actual;

    @Column(name = "predicted", nullable = false)
    private double predicted;

    @Column(name = "generated_at", nullable = false, insertable = false, updatable = false)
    private Instant generatedAt;

    public ForecastEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getNetworkType() { return networkType; }
    public void setNetworkType(String networkType) { this.networkType = networkType; }

    public String getMetric() { return metric; }
    public void setMetric(String metric) { this.metric = metric; }

    public Double getActual() { return actual; }
    public void setActual(Double actual) { this.actual = actual; }

    public double getPredicted() { return predicted; }
    public void setPredicted(double predicted) { this.predicted = predicted; }

    public Instant getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Instant generatedAt) { this.generatedAt = generatedAt; }
}
