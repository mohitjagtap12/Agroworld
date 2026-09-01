package com.agroworld.backend.repository;

import com.agroworld.backend.entity.ProduceOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduceOrderRepository extends JpaRepository<ProduceOrderEntity, String> {
    List<ProduceOrderEntity> findByFarmerId(String farmerId);
    List<ProduceOrderEntity> findByCustomerId(String customerId);
}
