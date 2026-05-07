package com.ericsson.festivalpulse.service;

import com.ericsson.festivalpulse.model.*;
import com.ericsson.festivalpulse.repository.CrowdReportRepository;
import com.ericsson.festivalpulse.repository.FestivalAreaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrowdReportServiceBoundaryTest {

    @Mock CrowdReportRepository crowdReportRepository;
    @Mock FestivalAreaRepository festivalAreaRepository;
    @Mock CrowdAlertService crowdAlertService;
    @Mock FestivalService festivalService;

    @InjectMocks CrowdReportService crowdReportService;

    private Festival festival;
    private FestivalArea area;

    @BeforeEach
    void setUp() {
        festival = new Festival(1L, "Electric Picnic", "Stradbally", null, null);
        area = new FestivalArea(10L, festival, "Main Stage", "Main stage", "Stage", null, null);
    }

    @Test
    void submitReport_withNullNote_savesSuccessfully() {
        when(festivalService.getFestivalById(1L)).thenReturn(festival);
        when(festivalAreaRepository.findById(10L)).thenReturn(Optional.of(area));
        when(crowdAlertService.hasActiveAlert(area)).thenReturn(false);
        when(crowdReportRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CrowdReport result = crowdReportService.submitReport(1L, 10L, CrowdLevel.LOW, null);

        assertThat(result.getNote()).isNull();
        verify(crowdReportRepository).save(any());
    }

    @Test
    void submitReport_withEmptyNote_savesSuccessfully() {
        when(festivalService.getFestivalById(1L)).thenReturn(festival);
        when(festivalAreaRepository.findById(10L)).thenReturn(Optional.of(area));
        when(crowdAlertService.hasActiveAlert(area)).thenReturn(false);
        when(crowdReportRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CrowdReport result = crowdReportService.submitReport(1L, 10L, CrowdLevel.LOW, "");

        assertThat(result.getNote()).isEmpty();
    }

    @Test
    void submitReport_setsSubmittedAtTimestamp() {
        when(festivalService.getFestivalById(1L)).thenReturn(festival);
        when(festivalAreaRepository.findById(10L)).thenReturn(Optional.of(area));
        when(crowdAlertService.hasActiveAlert(area)).thenReturn(false);
        when(crowdReportRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CrowdReport result = crowdReportService.submitReport(1L, 10L, CrowdLevel.LOW, "note");

        assertThat(result.getSubmittedAt()).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(value = CrowdLevel.class, names = {"LOW", "MEDIUM"})
    void submitReport_doesNotCreateAlert_forNonFullLevels(CrowdLevel level) {
        when(festivalService.getFestivalById(1L)).thenReturn(festival);
        when(festivalAreaRepository.findById(10L)).thenReturn(Optional.of(area));
        when(crowdAlertService.hasActiveAlert(area)).thenReturn(false);
        when(crowdReportRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        crowdReportService.submitReport(1L, 10L, level, "note");

        verify(crowdAlertService, never()).createAlertIfNotExists(any());
    }

    @Test
    void submitReport_onlyFullLevel_createsAlert() {
        when(festivalService.getFestivalById(1L)).thenReturn(festival);
        when(festivalAreaRepository.findById(10L)).thenReturn(Optional.of(area));
        when(crowdAlertService.hasActiveAlert(area)).thenReturn(false);
        when(crowdReportRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        crowdReportService.submitReport(1L, 10L, CrowdLevel.FULL, "note");

        verify(crowdAlertService, times(1)).createAlertIfNotExists(area);
    }
}
