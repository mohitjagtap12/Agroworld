package com.agroworld.backend.repository;

import com.agroworld.backend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrokerRequirementRepository extends JpaRepository<BrokerRequirementEntity, String> {
    List<BrokerRequirementEntity> findByBrokerId(String brokerId);
    List<BrokerRequirementEntity> findByStatus(String status);
}
