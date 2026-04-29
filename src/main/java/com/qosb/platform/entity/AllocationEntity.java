package com.qosb.platform.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.List;

/**
 * Allocation recommendation produced by the LLM allocator agent.
 * Filled by Step 6 (CrewAI). Empty until then.
 */
@Entity
@Table(name = "allocations")
public class AllocationEntity {

    @Id
    @Column(name = "recommendation_id", length = 40)
    private String recommendationId;

    @Column(name = "anomaly_id", length = 40)
    private String anomalyId;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "target_entity", nullable = false, length = 50)
    private String targetEntity;

    @Column(name = "network_type", nullable = false, length = 10)
    private String networkType;

    @Column(name = "recommended_action", nullable = false, columnDefinition = "TEXT")
    private String recommendedAction;

    @Column(name = "priority", nullable = false, length = 10)
    private String priority;

    @Column(name = "confidence", nullable = false)
    private double confidence;

    @Column(name = "issue_summary", columnDefinition = "TEXT")
    private String issueSummary;

    @Column(name = "root_cause_hypothesis", columnDefinition = "TEXT")
    private String rootCauseHypothesis;

    @Column(name = "expected_impact", columnDefinition = "TEXT")
    private String expectedImpact;

    @Type(JsonType.class)
    @Column(name = "follow_up_actions", columnDefinition = "jsonb")
    private List<String> followUpActions;

    @Column(name = "parse_status", nullable = false, length = 20)
    private String parseStatus;

    @Column(name = "model_name", nullable = false, length = 80)
    private String modelName;

    @Column(name = "generated_at", nullable = false, insertable = false, updatable = false)
    private Instant generatedAt;

    public AllocationEntity() {}

    public String getRecommendationId() { return recommendationId; }
    public void setRecommendationId(String recommendationId) { this.recommendationId = recommendationId; }

    public String getAnomalyId() { return anomalyId; }
    public void setAnomalyId(String anomalyId) { this.anomalyId = anomalyId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getTargetEntity() { return targetEntity; }
    public void setTargetEntity(String targetEntity) { this.targetEntity = targetEntity; }

    public String getNetworkType() { return networkType; }
    public void setNetworkType(String networkType) { this.networkType = networkType; }

    public String getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public String getIssueSummary() { return issueSummary; }
    public void setIssueSummary(String issueSummary) { this.issueSummary = issueSummary; }

    public String getRootCauseHypothesis() { return rootCauseHypothesis; }
    public void setRootCauseHypothesis(String rootCauseHypothesis) { this.rootCauseHypothesis = rootCauseHypothesis; }

    public String getExpectedImpact() { return expectedImpact; }
    public void setExpectedImpact(String expectedImpact) { this.expectedImpact = expectedImpact; }

    public List<String> getFollowUpActions() { return followUpActions; }
    public void setFollowUpActions(List<String> followUpActions) { this.followUpActions = followUpActions; }

    public String getParseStatus() { return parseStatus; }
    public void setParseStatus(String parseStatus) { this.parseStatus = parseStatus; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public Instant getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Instant generatedAt) { this.generatedAt = generatedAt; }
}
