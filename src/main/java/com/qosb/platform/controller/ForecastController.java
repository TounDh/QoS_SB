package com.qosb.platform.controller;

import com.qosb.platform.dto.ForecastPoint;
import com.qosb.platform.service.QosService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forecasts")
public class ForecastController {

    private final QosService service;

    public ForecastController(QosService service) {
        this.service = service;
    }

    @GetMapping
    public List<ForecastPoint> list(
            @RequestParam(required = false) String networkType,
            @RequestParam(required = false) String metric,
            @RequestParam(defaultValue = "200") int limit
    ) {
        return service.getForecasts(networkType, metric, limit);
    }
}
