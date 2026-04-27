package com.qosb.platform.dto;

import java.time.Instant;
import java.util.Map;

public record AlertReport(
        String id,
        String anomalyId,
        Instant timestamp,
        String networkType,
        String severityLabel,
        String reportType, // real_time_alert
        String content,
        ReportMetadata metadata,
        ReportEvaluation evaluation,
        Instant generatedAt
) {
    public record ReportMetadata(
            String escalationFlag,
            String qualityHint,
            int charCount,
            int wordCount
    ) {}

    public record ReportEvaluation(
            String status,
            double finalQualityScore,
            double accuracyScore,
            double coverageScore,
            double completenessScore,
            double hallucinationRate,
            Map<String, Boolean> sectionPresence
    ) {}
}
