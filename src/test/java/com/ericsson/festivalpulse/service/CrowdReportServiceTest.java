package com.ericsson.festivalpulse.service;

import com.ericsson.festivalpulse.model.*;
import com.ericsson.festivalpulse.repository.CrowdReportRepository;
import com.ericsson.festivalpulse.repository.FestivalAreaRepository;
import org.junit.jupiter.api.BeforeEach;
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
class CrowdReportServiceTest {

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
    void submitReport_savesReportSuccessfully() {
        when(festivalService.getFestivalById(1L)).thenReturn(festival);
        when(festivalAreaRepository.findById(10L)).thenReturn(Optional.of(area));
        when(crowdAlertService.hasActiveAlert(area)).thenReturn(false);
        when(crowdReportRepository.save(any())).thenAnswer(inv -> {
            CrowdReport r = inv.getArgument(0);
            r.setId(99L);
            return r;
        });

        CrowdReport result = crowdReportService.submitReport(1L, 10L, CrowdLevel.MEDIUM, "Getting busy");

        assertThat(result.getId()).isEqualTo(99L);
        assertThat(result.getCrowdLevel()).isEqualTo(CrowdLevel.MEDIUM);
        assertThat(result.getNote()).isEqualTo("Getting busy");
        assertThat(result.getArea()).isEqualTo(area);
        verify(crowdReportRepository).save(any());
    }

    @Test
    void submitReport_throwsNotFound_whenAreaDoesNotExist() {
        when(festivalService.getFestivalById(1L)).thenReturn(festival);
        when(festivalAreaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> crowdReportService.submitReport(1L, 99L, CrowdLevel.LOW, "note"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Area not found in this festival");

        verify(crowdReportRepository, never()).save(any());
    }

    @Test
    void submitReport_throwsNotFound_whenAreaBelongsToDifferentFestival() {
        Festival otherFestival = new Festival(2L, "Longitude", "Dublin", null, null);
        FestivalArea otherArea = new FestivalArea(10L, otherFestival, "Main Stage", null, "Stage", null, null);

        when(festivalService.getFestivalById(1L)).thenReturn(festival);
        when(festivalAreaRepository.findById(10L)).thenReturn(Optional.of(otherArea));

        assertThatThrownBy(() -> crowdReportService.submitReport(1L, 10L, CrowdLevel.LOW, "note"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Area not found in this festival");
    }

    @Test
    void submitReport_throwsConflict_whenAreaHasActiveAlert() {
        when(festivalService.getFestivalById(1L)).thenReturn(festival);
        when(festivalAreaRepository.findById(10L)).thenReturn(Optional.of(area));
        when(crowdAlertService.hasActiveAlert(area)).thenReturn(true);

        assertThatThrownBy(() -> crowdReportService.submitReport(1L, 10L, CrowdLevel.MEDIUM, "note"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Area already has an active alert");

        verify(crowdReportRepository, never()).save(any());
    }

    @Test
    void submitReport_createsAlert_whenCrowdLevelIsFull() {
        when(festivalService.getFestivalById(1L)).thenReturn(festival);
        when(festivalAreaRepository.findById(10L)).thenReturn(Optional.of(area));
        when(crowdAlertService.hasActiveAlert(area)).thenReturn(false);
        when(crowdReportRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        crowdReportService.submitReport(1L, 10L, CrowdLevel.FULL, "Packed out");

        verify(crowdAlertService).createAlertIfNotExists(area);
    }

    @Test
    void submitReport_doesNotCreateAlert_whenCrowdLevelIsNotFull() {
        when(festivalService.getFestivalById(1L)).thenReturn(festival);
        when(festivalAreaRepository.findById(10L)).thenReturn(Optional.of(area));
        when(crowdAlertService.hasActiveAlert(area)).thenReturn(false);
        when(crowdReportRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        crowdReportService.submitReport(1L, 10L, CrowdLevel.MEDIUM, "Busy");

        verify(crowdAlertService, never()).createAlertIfNotExists(any());
    }

    @Test
    void getRecentReports_returnsReportsForFestival() {
        CrowdReport report = new CrowdReport(1L, area, CrowdLevel.LOW, "Quiet", null);
        when(festivalService.getFestivalById(1L)).thenReturn(festival);
        when(crowdReportRepository.findTop20ByAreaFestivalOrderBySubmittedAtDesc(festival))
                .thenReturn(List.of(report));

        List<CrowdReport> result = crowdReportService.getRecentReports(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCrowdLevel()).isEqualTo(CrowdLevel.LOW);
    }
}
