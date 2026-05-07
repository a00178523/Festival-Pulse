package com.ericsson.festivalpulse.service;

import com.ericsson.festivalpulse.model.Festival;
import com.ericsson.festivalpulse.repository.FestivalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FestivalServiceTest {

    @Mock FestivalRepository festivalRepository;

    @InjectMocks FestivalService festivalService;

    @Test
    void createFestival_savesAndReturnsFestival() {
        Festival festival = new Festival(null, "Electric Picnic", "Stradbally", null, null);
        when(festivalRepository.save(any())).thenAnswer(inv -> {
            Festival f = inv.getArgument(0);
            f.setId(1L);
            return f;
        });

        Festival result = festivalService.createFestival(festival);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Electric Picnic");
        verify(festivalRepository).save(festival);
    }

    @Test
    void getAllFestivals_returnsAllFestivals() {
        Festival f1 = new Festival(1L, "Electric Picnic", null, null, null);
        Festival f2 = new Festival(2L, "Longitude", null, null, null);
        when(festivalRepository.findAll()).thenReturn(List.of(f1, f2));

        List<Festival> result = festivalService.getAllFestivals();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Festival::getName)
                .containsExactly("Electric Picnic", "Longitude");
    }

    @Test
    void getAllFestivals_returnsEmptyList_whenNoFestivals() {
        when(festivalRepository.findAll()).thenReturn(List.of());

        assertThat(festivalService.getAllFestivals()).isEmpty();
    }

    @Test
    void getFestivalById_returnsFestival_whenExists() {
        Festival festival = new Festival(1L, "Electric Picnic", null, null, null);
        when(festivalRepository.findById(1L)).thenReturn(Optional.of(festival));

        Festival result = festivalService.getFestivalById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Electric Picnic");
    }

    @Test
    void getFestivalById_throwsNotFound_whenFestivalDoesNotExist() {
        when(festivalRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> festivalService.getFestivalById(999L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Festival not found");
    }
}
