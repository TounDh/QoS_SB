package com.qosb.platform.mapper;

import com.qosb.platform.dto.*;
import com.qosb.platform.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Single source of truth for entity -> DTO conversions.
 * Keeps entities private to the persistence layer; DTOs are what controllers expose.
 */
@Component
public class EntityDtoMapper {

    public KpiPoint toKpiPoint(KpiAggregatedEntity e) {
        return new KpiPoint(
                e.getTimestamp(),
                e.getNetworkType(),
                e.getLatencyMs(),
                e.getJitterMs(),
                e.getThroughputMbps(),
                e.getPacketLossRate(),
                e.getErrorRate(),
                e.getSignalStrengthDbm(),
                (int) Math.round(e.getActiveUsers())
        );
    }

    public ForecastPoint toForecastPoint(ForecastEntity e) {
        return new ForecastPoint(
                e.getTimestamp(),
                e.getNetworkType(),
                e.getMetric(),
                e.getActual(),
                e.getPredicted()
        );
    }

    public AnomalyRecord toAnomalyRecord(AnomalyEntity e) {
        return new AnomalyRecord(
                e.getId(),
                e.getTimestamp(),
                e.getNetworkType(),
                e.getReconstructionError(),
                e.getThreshold(),
                e.getSeverityScore(),
                e.getSeverityLabel(),
                e.isAnomaly(),
                e.getLatencyMs(),
                e.getJitterMs(),
                e.getThroughputMbps(),
                e.getPacketLossRate(),
                e.getErrorRate(),
                e.getSignalStrengthDbm(),
                e.getActiveUsers()
        );
    }

    public AllocationRecommendation toAllocation(AllocationEntity e) {
        List<String> followUps = e.getFollowUpActions();
        return new AllocationRecommendation(
                e.getRecommendationId(),
                e.getTimestamp(),
                e.getAnomalyId(),
                e.getTargetEntity(),
                e.getNetworkType(),
                e.getRecommendedAction(),
                e.getPriority(),
                e.getConfidence(),
                e.getIssueSummary(),
                e.getRootCauseHypothesis(),
                e.getExpectedImpact(),
                followUps == null ? List.of() : followUps,
                e.getParseStatus(),
                e.getModelName(),
                e.getGeneratedAt()
        );
    }

    @SuppressWarnings("unchecked")
    public AlertReport toReport(ReportEntity e) {
        Map<String, Object> meta = e.getMetadata() == null ? Map.of() : e.getMetadata();
        Map<String, Object> eval = e.getEvaluation() == null ? Map.of() : e.getEvaluation();

        AlertReport.ReportMetadata metadata = new AlertReport.ReportMetadata(
                asString(meta.get("escalationFlag")),
                asString(meta.get("qualityHint")),
                asInt(meta.get("charCount")),
                asInt(meta.get("wordCount"))
        );

        Map<String, Boolean> sectionPresence =
                (Map<String, Boolean>) eval.getOrDefault("sectionPresence", Map.of());

        AlertReport.ReportEvaluation evaluation = new AlertReport.ReportEvaluation(
                asString(eval.get("status")),
                asDouble(eval.get("finalQualityScore")),
                asDouble(eval.get("accuracyScore")),
                asDouble(eval.get("coverageScore")),
                asDouble(eval.get("completenessScore")),
                asDouble(eval.get("hallucinationRate")),
                sectionPresence
        );

        return new AlertReport(
                e.getId(),
                e.getAnomalyId(),
                e.getTimestamp(),
                e.getNetworkType(),
                e.getSeverityLabel(),
                e.getReportType(),
                e.getContent(),
                metadata,
                evaluation,
                e.getGeneratedAt()
        );
    }

    private static String asString(Object o) { return o == null ? null : o.toString(); }
    private static int asInt(Object o)       { return o instanceof Number n ? n.intValue()    : 0; }
    private static double asDouble(Object o) { return o instanceof Number n ? n.doubleValue() : 0.0; }
}
