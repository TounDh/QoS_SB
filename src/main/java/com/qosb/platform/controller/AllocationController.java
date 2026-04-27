package com.qosb.platform.controller;

import com.qosb.platform.dto.AllocationRecommendation;
import com.qosb.platform.service.QosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/allocations")
public class AllocationController {

    private final QosService service;

    public AllocationController(QosService service) {
        this.service = service;
    }

    @GetMapping
    public List<AllocationRecommendation> list(
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String networkType
    ) {
        return service.getAllocations(priority, networkType);
    }

    @GetMapping("/by-anomaly/{anomalyId}")
    public ResponseEntity<AllocationRecommendation> byAnomaly(@PathVariable String anomalyId) {
        return service.getAllocationByAnomalyId(anomalyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
