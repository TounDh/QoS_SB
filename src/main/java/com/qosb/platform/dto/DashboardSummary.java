package com.qosb.platform.dto;

import java.util.Map;

public record DashboardSummary(
        int totalAnomalies,
        int criticalCount,
        int highCount,
        int mediumCount,
        int lowCount,
        Map<String, Integer> anomaliesByNetwork,
        Map<String, Double> avgLatencyByNetwork,
        Map<String, Double> avgThroughputByNetwork,
        double meanReportQualityScore,
        int totalRecommendations,
        Map<String, Integer> priorityDistribution
) {}
