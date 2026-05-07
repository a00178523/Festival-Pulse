package com.ericsson.festivalpulse.controller;

import com.ericsson.festivalpulse.model.CrowdAlert;
import com.ericsson.festivalpulse.service.CrowdAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class CrowdAlertController {

    private final CrowdAlertService crowdAlertService;

    @GetMapping
    public ResponseEntity<List<CrowdAlert>> getActiveAlerts() {
        return ResponseEntity.ok(crowdAlertService.getActiveAlerts());
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<CrowdAlert> resolveAlert(@PathVariable Long id) {
        return ResponseEntity.ok(crowdAlertService.resolveAlert(id));
    }
}
