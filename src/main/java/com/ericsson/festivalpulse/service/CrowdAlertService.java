package com.ericsson.festivalpulse.service;

import com.ericsson.festivalpulse.model.AlertStatus;
import com.ericsson.festivalpulse.model.CrowdAlert;
import com.ericsson.festivalpulse.model.Festival;
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

    public List<CrowdAlert> getActiveAlerts(Festival festival) {
        return crowdAlertRepository.findByAreaFestivalAndStatus(festival, AlertStatus.ACTIVE);
    }

    public CrowdAlert resolveAlert(Long festivalId, Long alertId) {
        CrowdAlert alert = crowdAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found"));
        if (!alert.getArea().getFestival().getId().equals(festivalId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found in this festival");
        }
        alert.setStatus(AlertStatus.RESOLVED);
        return crowdAlertRepository.save(alert);
    }
}
