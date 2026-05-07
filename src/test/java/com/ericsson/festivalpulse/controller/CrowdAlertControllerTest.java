package com.ericsson.festivalpulse.controller;

import com.ericsson.festivalpulse.model.AlertStatus;
import com.ericsson.festivalpulse.model.CrowdAlert;
import com.ericsson.festivalpulse.model.FestivalArea;
import com.ericsson.festivalpulse.service.CrowdAlertService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CrowdAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CrowdAlertService crowdAlertService;

    private final FestivalArea area = new FestivalArea(1L, "Main Stage", "Primary area", "Stage");

    @Test
    void getActiveAlerts_shouldReturn200WithAlertList() throws Exception {
        CrowdAlert alert = new CrowdAlert(1L, area, "Main Stage is FULL!", AlertStatus.ACTIVE, LocalDateTime.now());
        when(crowdAlertService.getActiveAlerts()).thenReturn(List.of(alert));

        mockMvc.perform(get("/api/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].message").value("Main Stage is FULL!"));
    }

    @Test
    void resolveAlert_shouldReturn200WithResolvedAlert() throws Exception {
        CrowdAlert resolved = new CrowdAlert(1L, area, "Main Stage is FULL!", AlertStatus.RESOLVED, LocalDateTime.now());
        when(crowdAlertService.resolveAlert(1L)).thenReturn(resolved);

        mockMvc.perform(patch("/api/alerts/1/resolve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESOLVED"));
    }
}
