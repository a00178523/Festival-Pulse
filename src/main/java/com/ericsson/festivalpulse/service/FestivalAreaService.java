package com.ericsson.festivalpulse.service;

import com.ericsson.festivalpulse.model.Festival;
import com.ericsson.festivalpulse.model.FestivalArea;
import com.ericsson.festivalpulse.repository.FestivalAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FestivalAreaService {

    private final FestivalAreaRepository festivalAreaRepository;
    private final FestivalService festivalService;

    public FestivalArea createArea(Long festivalId, FestivalArea area) {
        Festival festival = festivalService.getFestivalById(festivalId);
        area.setFestival(festival);
        return festivalAreaRepository.save(area);
    }

    public List<FestivalArea> getAreasByFestival(Long festivalId) {
        Festival festival = festivalService.getFestivalById(festivalId);
        return festivalAreaRepository.findByFestival(festival);
    }
}
