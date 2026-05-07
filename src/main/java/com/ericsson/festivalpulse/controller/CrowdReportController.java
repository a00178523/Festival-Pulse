package com.ericsson.festivalpulse.controller;

import com.ericsson.festivalpulse.model.CrowdLevel;
import com.ericsson.festivalpulse.model.CrowdReport;
import com.ericsson.festivalpulse.service.CrowdReportService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class CrowdReportController {

    private final CrowdReportService crowdReportService;

    @PostMapping
    public ResponseEntity<CrowdReport> submitReport(@RequestBody ReportRequest request) {
        CrowdReport report = crowdReportService.submitReport(request.getAreaId(), request.getCrowdLevel(), request.getNote());
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    @GetMapping
    public ResponseEntity<List<CrowdReport>> getRecentReports() {
        return ResponseEntity.ok(crowdReportService.getRecentReports());
    }

    @Data
    static class ReportRequest {
        private Long areaId;
        private CrowdLevel crowdLevel;
        private String note;
    }
}
