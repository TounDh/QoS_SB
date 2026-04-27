package com.qosb.platform.controller;

import com.qosb.platform.dto.DashboardSummary;
import com.qosb.platform.service.QosService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final QosService service;

    public DashboardController(QosService service) {
        this.service = service;
    }

    @GetMapping("/summary")
    public DashboardSummary summary() {
        return service.computeSummary();
    }
}
