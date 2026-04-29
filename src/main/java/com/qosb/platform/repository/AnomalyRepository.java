package com.qosb.platform.repository;

import com.qosb.platform.entity.AnomalyEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnomalyRepository extends JpaRepository<AnomalyEntity, String> {

    @Query("""
        SELECT a FROM AnomalyEntity a
        WHERE (:onlyAnomalies = false OR a.isAnomaly = true)
          AND (:networkType IS NULL OR LOWER(a.networkType)   = LOWER(:networkType))
          AND (:severity    IS NULL OR LOWER(a.severityLabel) = LOWER(:severity))
        ORDER BY a.timestamp DESC
    """)
    List<AnomalyEntity> search(boolean onlyAnomalies, String networkType, String severity, Pageable pageable);

    List<AnomalyEntity> findAllByIsAnomalyTrue();
}
