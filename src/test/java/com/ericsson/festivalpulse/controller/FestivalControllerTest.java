package com.ericsson.festivalpulse.controller;

import com.ericsson.festivalpulse.model.Festival;
import com.ericsson.festivalpulse.model.FestivalArea;
import com.ericsson.festivalpulse.service.FestivalAreaService;
import com.ericsson.festivalpulse.service.FestivalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({FestivalController.class, FestivalAreaController.class})
class FestivalControllerTest {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean FestivalService festivalService;
    @MockitoBean FestivalAreaService festivalAreaService;

    private Festival festival;
    private FestivalArea area;

    @BeforeEach
    void setUp() {
        festival = new Festival(1L, "Electric Picnic", "Stradbally", null, null);
        area = new FestivalArea(10L, festival, "Main Stage", "Main outdoor stage", "Stage", null, null);
    }

    // ── Festival endpoints ──────────────────────────────────────────────────

    @Test
    void createFestival_returns201_withValidRequest() throws Exception {
        when(festivalService.createFestival(any())).thenReturn(festival);

        mockMvc.perform(post("/api/festivals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("name", "Electric Picnic", "description", "Stradbally"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Electric Picnic"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createFestival_returns400_whenNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/festivals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("Name is required"));
    }

    @Test
    void createFestival_returns400_whenBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/api/festivals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("Name is required"));
    }

    @Test
    void getAllFestivals_returns200_withList() throws Exception {
        when(festivalService.getAllFestivals()).thenReturn(List.of(festival));

        mockMvc.perform(get("/api/festivals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Electric Picnic"));
    }

    @Test
    void getAllFestivals_returns200_withEmptyList() throws Exception {
        when(festivalService.getAllFestivals()).thenReturn(List.of());

        mockMvc.perform(get("/api/festivals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ── Festival Area endpoints ─────────────────────────────────────────────

    @Test
    void createArea_returns201_withValidRequest() throws Exception {
        when(festivalAreaService.createArea(eq(1L), any())).thenReturn(area);

        mockMvc.perform(post("/api/festivals/1/areas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("name", "Main Stage", "areaType", "Stage"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Main Stage"))
                .andExpect(jsonPath("$.areaType").value("Stage"));
    }

    @Test
    void createArea_returns400_whenNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/festivals/1/areas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("Name is required"));
    }

    @Test
    void createArea_returns400_whenBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/api/festivals/1/areas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("Name is required"));
    }

    @Test
    void createArea_returns404_whenFestivalNotFound() throws Exception {
        when(festivalAreaService.createArea(eq(999L), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Festival not found"));

        mockMvc.perform(post("/api/festivals/999/areas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "Main Stage"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Festival not found"));
    }

    @Test
    void getAllAreas_returns200_withAreaList() throws Exception {
        when(festivalAreaService.getAreasByFestival(1L)).thenReturn(List.of(area));

        mockMvc.perform(get("/api/festivals/1/areas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Main Stage"))
                .andExpect(jsonPath("$[0].areaType").value("Stage"));
    }

    @Test
    void getAllAreas_returns200_withEmptyList() throws Exception {
        when(festivalAreaService.getAreasByFestival(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/festivals/1/areas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllAreas_returns404_whenFestivalNotFound() throws Exception {
        when(festivalAreaService.getAreasByFestival(999L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Festival not found"));

        mockMvc.perform(get("/api/festivals/999/areas"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Festival not found"));
    }
}
