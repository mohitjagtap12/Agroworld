package com.agroworld.backend.repository;

import com.agroworld.backend.entity.ProduceListingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduceListingRepository extends JpaRepository<ProduceListingEntity, String> {
    List<ProduceListingEntity> findByFarmerId(String farmerId);
    List<ProduceListingEntity> findByCategory(String category);
    List<ProduceListingEntity> findByStatus(String status);
}
