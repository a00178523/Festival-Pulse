package com.ericsson.festivalpulse.service;

import com.ericsson.festivalpulse.model.AlertStatus;
import com.ericsson.festivalpulse.model.CrowdAlert;
import com.ericsson.festivalpulse.model.FestivalArea;
import com.ericsson.festivalpulse.repository.CrowdAlertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Mock
    private CrowdAlertRepository crowdAlertRepository;

    @InjectMocks
    private CrowdAlertService crowdAlertService;

    private final FestivalArea area = new FestivalArea(1L, "Main Stage", "Primary area", "Stage");

    @Test
    void createAlertIfNotExists_noActiveAlert_shouldCreateAlert() {
        when(crowdAlertRepository.findByAreaAndStatus(area, AlertStatus.ACTIVE)).thenReturn(Optional.empty());

        crowdAlertService.createAlertIfNotExists(area);

        verify(crowdAlertRepository, times(1)).save(any(CrowdAlert.class));
    }

    @Test
    void createAlertIfNotExists_alertAlreadyActive_shouldNotCreateDuplicate() {
        CrowdAlert existing = new CrowdAlert(1L, area, "Main Stage is FULL!", AlertStatus.ACTIVE, LocalDateTime.now());
        when(crowdAlertRepository.findByAreaAndStatus(area, AlertStatus.ACTIVE)).thenReturn(Optional.of(existing));

        crowdAlertService.createAlertIfNotExists(area);

        verify(crowdAlertRepository, never()).save(any());
    }

    @Test
    void resolveAlert_existingAlert_shouldSetResolvedAndSave() {
        CrowdAlert alert = new CrowdAlert(1L, area, "Main Stage is FULL!", AlertStatus.ACTIVE, LocalDateTime.now());
        when(crowdAlertRepository.findById(1L)).thenReturn(Optional.of(alert));
        when(crowdAlertRepository.save(alert)).thenReturn(alert);

        CrowdAlert result = crowdAlertService.resolveAlert(1L);

        assertThat(result.getStatus()).isEqualTo(AlertStatus.RESOLVED);
        verify(crowdAlertRepository, times(1)).save(alert);
    }

    @Test
    void resolveAlert_missingAlert_shouldThrow404() {
        when(crowdAlertRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> crowdAlertService.resolveAlert(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Alert not found");
    }

    @Test
    void getActiveAlerts_shouldReturnActiveAlerts() {
        CrowdAlert alert = new CrowdAlert(1L, area, "Main Stage is FULL!", AlertStatus.ACTIVE, LocalDateTime.now());
        when(crowdAlertRepository.findByStatus(AlertStatus.ACTIVE)).thenReturn(List.of(alert));

        List<CrowdAlert> result = crowdAlertService.getActiveAlerts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(AlertStatus.ACTIVE);
        verify(crowdAlertRepository, times(1)).findByStatus(AlertStatus.ACTIVE);
    }
}
