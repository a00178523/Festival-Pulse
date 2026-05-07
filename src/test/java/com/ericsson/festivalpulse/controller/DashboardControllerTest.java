package com.ericsson.festivalpulse.controller;

import com.ericsson.festivalpulse.model.*;
import com.ericsson.festivalpulse.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @Test
    void getDashboard_shouldReturn200WithSummary() throws Exception {
        FestivalArea area = new FestivalArea(1L, "Main Stage", "Primary area", "Stage");
        CrowdReport report = new CrowdReport(1L, area, CrowdLevel.MEDIUM, "Filling up", LocalDateTime.now());
        CrowdAlert alert = new CrowdAlert(1L, area, "Main Stage is FULL!", AlertStatus.ACTIVE, LocalDateTime.now());
        DashboardSummary summary = new DashboardSummary(1, List.of(report), List.of(alert));

        when(dashboardService.getSummary()).thenReturn(summary);

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAreas").value(1))
                .andExpect(jsonPath("$.recentReports[0].crowdLevel").value("MEDIUM"))
                .andExpect(jsonPath("$.activeAlerts[0].status").value("ACTIVE"));
    }

    @Test
    void getDashboard_noData_shouldReturnEmptySummary() throws Exception {
        when(dashboardService.getSummary()).thenReturn(new DashboardSummary(0, List.of(), List.of()));

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAreas").value(0))
                .andExpect(jsonPath("$.recentReports").isEmpty())
                .andExpect(jsonPath("$.activeAlerts").isEmpty());
    }
}
