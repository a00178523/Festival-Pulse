package com.ericsson.festivalpulse.repository;

import com.ericsson.festivalpulse.model.FestivalArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FestivalAreaRepository extends JpaRepository<FestivalArea, Long> {
}
