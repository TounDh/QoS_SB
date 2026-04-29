package com.qosb.platform.repository;

import com.qosb.platform.entity.AllocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AllocationRepository extends JpaRepository<AllocationEntity, String> {

    Optional<AllocationEntity> findFirstByAnomalyId(String anomalyId);

    @Query("""
        SELECT a FROM AllocationEntity a
        WHERE (:priority    IS NULL OR LOWER(a.priority)    = LOWER(:priority))
          AND (:networkType IS NULL OR LOWER(a.networkType) = LOWER(:networkType))
        ORDER BY a.timestamp DESC
    """)
    List<AllocationEntity> search(String priority, String networkType);
}
