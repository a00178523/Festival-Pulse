package com.ericsson.festivalpulse.controller;

import com.ericsson.festivalpulse.model.DashboardSummary;
import com.ericsson.festivalpulse.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/festivals/{festivalId}/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardSummary> getDashboard(@PathVariable Long festivalId) {
        return ResponseEntity.ok(dashboardService.getSummary(festivalId));
    }
}
