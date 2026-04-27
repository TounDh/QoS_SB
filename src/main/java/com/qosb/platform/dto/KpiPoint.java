package com.qosb.platform.dto;

import java.time.Instant;

public record KpiPoint(
        Instant timestamp,
        String networkType,
        double latencyMs,
        double jitterMs,
        double throughputMbps,
        double packetLossRate,
        double errorRate,
        double signalStrengthDbm,
        int activeUsers
) {}
