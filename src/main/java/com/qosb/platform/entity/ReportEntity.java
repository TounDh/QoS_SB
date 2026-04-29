package com.qosb.platform.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.Map;

/**
 * Alert report produced by the LLM report writer agent.
 * Filled by Step 6 (CrewAI). Empty until then.
 */
@Entity
@Table(name = "reports")
public class ReportEntity {

    @Id
    @Column(name = "id", length = 40)
    private String id;

    @Column(name = "anomaly_id", length = 40)
    private String anomalyId;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "network_type", nullable = false, length = 10)
    private String networkType;

    @Column(name = "severity_label", nullable = false, length = 10)
    private String severityLabel;

    @Column(name = "report_type", nullable = false, length = 30)
    private String reportType;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Type(JsonType.class)
    @Column(name = "metadata", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Type(JsonType.class)
    @Column(name = "evaluation", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> evaluation;

    @Column(name = "generated_at", nullable = false, insertable = false, updatable = false)
    private Instant generatedAt;

    public ReportEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAnomalyId() { return anomalyId; }
    public void setAnomalyId(String anomalyId) { this.anomalyId = anomalyId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getNetworkType() { return networkType; }
    public void setNetworkType(String networkType) { this.networkType = networkType; }

    public String getSeverityLabel() { return severityLabel; }
    public void setSeverityLabel(String severityLabel) { this.severityLabel = severityLabel; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public Map<String, Object> getEvaluation() { return evaluation; }
    public void setEvaluation(Map<String, Object> evaluation) { this.evaluation = evaluation; }

    public Instant getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Instant generatedAt) { this.generatedAt = generatedAt; }
}
