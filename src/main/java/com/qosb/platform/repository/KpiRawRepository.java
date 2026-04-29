package com.qosb.platform.repository;

import com.qosb.platform.entity.KpiRawEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KpiRawRepository extends JpaRepository<KpiRawEntity, String> {
}
