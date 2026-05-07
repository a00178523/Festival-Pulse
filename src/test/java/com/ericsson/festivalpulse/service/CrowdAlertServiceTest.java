package com.ericsson.festivalpulse.service;

import com.ericsson.festivalpulse.model.*;
import com.ericsson.festivalpulse.repository.CrowdAlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrowdAlertServiceTest {

    @Mock CrowdAlertRepository crowdAlertRepository;

    @InjectMocks CrowdAlertService crowdAlertService;

    private Festival festival;
    private FestivalArea area;

    @BeforeEach
    void setUp() {
        festival = new Festival(1L, "Electric Picnic", "Stradbally", null, null);
        area = new FestivalArea(10L, festival, "Main Stage", "Main stage", "Stage", null, null);
    }

    @Test
    void createAlertIfNotExists_createsAlert_whenNoneExists() {
        when(crowdAlertRepository.findByAreaAndStatus(area, AlertStatus.ACTIVE))
                .thenReturn(Optional.empty());

        crowdAlertService.createAlertIfNotExists(area);

        ArgumentCaptor<CrowdAlert> captor = ArgumentCaptor.forClass(CrowdAlert.class);
        verify(crowdAlertRepository).save(captor.capture());

        CrowdAlert saved = captor.getValue();
        assertThat(saved.getMessage()).isEqualTo("Main Stage is FULL!");
        assertThat(saved.getStatus()).isEqualTo(AlertStatus.ACTIVE);
        assertThat(saved.getArea()).isEqualTo(area);
    }

    @Test
    void createAlertIfNotExists_doesNotCreateDuplicate_whenActiveAlertExists() {
        CrowdAlert existing = new CrowdAlert(1L, area, "Main Stage is FULL!", AlertStatus.ACTIVE, LocalDateTime.now());
        when(crowdAlertRepository.findByAreaAndStatus(area, AlertStatus.ACTIVE))
                .thenReturn(Optional.of(existing));

        crowdAlertService.createAlertIfNotExists(area);

        verify(crowdAlertRepository, never()).save(any());
    }

    @Test
    void getActiveAlerts_returnsOnlyActiveAlertsForFestival() {
        CrowdAlert alert = new CrowdAlert(1L, area, "Main Stage is FULL!", AlertStatus.ACTIVE, LocalDateTime.now());
        when(crowdAlertRepository.findByAreaFestivalAndStatus(festival, AlertStatus.ACTIVE))
                .thenReturn(List.of(alert));

        List<CrowdAlert> result = crowdAlertService.getActiveAlerts(festival);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(AlertStatus.ACTIVE);
    }

    @Test
    void resolveAlert_setsStatusToResolved() {
        CrowdAlert alert = new CrowdAlert(1L, area, "Main Stage is FULL!", AlertStatus.ACTIVE, LocalDateTime.now());
        when(crowdAlertRepository.findById(1L)).thenReturn(Optional.of(alert));
        when(crowdAlertRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CrowdAlert result = crowdAlertService.resolveAlert(1L, 1L);

        assertThat(result.getStatus()).isEqualTo(AlertStatus.RESOLVED);
        verify(crowdAlertRepository).save(alert);
    }

    @Test
    void resolveAlert_throwsNotFound_whenAlertDoesNotExist() {
        when(crowdAlertRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> crowdAlertService.resolveAlert(1L, 99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Alert not found");
    }

    @Test
    void resolveAlert_throwsNotFound_whenAlertBelongsToDifferentFestival() {
        Festival otherFestival = new Festival(2L, "Longitude", "Dublin", null, null);
        FestivalArea otherArea = new FestivalArea(20L, otherFestival, "Second Stage", null, "Stage", null, null);
        CrowdAlert alert = new CrowdAlert(1L, otherArea, "Second Stage is FULL!", AlertStatus.ACTIVE, LocalDateTime.now());

        when(crowdAlertRepository.findById(1L)).thenReturn(Optional.of(alert));

        assertThatThrownBy(() -> crowdAlertService.resolveAlert(1L, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Alert not found in this festival");
    }

    @Test
    void hasActiveAlert_returnsTrue_whenActiveAlertExists() {
        when(crowdAlertRepository.findByAreaAndStatus(area, AlertStatus.ACTIVE))
                .thenReturn(Optional.of(new CrowdAlert()));

        assertThat(crowdAlertService.hasActiveAlert(area)).isTrue();
    }

    @Test
    void hasActiveAlert_returnsFalse_whenNoActiveAlert() {
        when(crowdAlertRepository.findByAreaAndStatus(area, AlertStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThat(crowdAlertService.hasActiveAlert(area)).isFalse();
    }
}
