package com.agroworld.backend.repository;

import com.agroworld.backend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmerCropRepository extends JpaRepository<FarmerCropEntity, String> {
    List<FarmerCropEntity> findByFarmerId(String farmerId);
}
