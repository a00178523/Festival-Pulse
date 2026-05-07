package com.ericsson.festivalpulse.repository;

import com.ericsson.festivalpulse.model.AlertStatus;
import com.ericsson.festivalpulse.model.CrowdAlert;
import com.ericsson.festivalpulse.model.Festival;
import com.ericsson.festivalpulse.model.FestivalArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CrowdAlertRepository extends JpaRepository<CrowdAlert, Long> {
    List<CrowdAlert> findByAreaFestivalAndStatus(Festival festival, AlertStatus status);
    Optional<CrowdAlert> findByAreaAndStatus(FestivalArea area, AlertStatus status);
}
