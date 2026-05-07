package com.ericsson.festivalpulse.controller;

import com.ericsson.festivalpulse.model.*;
import com.ericsson.festivalpulse.service.CrowdAlertService;
import com.ericsson.festivalpulse.service.FestivalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CrowdAlertController.class)
class CrowdAlertControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean CrowdAlertService crowdAlertService;
    @MockitoBean FestivalService festivalService;

    private Festival festival;
    private FestivalArea area;
    private CrowdAlert alert;

    @BeforeEach
    void setUp() {
        festival = new Festival(1L, "Electric Picnic", "Stradbally", null, null);
        area = new FestivalArea(10L, festival, "Main Stage", "Main stage", "Stage", null, null);
        alert = new CrowdAlert(1L, area, "Main Stage is FULL!", AlertStatus.ACTIVE, LocalDateTime.now());
    }

    @Test
    void getActiveAlerts_returns200_withAlertList() throws Exception {
        when(festivalService.getFestivalById(1L)).thenReturn(festival);
        when(crowdAlertService.getActiveAlerts(festival)).thenReturn(List.of(alert));

        mockMvc.perform(get("/api/festivals/1/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Main Stage is FULL!"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].area.name").value("Main Stage"));
    }

    @Test
    void getActiveAlerts_returns200_withEmptyList_whenNoAlerts() throws Exception {
        when(festivalService.getFestivalById(1L)).thenReturn(festival);
        when(crowdAlertService.getActiveAlerts(festival)).thenReturn(List.of());

        mockMvc.perform(get("/api/festivals/1/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void resolveAlert_returns200_withResolvedAlert() throws Exception {
        CrowdAlert resolved = new CrowdAlert(1L, area, "Main Stage is FULL!", AlertStatus.RESOLVED, LocalDateTime.now());
        when(crowdAlertService.resolveAlert(1L, 1L)).thenReturn(resolved);

        mockMvc.perform(patch("/api/festivals/1/alerts/1/resolve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESOLVED"));
    }

    @Test
    void resolveAlert_returns404_whenAlertNotFound() throws Exception {
        when(crowdAlertService.resolveAlert(1L, 99L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found"));

        mockMvc.perform(patch("/api/festivals/1/alerts/99/resolve"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Alert not found"));
    }
}
