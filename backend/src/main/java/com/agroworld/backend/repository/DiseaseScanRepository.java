package com.agroworld.backend.repository;

import com.agroworld.backend.entity.DiseaseScanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiseaseScanRepository extends JpaRepository<DiseaseScanEntity, String> {
    List<DiseaseScanEntity> findByFarmerIdOrderByCreatedAtDesc(String farmerId);
}
