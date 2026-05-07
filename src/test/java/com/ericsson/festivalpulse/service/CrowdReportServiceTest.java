package com.ericsson.festivalpulse.service;

import com.ericsson.festivalpulse.model.CrowdLevel;
import com.ericsson.festivalpulse.model.CrowdReport;
import com.ericsson.festivalpulse.model.FestivalArea;
import com.ericsson.festivalpulse.repository.CrowdReportRepository;
import com.ericsson.festivalpulse.repository.FestivalAreaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrowdReportServiceTest {

    @Mock
    private CrowdReportRepository crowdReportRepository;

    @Mock
    private FestivalAreaRepository festivalAreaRepository;

    @Mock
    private CrowdAlertService crowdAlertService;

    @InjectMocks
    private CrowdReportService crowdReportService;

    private final FestivalArea area = new FestivalArea(1L, "Main Stage", "Primary area", "Stage");

    @Test
    void submitReport_validArea_shouldSaveAndReturnReport() {
        CrowdReport saved = new CrowdReport(1L, area, CrowdLevel.MEDIUM, "Filling up", LocalDateTime.now());

        when(festivalAreaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(crowdReportRepository.save(any(CrowdReport.class))).thenReturn(saved);

        CrowdReport result = crowdReportService.submitReport(1L, CrowdLevel.MEDIUM, "Filling up");

        assertThat(result.getCrowdLevel()).isEqualTo(CrowdLevel.MEDIUM);
        assertThat(result.getArea().getId()).isEqualTo(1L);
        verify(crowdReportRepository, times(1)).save(any(CrowdReport.class));
    }

    @Test
    void submitReport_missingArea_shouldThrow404() {
        when(festivalAreaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> crowdReportService.submitReport(99L, CrowdLevel.LOW, null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Area not found");

        verify(crowdReportRepository, never()).save(any());
    }

    @Test
    void submitReport_fullCrowdLevel_shouldTriggerAlert() {
        CrowdReport saved = new CrowdReport(1L, area, CrowdLevel.FULL, null, LocalDateTime.now());

        when(festivalAreaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(crowdReportRepository.save(any(CrowdReport.class))).thenReturn(saved);

        crowdReportService.submitReport(1L, CrowdLevel.FULL, null);

        verify(crowdAlertService, times(1)).createAlertIfNotExists(area);
    }

    @Test
    void submitReport_nonFullCrowdLevel_shouldNotTriggerAlert() {
        CrowdReport saved = new CrowdReport(1L, area, CrowdLevel.LOW, null, LocalDateTime.now());

        when(festivalAreaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(crowdReportRepository.save(any(CrowdReport.class))).thenReturn(saved);

        crowdReportService.submitReport(1L, CrowdLevel.LOW, null);

        verify(crowdAlertService, never()).createAlertIfNotExists(any());
    }
}
