package com.agroworld.backend.repository;

import com.agroworld.backend.entity.AgriWasteListingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgriWasteListingRepository extends JpaRepository<AgriWasteListingEntity, String> {
    List<AgriWasteListingEntity> findByFarmerId(String farmerId);
    List<AgriWasteListingEntity> findByStatus(String status);
}
