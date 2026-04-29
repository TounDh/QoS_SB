package com.qosb.platform.service;

import com.qosb.platform.dto.*;
import com.qosb.platform.entity.AnomalyEntity;
import com.qosb.platform.mapper.EntityDtoMapper;
import com.qosb.platform.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class QosService {

    private final KpiAggregatedRepository kpiRepo;
    private final AnomalyRepository       anomalyRepo;
    private final ForecastRepository      forecastRepo;
    private final AllocationRepository    allocationRepo;
    private final ReportRepository        reportRepo;
    private final EntityDtoMapper         mapper;

    public QosService(KpiAggregatedRepository kpiRepo,
                      AnomalyRepository anomalyRepo,
                      ForecastRepository forecastRepo,
                      AllocationRepository allocationRepo,
                      ReportRepository reportRepo,
                      EntityDtoMapper mapper) {
        this.kpiRepo = kpiRepo;
        this.anomalyRepo = anomalyRepo;
        this.forecastRepo = forecastRepo;
        this.allocationRepo = allocationRepo;
        this.reportRepo = reportRepo;
        this.mapper = mapper;
    }

    // ================= KPIs =================

    public List<KpiPoint> getKpis(String networkType, Instant from, Instant to) {
        return kpiRepo.search(networkType, from, to).stream()
                .map(mapper::toKpiPoint)
                .toList();
    }

    // ================= Forecasts =================

    public List<ForecastPoint> getForecasts(String networkType, String metric, int limit) {
        Pageable pageable = limit > 0 ? PageRequest.of(0, limit) : Pageable.unpaged();
        return forecastRepo.search(networkType, metric, pageable).stream()
                .map(mapper::toForecastPoint)
                .toList();
    }

    // ================= Anomalies =================

    public List<AnomalyRecord> getAnomalies(String networkType, String severity, boolean onlyAnomalies, int limit) {
        Pageable pageable = limit > 0 ? PageRequest.of(0, limit) : Pageable.unpaged();
        return anomalyRepo.search(onlyAnomalies, networkType, severity, pageable).stream()
                .map(mapper::toAnomalyRecord)
                .toList();
    }

    public Optional<AnomalyRecord> getAnomalyById(String id) {
        return anomalyRepo.findById(id).map(mapper::toAnomalyRecord);
    }

    // ================= Allocations =================

    public List<AllocationRecommendation> getAllocations(String priority, String networkType) {
        return allocationRepo.search(priority, networkType).stream()
                .map(mapper::toAllocation)
                .toList();
    }

    public Optional<AllocationRecommendation> getAllocationByAnomalyId(String anomalyId) {
        return allocationRepo.findFirstByAnomalyId(anomalyId).map(mapper::toAllocation);
    }

    // ================= Reports =================

    public List<AlertReport> getReports(String networkType, String severity) {
        return reportRepo.search(networkType, severity).stream()
                .map(mapper::toReport)
                .toList();
    }

    public Optional<AlertReport> getReportById(String id) {
        return reportRepo.findById(id).map(mapper::toReport);
    }

    // ================= Dashboard summary =================

    public DashboardSummary computeSummary() {
        List<AnomalyEntity> all       = anomalyRepo.findAll();
        List<AnomalyEntity> anomalous = all.stream().filter(AnomalyEntity::isAnomaly).toList();

        Map<String, Integer> bySeverity = new HashMap<>();
        for (AnomalyEntity a : anomalous) {
            bySeverity.merge(a.getSeverityLabel(), 1, Integer::sum);
        }

        Map<String, Integer> byNetwork = anomalous.stream()
                .collect(Collectors.groupingBy(
                        AnomalyEntity::getNetworkType,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        Map<String, Double> avgLatency = all.stream()
                .collect(Collectors.groupingBy(
                        AnomalyEntity::getNetworkType,
                        Collectors.averagingDouble(AnomalyEntity::getLatencyMs)
                ));

        Map<String, Double> avgThroughput = all.stream()
                .collect(Collectors.groupingBy(
                        AnomalyEntity::getNetworkType,
                        Collectors.averagingDouble(AnomalyEntity::getThroughputMbps)
                ));

        // Mean report quality (extracted from JSONB evaluation column)
        double meanQuality = reportRepo.findAll().stream()
                .map(r -> r.getEvaluation() == null ? null : r.getEvaluation().get("finalQualityScore"))
                .filter(Objects::nonNull)
                .mapToDouble(o -> o instanceof Number n ? n.doubleValue() : 0.0)
                .average()
                .orElse(0.0);

        Map<String, Integer> priorityDist = allocationRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        a -> a.getPriority(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        return new DashboardSummary(
                anomalous.size(),
                bySeverity.getOrDefault("critical", 0),
                bySeverity.getOrDefault("high",     0),
                bySeverity.getOrDefault("medium",   0),
                bySeverity.getOrDefault("low",      0),
                byNetwork,
                roundMap(avgLatency,    2),
                roundMap(avgThroughput, 2),
                Math.round(meanQuality * 10000.0) / 10000.0,
                (int) allocationRepo.count(),
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
