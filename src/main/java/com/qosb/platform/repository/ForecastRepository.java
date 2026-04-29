package com.qosb.platform.repository;

import com.qosb.platform.entity.ForecastEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ForecastRepository extends JpaRepository<ForecastEntity, Long> {

    @Query("""
        SELECT f FROM ForecastEntity f
        WHERE (:networkType IS NULL OR LOWER(f.networkType) = LOWER(:networkType))
          AND (:metric      IS NULL OR LOWER(f.metric)      = LOWER(:metric))
        ORDER BY f.timestamp ASC
    """)
    List<ForecastEntity> search(String networkType, String metric, Pageable pageable);
}
