package com.qosb.platform.mock;

import com.qosb.platform.dto.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Generates realistic synthetic telecom QoS data mirroring the structure
 * of the original Python pipeline (Nigerian Telecom QoS dataset).
 *
 * Each network type (2G/3G/4G/5G) has its own baseline KPI profile.
 * ~5% of timestamps are seeded as anomalies with degraded metrics.
 */
@Component
public class MockDataStore {

    private static final List<String> NETWORKS = List.of("2G", "3G", "4G", "5G");
    private static final List<String> METRICS = List.of(
            "latency_ms", "jitter_ms", "throughput_mbps", "packet_loss_rate",
            "error_rate", "signal_strength_dbm", "active_users"
    );

    private static final int POINTS_PER_NETWORK = 720; // 12 hours @ 1-min cadence
    private static final double ANOMALY_RATE = 0.05;

    private final List<KpiPoint> kpiPoints = new ArrayList<>();
    private final List<AnomalyRecord> anomalies = new ArrayList<>();
    private final List<ForecastPoint> forecasts = new ArrayList<>();
    private final List<AllocationRecommendation> allocations = new ArrayList<>();
    private final List<AlertReport> reports = new ArrayList<>();

    private final Random rng = new Random(42L); // deterministic mock data

    @PostConstruct
    public void init() {
        Instant baseTime = Instant.now().truncatedTo(ChronoUnit.MINUTES)
                .minus(POINTS_PER_NETWORK, ChronoUnit.MINUTES);

        for (String network : NETWORKS) {
            generateNetworkData(network, baseTime);
        }
        generateAllocations();
        generateReports();
    }

    private void generateNetworkData(String network, Instant base) {
        double[] baseline = baselineFor(network);
        double[] noise = noiseFor(network);
        double threshold = 0.025 + rng.nextDouble() * 0.015;

        for (int i = 0; i < POINTS_PER_NETWORK; i++) {
            Instant ts = base.plus(i, ChronoUnit.MINUTES);
            boolean isAnomaly = rng.nextDouble() < ANOMALY_RATE;

            // Base KPIs with sinusoidal daily pattern + noise
            double dayPhase = Math.sin(2 * Math.PI * i / POINTS_PER_NETWORK);

            double latency = baseline[0] + dayPhase * 5 + gauss(noise[0]);
            double jitter = baseline[1] + dayPhase * 1.5 + gauss(noise[1]);
            double throughput = baseline[2] - dayPhase * 4 + gauss(noise[2]);
            double packetLoss = Math.max(0, baseline[3] + gauss(noise[3]));
            double errorRate = Math.max(0, baseline[4] + gauss(noise[4]));
            double signalStrength = baseline[5] + gauss(noise[5]);
            int activeUsers = (int) Math.max(0, baseline[6] + dayPhase * 50 + gauss(noise[6]));

            // Inject anomalies
            if (isAnomaly) {
                latency *= 2.5 + rng.nextDouble();
                jitter *= 2.0 + rng.nextDouble();
                throughput *= 0.4;
                packetLoss += 3 + rng.nextDouble() * 4;
                errorRate += 1.5 + rng.nextDouble() * 2;
                signalStrength -= 15 + rng.nextDouble() * 10;
            }

            kpiPoints.add(new KpiPoint(
                    ts, network,
                    round(latency, 2), round(jitter, 2), round(throughput, 2),
                    round(packetLoss, 4), round(errorRate, 4), round(signalStrength, 2),
                    activeUsers
            ));

            // Reconstruction error proportional to deviation magnitude
            double reconErr;
            if (isAnomaly) {
                reconErr = threshold * (1.0 + rng.nextDouble() * 0.6);
            } else {
                reconErr = threshold * (0.2 + rng.nextDouble() * 0.7);
            }
            double severity = reconErr / threshold;

            anomalies.add(new AnomalyRecord(
                    String.format("anom-%s-%d", network, i),
                    ts, network,
                    round(reconErr, 6),
                    round(threshold, 6),
                    round(severity, 4),
                    severityLabel(isAnomaly, severity),
                    isAnomaly,
                    round(latency, 2), round(jitter, 2), round(throughput, 2),
                    round(packetLoss, 4), round(errorRate, 4), round(signalStrength, 2),
                    activeUsers
            ));

            // Forecast: predicted = actual + small noise (mock LSTM)
            for (String metric : METRICS) {
                double actualVal = switch (metric) {
                    case "latency_ms" -> latency;
                    case "jitter_ms" -> jitter;
                    case "throughput_mbps" -> throughput;
                    case "packet_loss_rate" -> packetLoss;
                    case "error_rate" -> errorRate;
                    case "signal_strength_dbm" -> signalStrength;
                    case "active_users" -> activeUsers;
                    default -> 0.0;
                };
                double predicted = actualVal + gauss(Math.abs(actualVal) * 0.06 + 0.5);
                forecasts.add(new ForecastPoint(
                        ts, network, metric, round(actualVal, 3), round(predicted, 3)
                ));
            }
        }
    }

    private void generateAllocations() {
        // Generate allocations for top 15 anomalies by severity, mirroring Python
        anomalies.stream()
                .filter(AnomalyRecord::isAnomaly)
                .sorted(Comparator.comparingDouble(AnomalyRecord::severityScore).reversed())
                .limit(15)
                .forEach(a -> allocations.add(buildAllocation(a)));
    }

    private AllocationRecommendation buildAllocation(AnomalyRecord a) {
        String priority = priorityFor(a.severityScore());
        String action = recommendedActionFor(a);

        return new AllocationRecommendation(
                "rec-" + a.id(),
                Instant.now(),
                a.id(),
                a.networkType() + " network",
                a.networkType(),
                action,
                priority,
                round(0.78 + rng.nextDouble() * 0.20, 4),
                issueSummary(a),
                rootCauseFor(a),
                expectedImpactFor(a, priority),
                followUpActionsFor(a),
                "parsed",
                "llama-3.3-70b-versatile (mocked)",
                Instant.now()
        );
    }

    private void generateReports() {
        // Generate alert reports for top 5 allocations
        allocations.stream().limit(5).forEach(rec -> {
            AnomalyRecord a = anomalies.stream()
                    .filter(x -> x.id().equals(rec.anomalyId()))
                    .findFirst().orElse(null);
            if (a == null) return;
            reports.add(buildReport(a, rec));
        });
    }

    private AlertReport buildReport(AnomalyRecord a, AllocationRecommendation rec) {
        String content = String.format("""
                ## 1. Situation
                A %s severity anomaly was detected on the %s network at %s. \
                Reconstruction error of %.6f exceeded the threshold of %.6f \
                (severity score: %.2f).

                ## 2. Key Evidence
                - Latency: %.2f ms
                - Jitter: %.2f ms
                - Throughput: %.2f mbps
                - Packet loss rate: %.2f%%
                - Error rate: %.2f%%
                - Signal strength: %.1f dBm
                - Active users: %d

                ## 3. Recommended Action
                %s. %s

                ## 4. Escalation: %s
                Priority: %s. Confidence: %.2f. %s

                ## 5. Next Check
                Re-evaluate %s metrics in 15 minutes. If degradation persists, escalate to Tier-2 NOC.
                """,
                a.severityLabel(), a.networkType(), a.timestamp(),
                a.reconstructionError(), a.threshold(), a.severityScore(),
                a.latencyMs(), a.jitterMs(), a.throughputMbps(),
                a.packetLossRate(), a.errorRate(), a.signalStrengthDbm(), a.activeUsers(),
                rec.recommendedAction(), rec.expectedImpact(),
                "critical".equals(rec.priority()) || "high".equals(rec.priority()) ? "Yes" : "No",
                rec.priority(), rec.confidence(), rec.issueSummary(),
                a.networkType()
        );

        Map<String, Boolean> sections = new LinkedHashMap<>();
        sections.put("Situation", true);
        sections.put("Key Evidence", true);
        sections.put("Recommended Action", true);
        sections.put("Escalation", true);
        sections.put("Next Check", true);

        AlertReport.ReportEvaluation eval = new AlertReport.ReportEvaluation(
                "ok",
                round(0.85 + rng.nextDouble() * 0.13, 4),
                round(0.88 + rng.nextDouble() * 0.10, 4),
                round(0.95 + rng.nextDouble() * 0.05, 4),
                1.0,
                round(rng.nextDouble() * 0.05, 4),
                sections
        );

        AlertReport.ReportMetadata meta = new AlertReport.ReportMetadata(
                "critical".equals(rec.priority()) || "high".equals(rec.priority()) ? "yes" : "no",
                a.severityLabel(),
                content.length(),
                content.split("\\s+").length
        );

        return new AlertReport(
                "rep-" + a.id(),
                a.id(),
                a.timestamp(),
                a.networkType(),
                a.severityLabel(),
                "real_time_alert",
                content,
                meta,
                eval,
                Instant.now()
        );
    }

    // -------- helpers --------

    private double[] baselineFor(String net) {
        // [latency, jitter, throughput, packetLoss, errorRate, signal, activeUsers]
        return switch (net) {
            case "2G" -> new double[]{180, 22, 0.3, 1.5, 1.2, -95, 80};
            case "3G" -> new double[]{90, 12, 5, 0.8, 0.5, -85, 200};
            case "4G" -> new double[]{40, 6, 50, 0.3, 0.2, -75, 450};
            case "5G" -> new double[]{15, 2, 250, 0.1, 0.1, -65, 750};
            default -> new double[]{50, 8, 25, 0.5, 0.3, -80, 300};
        };
    }

    private double[] noiseFor(String net) {
        return switch (net) {
            case "2G" -> new double[]{12, 3, 0.1, 0.4, 0.3, 4, 15};
            case "3G" -> new double[]{8, 2, 1.0, 0.2, 0.1, 3, 25};
            case "4G" -> new double[]{4, 1, 5.0, 0.1, 0.05, 2, 40};
            case "5G" -> new double[]{2, 0.5, 25.0, 0.05, 0.05, 2, 70};
            default -> new double[]{5, 1, 3.0, 0.1, 0.1, 3, 30};
        };
    }

    private double gauss(double sigma) {
        return rng.nextGaussian() * sigma;
    }

    private double round(double v, int digits) {
        double scale = Math.pow(10, digits);
        return Math.round(v * scale) / scale;
    }

    private String severityLabel(boolean isAnomaly, double score) {
        if (!isAnomaly) return "none";
        if (score >= 1.4) return "critical";
        if (score >= 1.2) return "high";
        if (score >= 1.05) return "medium";
        return "low";
    }

    private String priorityFor(double severity) {
        if (severity >= 1.4) return "critical";
        if (severity >= 1.2) return "high";
        if (severity >= 1.05) return "medium";
        return "low";
    }

    private String recommendedActionFor(AnomalyRecord a) {
        if (a.packetLossRate() > 5) return "Reroute traffic via redundant link to mitigate packet loss";
        if (a.latencyMs() > 200) return "Investigate backhaul congestion and rebalance cell load";
        if (a.throughputMbps() < 1 && !"2G".equals(a.networkType()))
            return "Allocate additional spectrum channels and review carrier aggregation";
        if (a.signalStrengthDbm() < -100) return "Dispatch field team to inspect tower antenna alignment";
        return "Increase monitoring on this network and capture detailed diagnostics";
    }

    private String issueSummary(AnomalyRecord a) {
        return String.format(
                "%s network shows degraded KPIs with severity %.2f, indicating service-affecting conditions",
                a.networkType(), a.severityScore()
        );
    }

    private String rootCauseFor(AnomalyRecord a) {
        if (a.packetLossRate() > 5) return "Likely upstream link saturation or transient routing fault";
        if (a.latencyMs() > 200) return "Probable backhaul congestion during peak window";
        if (a.signalStrengthDbm() < -100) return "Potential antenna degradation or RF interference";
        return "Combined load and capacity pressure on the cell";
    }

    private String expectedImpactFor(AnomalyRecord a, String priority) {
        return switch (priority) {
            case "critical" -> "Service-impacting outage risk for " + a.activeUsers() + " active users";
            case "high" -> "Noticeable QoS degradation affecting subscriber experience";
            case "medium" -> "Marginal degradation; SLA still within tolerance";
            default -> "Minor metric drift; no immediate user impact expected";
        };
    }

    private List<String> followUpActionsFor(AnomalyRecord a) {
        return List.of(
                "Validate alarm against neighbor cells",
                "Cross-check forecast deviation in next 10 min",
                "Notify field engineering if KPIs do not recover"
        );
    }

    // -------- accessors --------
    public List<KpiPoint> getKpiPoints() { return kpiPoints; }
    public List<AnomalyRecord> getAnomalies() { return anomalies; }
    public List<ForecastPoint> getForecasts() { return forecasts; }
    public List<AllocationRecommendation> getAllocations() { return allocations; }
    public List<AlertReport> getReports() { return reports; }
}
