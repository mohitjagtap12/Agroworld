package com.agroworld.backend.repository;

import com.agroworld.backend.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, String> {
    List<ProductEntity> findBySellerId(String sellerId);
    List<ProductEntity> findByCategory(String category);
    List<ProductEntity> findByStatus(String status);
}
