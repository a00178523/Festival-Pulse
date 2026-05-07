package com.ericsson.festivalpulse.service;

import com.ericsson.festivalpulse.model.Festival;
import com.ericsson.festivalpulse.repository.FestivalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FestivalService {

    private final FestivalRepository festivalRepository;

    public Festival createFestival(Festival festival) {
        return festivalRepository.save(festival);
    }

    public List<Festival> getAllFestivals() {
        return festivalRepository.findAll();
    }

    public Festival getFestivalById(Long id) {
        return festivalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Festival not found"));
    }
}
