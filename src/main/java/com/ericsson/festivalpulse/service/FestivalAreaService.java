package com.ericsson.festivalpulse.service;

import com.ericsson.festivalpulse.model.FestivalArea;
import com.ericsson.festivalpulse.repository.FestivalAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FestivalAreaService {

    private final FestivalAreaRepository festivalAreaRepository;

    public FestivalArea createArea(FestivalArea area) {
        return festivalAreaRepository.save(area);
    }

    public List<FestivalArea> getAllAreas() {
        return festivalAreaRepository.findAll();
    }
}
