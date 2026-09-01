package com.agroworld.backend.repository;

import com.agroworld.backend.entity.ActivityRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRecordRepository extends JpaRepository<ActivityRecordEntity, String> {
    List<ActivityRecordEntity> findByUserIdOrderByCreatedAtDesc(String userId);
    List<ActivityRecordEntity> findByUserIdAndActivityType(String userId, String activityType);
}
