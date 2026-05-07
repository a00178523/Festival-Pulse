package com.ericsson.festivalpulse.service;

import com.ericsson.festivalpulse.model.DashboardSummary;
import com.ericsson.festivalpulse.model.Festival;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FestivalService festivalService;
    private final FestivalAreaService festivalAreaService;
    private final CrowdReportService crowdReportService;
    private final CrowdAlertService crowdAlertService;

    public DashboardSummary getSummary(Long festivalId) {
        Festival festival = festivalService.getFestivalById(festivalId);
        return new DashboardSummary(
                festivalAreaService.getAreasByFestival(festivalId).size(),
                crowdReportService.getRecentReports(festivalId),
                crowdAlertService.getActiveAlerts(festival)
        );
    }
}
