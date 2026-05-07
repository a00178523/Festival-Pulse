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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrowdAlertServiceBoundaryTest {

    @Mock CrowdAlertRepository crowdAlertRepository;

    @InjectMocks CrowdAlertService crowdAlertService;

    private Festival festival;
    private FestivalArea area1;
    private FestivalArea area2;

    @BeforeEach
    void setUp() {
        festival = new Festival(1L, "Electric Picnic", "Stradbally", null, null);
        area1 = new FestivalArea(10L, festival, "Main Stage", null, "Stage", null, null);
        area2 = new FestivalArea(11L, festival, "Food Village", null, "Food & Drink", null, null);
    }

    @Test
    void createAlertIfNotExists_setsCreatedAtTimestamp() {
        when(crowdAlertRepository.findByAreaAndStatus(area1, AlertStatus.ACTIVE))
                .thenReturn(Optional.empty());
        when(crowdAlertRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        crowdAlertService.createAlertIfNotExists(area1);

        ArgumentCaptor<CrowdAlert> captor = ArgumentCaptor.forClass(CrowdAlert.class);
        verify(crowdAlertRepository).save(captor.capture());
        assertThat(captor.getValue().getCreatedAt()).isNotNull();
    }

    @Test
    void createAlertIfNotExists_alertMessageContainsAreaName() {
        when(crowdAlertRepository.findByAreaAndStatus(area1, AlertStatus.ACTIVE))
                .thenReturn(Optional.empty());
        when(crowdAlertRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        crowdAlertService.createAlertIfNotExists(area1);

        ArgumentCaptor<CrowdAlert> captor = ArgumentCaptor.forClass(CrowdAlert.class);
        verify(crowdAlertRepository).save(captor.capture());
        assertThat(captor.getValue().getMessage()).contains("Main Stage");
    }

    @Test
    void createAlertIfNotExists_independentAlertsForDifferentAreas() {
        when(crowdAlertRepository.findByAreaAndStatus(area1, AlertStatus.ACTIVE))
                .thenReturn(Optional.empty());
        when(crowdAlertRepository.findByAreaAndStatus(area2, AlertStatus.ACTIVE))
                .thenReturn(Optional.empty());

        crowdAlertService.createAlertIfNotExists(area1);
        crowdAlertService.createAlertIfNotExists(area2);

        verify(crowdAlertRepository, times(2)).save(any());
    }

    @Test
    void getActiveAlerts_returnsEmptyList_whenNoActiveAlerts() {
        when(crowdAlertRepository.findByAreaFestivalAndStatus(festival, AlertStatus.ACTIVE))
                .thenReturn(List.of());

        assertThat(crowdAlertService.getActiveAlerts(festival)).isEmpty();
    }

    @Test
    void getActiveAlerts_returnsMultipleAlerts_whenMultipleAreasAreFull() {
        CrowdAlert alert1 = new CrowdAlert(1L, area1, "Main Stage is FULL!", AlertStatus.ACTIVE, LocalDateTime.now());
        CrowdAlert alert2 = new CrowdAlert(2L, area2, "Food Village is FULL!", AlertStatus.ACTIVE, LocalDateTime.now());
        when(crowdAlertRepository.findByAreaFestivalAndStatus(festival, AlertStatus.ACTIVE))
                .thenReturn(List.of(alert1, alert2));

        List<CrowdAlert> result = crowdAlertService.getActiveAlerts(festival);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CrowdAlert::getStatus)
                .containsOnly(AlertStatus.ACTIVE);
    }

    @Test
    void resolveAlert_doesNotAffectOtherAlerts() {
        CrowdAlert alertToResolve = new CrowdAlert(1L, area1, "Main Stage is FULL!", AlertStatus.ACTIVE, LocalDateTime.now());
        when(crowdAlertRepository.findById(1L)).thenReturn(Optional.of(alertToResolve));
        when(crowdAlertRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CrowdAlert result = crowdAlertService.resolveAlert(1L, 1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(AlertStatus.RESOLVED);
        // Only the one alert was saved
        verify(crowdAlertRepository, times(1)).save(any());
    }

    @Test
    void resolveAlert_alreadyResolvedAlert_canBeResolvedAgain() {
        // System doesn't prevent resolving an already-resolved alert — it just sets RESOLVED again
        CrowdAlert alreadyResolved = new CrowdAlert(1L, area1, "Main Stage is FULL!", AlertStatus.RESOLVED, LocalDateTime.now());
        when(crowdAlertRepository.findById(1L)).thenReturn(Optional.of(alreadyResolved));
        when(crowdAlertRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CrowdAlert result = crowdAlertService.resolveAlert(1L, 1L);

        assertThat(result.getStatus()).isEqualTo(AlertStatus.RESOLVED);
    }
}
