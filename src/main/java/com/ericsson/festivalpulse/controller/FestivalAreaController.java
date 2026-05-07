package com.ericsson.festivalpulse.controller;

import com.ericsson.festivalpulse.model.FestivalArea;
import com.ericsson.festivalpulse.service.FestivalAreaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/areas")
@RequiredArgsConstructor
public class FestivalAreaController {

    private final FestivalAreaService festivalAreaService;

    @PostMapping
    public ResponseEntity<FestivalArea> createArea(@Valid @RequestBody FestivalArea area) {
        FestivalArea created = festivalAreaService.createArea(area);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<FestivalArea>> getAllAreas() {
        return ResponseEntity.ok(festivalAreaService.getAllAreas());
    }
}
