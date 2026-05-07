package com.ericsson.festivalpulse.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DashboardSummary {
    private int totalAreas;
    private List<CrowdReport> recentReports;
    private List<CrowdAlert> activeAlerts;
}
