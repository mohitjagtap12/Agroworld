package com.agroworld.backend.repository;

import com.agroworld.backend.entity.ProductOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrderEntity, String> {
    List<ProductOrderEntity> findByFarmerId(String farmerId);
    List<ProductOrderEntity> findBySellerId(String sellerId);
}
