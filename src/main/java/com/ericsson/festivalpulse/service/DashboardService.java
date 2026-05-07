package com.ericsson.festivalpulse.service;

import com.ericsson.festivalpulse.model.DashboardSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FestivalAreaService festivalAreaService;
    private final CrowdReportService crowdReportService;
    private final CrowdAlertService crowdAlertService;

    public DashboardSummary getSummary() {
        return new DashboardSummary(
                festivalAreaService.getAllAreas().size(),
                crowdReportService.getRecentReports(),
                crowdAlertService.getActiveAlerts()
        );
    }
}
