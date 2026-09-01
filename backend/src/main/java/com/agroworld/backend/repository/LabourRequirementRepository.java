package com.agroworld.backend.repository;

import com.agroworld.backend.entity.LabourRequirementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabourRequirementRepository extends JpaRepository<LabourRequirementEntity, String> {
    List<LabourRequirementEntity> findByFarmerId(String farmerId);
    List<LabourRequirementEntity> findByStatus(String status);
}
