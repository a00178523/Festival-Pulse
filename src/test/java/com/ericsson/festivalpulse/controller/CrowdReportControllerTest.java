package com.ericsson.festivalpulse.controller;

import com.ericsson.festivalpulse.model.*;
import com.ericsson.festivalpulse.service.CrowdReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CrowdReportController.class)
class CrowdReportControllerTest {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean CrowdReportService crowdReportService;

    private Festival festival;
    private FestivalArea area;
    private CrowdReport report;

    @BeforeEach
    void setUp() {
        festival = new Festival(1L, "Electric Picnic", "Stradbally", null, null);
        area = new FestivalArea(10L, festival, "Main Stage", "Main stage", "Stage", null, null);
        report = new CrowdReport(1L, area, CrowdLevel.MEDIUM, "Getting busy", LocalDateTime.now());
    }

    @Test
    void submitReport_returns201_withValidRequest() throws Exception {
        when(crowdReportService.submitReport(eq(1L), eq(10L), eq(CrowdLevel.MEDIUM), anyString()))
                .thenReturn(report);

        mockMvc.perform(post("/api/festivals/1/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("areaId", 10, "crowdLevel", "MEDIUM", "note", "Getting busy"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.crowdLevel").value("MEDIUM"))
                .andExpect(jsonPath("$.area.name").value("Main Stage"));
    }

    @Test
    void submitReport_returns400_whenAreaIdMissing() throws Exception {
        mockMvc.perform(post("/api/festivals/1/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("crowdLevel", "MEDIUM"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.areaId").value("Area ID is required"));
    }

    @Test
    void submitReport_returns400_whenCrowdLevelMissing() throws Exception {
        mockMvc.perform(post("/api/festivals/1/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("areaId", 10))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.crowdLevel").value("Crowd level is required"));
    }

    @Test
    void submitReport_returns404_whenAreaNotFound() throws Exception {
        when(crowdReportService.submitReport(any(), any(), any(), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Area not found in this festival"));

        mockMvc.perform(post("/api/festivals/1/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("areaId", 99, "crowdLevel", "LOW"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Area not found in this festival"));
    }

    @Test
    void submitReport_returns409_whenAreaHasActiveAlert() throws Exception {
        when(crowdReportService.submitReport(any(), any(), any(), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT,
                        "Area already has an active alert — resolve it before submitting a new report"));

        mockMvc.perform(post("/api/festivals/1/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("areaId", 10, "crowdLevel", "MEDIUM"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        "Area already has an active alert — resolve it before submitting a new report"));
    }

    @Test
    void getRecentReports_returns200_withReportList() throws Exception {
        when(crowdReportService.getRecentReports(1L)).thenReturn(List.of(report));

        mockMvc.perform(get("/api/festivals/1/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].crowdLevel").value("MEDIUM"))
                .andExpect(jsonPath("$[0].area.name").value("Main Stage"));
    }
}
