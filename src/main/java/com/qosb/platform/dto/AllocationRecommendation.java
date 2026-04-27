package com.qosb.platform.dto;

import java.time.Instant;
import java.util.List;

public record AllocationRecommendation(
        String recommendationId,
        Instant timestamp,
        String anomalyId,
        String targetEntity,
        String networkType,
        String recommendedAction,
        String priority, // low | medium | high | critical
        double confidence,
        String issueSummary,
        String rootCauseHypothesis,
        String expectedImpact,
        List<String> followUpActions,
        String parseStatus, // parsed | fallback
        String modelName,
        Instant generatedAt
) {}
