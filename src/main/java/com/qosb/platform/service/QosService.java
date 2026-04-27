package com.qosb.platform.service;

import com.qosb.platform.dto.*;
import com.qosb.platform.mock.MockDataStore;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QosService {

    private final MockDataStore store;

    public QosService(MockDataStore store) {
        this.store = store;
    }

    public List<KpiPoint> getKpis(String networkType, Instant from, Instant to) {
        return store.getKpiPoints().stream()
                .filter(p -> networkType == null || networkType.equalsIgnoreCase(p.networkType()))
                .filter(p -> from == null || !p.timestamp().isBefore(from))
                .filter(p -> to == null || !p.timestamp().isAfter(to))
                .toList();
    }

    public List<ForecastPoint> getForecasts(String networkType, String metric, int limit) {
        return store.getForecasts().stream()
                .filter(f -> networkType == null || networkType.equalsIgnoreCase(f.networkType()))
                .filter(f -> metric == null || metric.equalsIgnoreCase(f.metric()))
                .limit(limit > 0 ? limit : Long.MAX_VALUE)
                .toList();
    }

    public List<AnomalyRecord> getAnomalies(String networkType, String severity, boolean onlyAnomalies, int limit) {
        return store.getAnomalies().stream()
                .filter(a -> !onlyAnomalies || a.isAnomaly())
                .filter(a -> networkType == null || networkType.equalsIgnoreCase(a.networkType()))
                .filter(a -> severity == null || severity.equalsIgnoreCase(a.severityLabel()))
                .sorted(Comparator.comparing(AnomalyRecord::timestamp).reversed())
                .limit(limit > 0 ? limit : Long.MAX_VALUE)
                .toList();
    }

    public Optional<AnomalyRecord> getAnomalyById(String id) {
        return store.getAnomalies().stream().filter(a -> a.id().equals(id)).findFirst();
    }

    public List<AllocationRecommendation> getAllocations(String priority, String networkType) {
        return store.getAllocations().stream()
                .filter(r -> priority == null || priority.equalsIgnoreCase(r.priority()))
                .filter(r -> networkType == null || networkType.equalsIgnoreCase(r.networkType()))
                .toList();
    }

    public Optional<AllocationRecommendation> getAllocationByAnomalyId(String anomalyId) {
        return store.getAllocations().stream()
                .filter(r -> r.anomalyId().equals(anomalyId))
                .findFirst();
    }

    public List<AlertReport> getReports(String networkType, String severity) {
        return store.getReports().stream()
                .filter(r -> networkType == null || networkType.equalsIgnoreCase(r.networkType()))
                .filter(r -> severity == null || severity.equalsIgnoreCase(r.severityLabel()))
                .toList();
    }

    public Optional<AlertReport> getReportById(String id) {
        return store.getReports().stream().filter(r -> r.id().equals(id)).findFirst();
    }

    public DashboardSummary computeSummary() {
        List<AnomalyRecord> all = store.getAnomalies();
        List<AnomalyRecord> anomalous = all.stream().filter(AnomalyRecord::isAnomaly).toList();

        Map<String, Integer> bySeverity = new HashMap<>();
        for (AnomalyRecord a : anomalous) {
            bySeverity.merge(a.severityLabel(), 1, Integer::sum);
        }

        Map<String, Integer> byNetwork = anomalous.stream()
                .collect(Collectors.groupingBy(
                        AnomalyRecord::networkType,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        Map<String, Double> avgLatency = all.stream()
                .collect(Collectors.groupingBy(
                        AnomalyRecord::networkType,
                        Collectors.averagingDouble(AnomalyRecord::latencyMs)
                ));

        Map<String, Double> avgThroughput = all.stream()
                .collect(Collectors.groupingBy(
                        AnomalyRecord::networkType,
                        Collectors.averagingDouble(AnomalyRecord::throughputMbps)
                ));

        double meanQuality = store.getReports().stream()
                .mapToDouble(r -> r.evaluation().finalQualityScore())
                .average().orElse(0.0);

        Map<String, Integer> priorityDist = store.getAllocations().stream()
                .collect(Collectors.groupingBy(
                        AllocationRecommendation::priority,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        return new DashboardSummary(
                anomalous.size(),
                bySeverity.getOrDefault("critical", 0),
                bySeverity.getOrDefault("high", 0),
                bySeverity.getOrDefault("medium", 0),
                bySeverity.getOrDefault("low", 0),
                byNetwork,
                roundMap(avgLatency, 2),
                roundMap(avgThroughput, 2),
                Math.round(meanQuality * 10000.0) / 10000.0,
                store.getAllocations().size(),
                priorityDist
        );
    }

    private Map<String, Double> roundMap(Map<String, Double> in, int digits) {
        double scale = Math.pow(10, digits);
        Map<String, Double> out = new LinkedHashMap<>();
        in.forEach((k, v) -> out.put(k, Math.round(v * scale) / scale));
        return out;
    }
}
