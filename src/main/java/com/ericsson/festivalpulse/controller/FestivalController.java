package com.ericsson.festivalpulse.controller;

import com.ericsson.festivalpulse.model.Festival;
import com.ericsson.festivalpulse.service.FestivalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/festivals")
@RequiredArgsConstructor
public class FestivalController {

    private final FestivalService festivalService;

    @PostMapping
    public ResponseEntity<Festival> createFestival(@Valid @RequestBody Festival festival) {
        return ResponseEntity.status(HttpStatus.CREATED).body(festivalService.createFestival(festival));
    }

    @GetMapping
    public ResponseEntity<List<Festival>> getAllFestivals() {
        return ResponseEntity.ok(festivalService.getAllFestivals());
    }
}
