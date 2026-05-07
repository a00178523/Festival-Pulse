package com.ericsson.festivalpulse.service;

import com.ericsson.festivalpulse.model.Festival;
import com.ericsson.festivalpulse.model.FestivalArea;
import com.ericsson.festivalpulse.repository.FestivalAreaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FestivalAreaServiceTest {

    @Mock FestivalAreaRepository festivalAreaRepository;
    @Mock FestivalService festivalService;

    @InjectMocks FestivalAreaService festivalAreaService;

    private Festival festival;
    private FestivalArea area;

    @BeforeEach
    void setUp() {
        festival = new Festival(1L, "Electric Picnic", "Stradbally", null, null);
        area = new FestivalArea(null, null, "Main Stage", "Main stage area", "Stage", null, null);
    }

    @Test
    void createArea_savesAreaWithFestival() {
        when(festivalService.getFestivalById(1L)).thenReturn(festival);
        when(festivalAreaRepository.save(any())).thenAnswer(inv -> {
            FestivalArea saved = inv.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        FestivalArea result = festivalAreaService.createArea(1L, area);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Main Stage");
        assertThat(result.getFestival()).isEqualTo(festival);
        verify(festivalAreaRepository).save(area);
    }

    @Test
    void getAreasByFestival_returnsAreasForFestival() {
        FestivalArea area2 = new FestivalArea(2L, festival, "Food Village", "Food area", "Food & Drink", null, null);
        when(festivalService.getFestivalById(1L)).thenReturn(festival);
        when(festivalAreaRepository.findByFestival(festival)).thenReturn(List.of(area, area2));

        List<FestivalArea> result = festivalAreaService.getAreasByFestival(1L);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(FestivalArea::getName)
                .containsExactly("Main Stage", "Food Village");
    }

    @Test
    void getAreasByFestival_returnsEmptyList_whenNoAreas() {
        when(festivalService.getFestivalById(1L)).thenReturn(festival);
        when(festivalAreaRepository.findByFestival(festival)).thenReturn(List.of());

        List<FestivalArea> result = festivalAreaService.getAreasByFestival(1L);

        assertThat(result).isEmpty();
    }
}
