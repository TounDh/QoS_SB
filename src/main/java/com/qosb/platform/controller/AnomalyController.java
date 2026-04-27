package com.qosb.platform.controller;

import com.qosb.platform.dto.AnomalyRecord;
import com.qosb.platform.service.QosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/anomalies")
public class AnomalyController {

    private final QosService service;

    public AnomalyController(QosService service) {
        this.service = service;
    }

    @GetMapping
    public List<AnomalyRecord> list(
            @RequestParam(required = false) String networkType,
            @RequestParam(required = false) String severity,
            @RequestParam(defaultValue = "true") boolean onlyAnomalies,
            @RequestParam(defaultValue = "100") int limit
    ) {
        return service.getAnomalies(networkType, severity, onlyAnomalies, limit);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnomalyRecord> getOne(@PathVariable String id) {
        return service.getAnomalyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
