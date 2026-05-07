package com.ericsson.festivalpulse.service;

import com.ericsson.festivalpulse.model.AlertStatus;
import com.ericsson.festivalpulse.model.CrowdAlert;
import com.ericsson.festivalpulse.model.FestivalArea;
import com.ericsson.festivalpulse.repository.CrowdAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CrowdAlertService {

    private final CrowdAlertRepository crowdAlertRepository;

    public void createAlertIfNotExists(FestivalArea area) {
        boolean alreadyActive = crowdAlertRepository.findByAreaAndStatus(area, AlertStatus.ACTIVE).isPresent();
        if (!alreadyActive) {
            CrowdAlert alert = new CrowdAlert(null, area, area.getName() + " is FULL!", AlertStatus.ACTIVE, LocalDateTime.now());
            crowdAlertRepository.save(alert);
        }
    }

    public List<CrowdAlert> getActiveAlerts() {
        return crowdAlertRepository.findByStatus(AlertStatus.ACTIVE);
    }

    public CrowdAlert resolveAlert(Long id) {
        CrowdAlert alert = crowdAlertRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found"));
        alert.setStatus(AlertStatus.RESOLVED);
        return crowdAlertRepository.save(alert);
    }
}
