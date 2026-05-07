package com.ericsson.festivalpulse.service;

import com.ericsson.festivalpulse.model.CrowdLevel;
import com.ericsson.festivalpulse.model.CrowdReport;
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

    public CrowdReport submitReport(Long areaId, CrowdLevel crowdLevel, String note) {
        FestivalArea area = festivalAreaRepository.findById(areaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Area not found"));

        CrowdReport report = new CrowdReport(null, area, crowdLevel, note, LocalDateTime.now());
        CrowdReport saved = crowdReportRepository.save(report);

        if (crowdLevel == CrowdLevel.FULL) {
            crowdAlertService.createAlertIfNotExists(area);
        }

        return saved;
    }

    public List<CrowdReport> getRecentReports() {
        return crowdReportRepository.findTop20ByOrderBySubmittedAtDesc();
    }
}
