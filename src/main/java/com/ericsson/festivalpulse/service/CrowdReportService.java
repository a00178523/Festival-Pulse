package com.ericsson.festivalpulse.service;

import com.ericsson.festivalpulse.model.CrowdLevel;
import com.ericsson.festivalpulse.model.CrowdReport;
import com.ericsson.festivalpulse.model.Festival;
import com.ericsson.festivalpulse.model.FestivalArea;
import com.ericsson.festivalpulse.repository.CrowdReportRepository;
import com.ericsson.festivalpulse.repository.FestivalAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CrowdReportService {

    private final CrowdReportRepository crowdReportRepository;
    private final FestivalAreaRepository festivalAreaRepository;
    private final CrowdAlertService crowdAlertService;
    private final FestivalService festivalService;

    public CrowdReport submitReport(Long festivalId, Long areaId, CrowdLevel crowdLevel, String note) {
        festivalService.getFestivalById(festivalId);

        FestivalArea area = festivalAreaRepository.findById(areaId)
                .filter(a -> a.getFestival().getId().equals(festivalId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Area not found in this festival"));

        CrowdReport report = new CrowdReport(null, area, crowdLevel, note, LocalDateTime.now());
        CrowdReport saved = crowdReportRepository.save(report);

        if (crowdLevel == CrowdLevel.FULL) {
            crowdAlertService.createAlertIfNotExists(area);
        }

        return saved;
    }

    public List<CrowdReport> getRecentReports(Long festivalId) {
        Festival festival = festivalService.getFestivalById(festivalId);
        return crowdReportRepository.findTop20ByAreaFestivalOrderBySubmittedAtDesc(festival);
    }
}
