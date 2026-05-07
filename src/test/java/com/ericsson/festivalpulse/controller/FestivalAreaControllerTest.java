package com.ericsson.festivalpulse.controller;

import com.ericsson.festivalpulse.model.FestivalArea;
import com.ericsson.festivalpulse.service.FestivalAreaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FestivalAreaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private FestivalAreaService festivalAreaService;

    @Test
    void createArea_shouldReturn201WithCreatedArea() throws Exception {
        FestivalArea input = new FestivalArea(null, "Main Stage", "Primary performance area", "Stage");
        FestivalArea created = new FestivalArea(1L, "Main Stage", "Primary performance area", "Stage");

        when(festivalAreaService.createArea(any(FestivalArea.class))).thenReturn(created);

        mockMvc.perform(post("/api/areas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Main Stage"));
    }

    @Test
    void getAllAreas_shouldReturn200WithAreaList() throws Exception {
        List<FestivalArea> areas = Arrays.asList(
                new FestivalArea(1L, "Main Stage", "Primary performance area", "Stage"),
                new FestivalArea(2L, "Food Court", "Dining area", "Food")
        );

        when(festivalAreaService.getAllAreas()).thenReturn(areas);

        mockMvc.perform(get("/api/areas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Main Stage"))
                .andExpect(jsonPath("$[1].name").value("Food Court"));
    }
}
