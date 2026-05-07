package com.ericsson.festivalpulse.controller;

import com.ericsson.festivalpulse.model.FestivalArea;
import com.ericsson.festivalpulse.service.FestivalAreaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/festivals/{festivalId}/areas")
@RequiredArgsConstructor
public class FestivalAreaController {

    private final FestivalAreaService festivalAreaService;

    @PostMapping
    public ResponseEntity<FestivalArea> createArea(@PathVariable Long festivalId, @Valid @RequestBody AreaRequest request) {
        FestivalArea area = new FestivalArea(null, null, request.getName(), request.getDescription(), request.getAreaType());
        return ResponseEntity.status(HttpStatus.CREATED).body(festivalAreaService.createArea(festivalId, area));
    }

    @GetMapping
    public ResponseEntity<List<FestivalArea>> getAllAreas(@PathVariable Long festivalId) {
        return ResponseEntity.ok(festivalAreaService.getAreasByFestival(festivalId));
    }

    @Data
    static class AreaRequest {
        @NotBlank(message = "Name is required")
        private String name;
        private String description;
        private String areaType;
    }
}
