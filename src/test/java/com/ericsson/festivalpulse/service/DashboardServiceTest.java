package com.ericsson.festivalpulse.service;

import com.ericsson.festivalpulse.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private FestivalAreaService festivalAreaService;

    @Mock
    private CrowdReportService crowdReportService;

    @Mock
    private CrowdAlertService crowdAlertService;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void getSummary_shouldAggregateAllServices() {
        FestivalArea area = new FestivalArea(1L, "Main Stage", "Primary area", "Stage");
        CrowdReport report = new CrowdReport(1L, area, CrowdLevel.MEDIUM, "Filling up", LocalDateTime.now());
        CrowdAlert alert = new CrowdAlert(1L, area, "Main Stage is FULL!", AlertStatus.ACTIVE, LocalDateTime.now());

        when(festivalAreaService.getAllAreas()).thenReturn(List.of(area));
        when(crowdReportService.getRecentReports()).thenReturn(List.of(report));
        when(crowdAlertService.getActiveAlerts()).thenReturn(List.of(alert));

        DashboardSummary result = dashboardService.getSummary();

        assertThat(result.getTotalAreas()).isEqualTo(1);
        assertThat(result.getRecentReports()).hasSize(1);
        assertThat(result.getActiveAlerts()).hasSize(1);
        assertThat(result.getActiveAlerts().get(0).getStatus()).isEqualTo(AlertStatus.ACTIVE);
    }

    @Test
    void getSummary_noData_shouldReturnEmptySummary() {
        when(festivalAreaService.getAllAreas()).thenReturn(List.of());
        when(crowdReportService.getRecentReports()).thenReturn(List.of());
        when(crowdAlertService.getActiveAlerts()).thenReturn(List.of());

        DashboardSummary result = dashboardService.getSummary();

        assertThat(result.getTotalAreas()).isZero();
        assertThat(result.getRecentReports()).isEmpty();
        assertThat(result.getActiveAlerts()).isEmpty();
    }
}
