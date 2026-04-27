package com.qosb.platform.controller;

import com.qosb.platform.dto.KpiPoint;
import com.qosb.platform.service.QosService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/kpis")
public class KpiController {

    private final QosService service;

    public KpiController(QosService service) {
        this.service = service;
    }

    @GetMapping
    public List<KpiPoint> list(
            @RequestParam(required = false) String networkType,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to
    ) {
        return service.getKpis(networkType, from, to);
    }
}
