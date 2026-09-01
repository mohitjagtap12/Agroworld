package com.agroworld.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_records", indexes = {
        @Index(name = "idx_act_user", columnList = "user_id"),
        @Index(name = "idx_act_type", columnList = "activity_type")
})
public class ActivityRecordEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Column(name = "activity_type", nullable = false, length = 64)
    private String activityType; // LABOUR, WASTE, PRODUCT_ORDERS, CONTRACTS, BROKER_DEALS, PRODUCE_SALES, DELIVERY, DISEASE_SCAN

    @Column(name = "related_entity_id", length = 64)
    private String relatedEntityId;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 64)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public ActivityRecordEntity() {}

    public ActivityRecordEntity(String id, String userId, String activityType, String relatedEntityId, String title, String description, String status) {
        this.id = id;
        this.userId = userId;
        this.activityType = activityType;
        this.relatedEntityId = relatedEntityId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }
    public String getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(String relatedEntityId) { this.relatedEntityId = relatedEntityId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
