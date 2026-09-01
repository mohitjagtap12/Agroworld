package com.agroworld.backend.repository;

import com.agroworld.backend.entity.DeliveryJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryJobRepository extends JpaRepository<DeliveryJobEntity, String> {
    List<DeliveryJobEntity> findByStatus(String status);
    List<DeliveryJobEntity> findByAssignedPartnerId(String assignedPartnerId);
    List<DeliveryJobEntity> findBySourceUserIdOrDestinationUserId(String sourceUserId, String destinationUserId);
}
