package com.agroworld.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "broker_offers")
public class BrokerOfferEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "requirement_id", nullable = false, length = 64)
    private String requirementId;

    @Column(name = "farmer_id", nullable = false, length = 64)
    private String farmerId;

    @Column(name = "crop_id", length = 64)
    private String cropId;

    @Column(name = "available_quantity", nullable = false)
    private Double availableQuantity;

    @Column(name = "expected_price", nullable = false)
    private Double expectedPrice;

    @Column(name = "available_date")
    private LocalDate availableDate;

    @Column(name = "quality_details", columnDefinition = "TEXT")
    private String qualityDetails;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(length = 32)
    private String status = "OFFERED"; // OFFERED, COUNTERED, ACCEPTED, REJECTED

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public BrokerOfferEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRequirementId() { return requirementId; }
    public void setRequirementId(String requirementId) { this.requirementId = requirementId; }
    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }
    public String getCropId() { return cropId; }
    public void setCropId(String cropId) { this.cropId = cropId; }
    public Double getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(Double availableQuantity) { this.availableQuantity = availableQuantity; }
    public Double getExpectedPrice() { return expectedPrice; }
    public void setExpectedPrice(Double expectedPrice) { this.expectedPrice = expectedPrice; }
    public LocalDate getAvailableDate() { return availableDate; }
    public void setAvailableDate(LocalDate availableDate) { this.availableDate = availableDate; }
    public String getQualityDetails() { return qualityDetails; }
    public void setQualityDetails(String qualityDetails) { this.qualityDetails = qualityDetails; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
