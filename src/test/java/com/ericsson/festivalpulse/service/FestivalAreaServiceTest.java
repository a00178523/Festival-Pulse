package com.ericsson.festivalpulse.service;

import com.ericsson.festivalpulse.model.FestivalArea;
import com.ericsson.festivalpulse.repository.FestivalAreaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FestivalAreaServiceTest {

    @Mock
    private FestivalAreaRepository festivalAreaRepository;

    @InjectMocks
    private FestivalAreaService festivalAreaService;

    @Test
    void createArea_shouldSaveAndReturnArea() {
        FestivalArea input = new FestivalArea(null, "Main Stage", "Primary performance area", "Stage");
        FestivalArea saved = new FestivalArea(1L, "Main Stage", "Primary performance area", "Stage");

        when(festivalAreaRepository.save(any(FestivalArea.class))).thenReturn(saved);

        FestivalArea result = festivalAreaService.createArea(input);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Main Stage");
        verify(festivalAreaRepository, times(1)).save(input);
    }

    @Test
    void getAllAreas_shouldReturnAllAreas() {
        List<FestivalArea> areas = Arrays.asList(
                new FestivalArea(1L, "Main Stage", "Primary performance area", "Stage"),
                new FestivalArea(2L, "Food Court", "Dining area", "Food")
        );

        when(festivalAreaRepository.findAll()).thenReturn(areas);

        List<FestivalArea> result = festivalAreaService.getAllAreas();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Main Stage");
        verify(festivalAreaRepository, times(1)).findAll();
    }
}
