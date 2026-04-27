package com.qosb.platform.dto;

import java.time.Instant;

public record ForecastPoint(
        Instant timestamp,
        String networkType,
        String metric,
        Double actual,
        Double predicted
) {}
