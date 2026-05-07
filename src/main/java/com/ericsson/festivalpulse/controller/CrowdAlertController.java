package com.ericsson.festivalpulse.controller;

import com.ericsson.festivalpulse.model.CrowdAlert;
import com.ericsson.festivalpulse.model.Festival;
import com.ericsson.festivalpulse.service.CrowdAlertService;
import com.ericsson.festivalpulse.service.FestivalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/festivals/{festivalId}/alerts")
@RequiredArgsConstructor
public class CrowdAlertController {

    private final CrowdAlertService crowdAlertService;
    private final FestivalService festivalService;

    @GetMapping
    public ResponseEntity<List<CrowdAlert>> getActiveAlerts(@PathVariable Long festivalId) {
        Festival festival = festivalService.getFestivalById(festivalId);
        return ResponseEntity.ok(crowdAlertService.getActiveAlerts(festival));
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<CrowdAlert> resolveAlert(@PathVariable Long festivalId, @PathVariable Long id) {
        return ResponseEntity.ok(crowdAlertService.resolveAlert(festivalId, id));
    }
}
