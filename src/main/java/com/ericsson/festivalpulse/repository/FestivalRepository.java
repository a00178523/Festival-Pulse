package com.ericsson.festivalpulse.repository;

import com.ericsson.festivalpulse.model.Festival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FestivalRepository extends JpaRepository<Festival, Long> {
}
