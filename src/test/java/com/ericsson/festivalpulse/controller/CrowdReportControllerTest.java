package com.ericsson.festivalpulse.controller;

import com.ericsson.festivalpulse.model.CrowdLevel;
import com.ericsson.festivalpulse.model.CrowdReport;
import com.ericsson.festivalpulse.model.FestivalArea;
import com.ericsson.festivalpulse.service.CrowdReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CrowdReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CrowdReportService crowdReportService;

    private final FestivalArea area = new FestivalArea(1L, "Main Stage", "Primary area", "Stage");

    @Test
    void submitReport_validRequest_shouldReturn201() throws Exception {
        CrowdReport saved = new CrowdReport(1L, area, CrowdLevel.MEDIUM, "Filling up", LocalDateTime.now());
        when(crowdReportService.submitReport(1L, CrowdLevel.MEDIUM, "Filling up")).thenReturn(saved);

        mockMvc.perform(post("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "areaId", 1,
                                "crowdLevel", "MEDIUM",
                                "note", "Filling up"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.crowdLevel").value("MEDIUM"));
    }

    @Test
    void submitReport_missingArea_shouldReturn404() throws Exception {
        when(crowdReportService.submitReport(99L, CrowdLevel.LOW, null))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Area not found"));

        mockMvc.perform(post("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "areaId", 99,
                                "crowdLevel", "LOW"
                        ))))
                .andExpect(status().isNotFound());
    }

    @Test
    void submitReport_missingRequiredFields_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRecentReports_shouldReturn200WithList() throws Exception {
        CrowdReport report = new CrowdReport(1L, area, CrowdLevel.MEDIUM, "Filling up", LocalDateTime.now());
        when(crowdReportService.getRecentReports()).thenReturn(List.of(report));

        mockMvc.perform(get("/api/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].crowdLevel").value("MEDIUM"));
    }
}
