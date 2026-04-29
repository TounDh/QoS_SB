package com.qosb.platform.repository;

import com.qosb.platform.entity.KpiAggregatedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface KpiAggregatedRepository extends JpaRepository<KpiAggregatedEntity, Long> {

    @Query("""
        SELECT k FROM KpiAggregatedEntity k
        WHERE (:networkType IS NULL OR LOWER(k.networkType) = LOWER(:networkType))
          AND (:from IS NULL OR k.timestamp >= :from)
          AND (:to   IS NULL OR k.timestamp <= :to)
        ORDER BY k.timestamp ASC
    """)
    List<KpiAggregatedEntity> search(String networkType, Instant from, Instant to);
}
