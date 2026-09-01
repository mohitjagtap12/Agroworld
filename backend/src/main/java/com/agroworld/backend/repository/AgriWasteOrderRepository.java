package com.agroworld.backend.repository;

import com.agroworld.backend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgriWasteOrderRepository extends JpaRepository<AgriWasteOrderEntity, String> {
    List<AgriWasteOrderEntity> findByFarmerId(String farmerId);
    List<AgriWasteOrderEntity> findByBuyerId(String buyerId);
}
