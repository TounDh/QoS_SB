package com.qosb.platform.repository;

import com.qosb.platform.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReportRepository extends JpaRepository<ReportEntity, String> {

    @Query("""
        SELECT r FROM ReportEntity r
        WHERE (:networkType IS NULL OR LOWER(r.networkType)   = LOWER(:networkType))
          AND (:severity    IS NULL OR LOWER(r.severityLabel) = LOWER(:severity))
        ORDER BY r.timestamp DESC
    """)
    List<ReportEntity> search(String networkType, String severity);
}
