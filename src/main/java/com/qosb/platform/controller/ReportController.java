package com.qosb.platform.controller;

import com.qosb.platform.dto.AlertReport;
import com.qosb.platform.service.QosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final QosService service;

    public ReportController(QosService service) {
        this.service = service;
    }

    @GetMapping
    public List<AlertReport> list(
            @RequestParam(required = false) String networkType,
            @RequestParam(required = false) String severity
    ) {
        return service.getReports(networkType, severity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertReport> getOne(@PathVariable String id) {
        return service.getReportById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
