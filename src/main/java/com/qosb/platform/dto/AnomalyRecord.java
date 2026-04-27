package com.qosb.platform.dto;

import java.time.Instant;

public record AnomalyRecord(
        String id,
        Instant timestamp,
        String networkType,
        double reconstructionError,
        double threshold,
        double severityScore,
        String severityLabel, // none | low | medium | high | critical
        boolean isAnomaly,
        double latencyMs,
        double jitterMs,
        double throughputMbps,
        double packetLossRate,
        double errorRate,
        double signalStrengthDbm,
        int activeUsers
) {}
