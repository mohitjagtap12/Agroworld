package com.agroworld.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "broker_deals", indexes = {
        @Index(name = "idx_deals_broker", columnList = "broker_id"),
        @Index(name = "idx_deals_farmer", columnList = "farmer_id")
})
public class BrokerDealEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "requirement_id", nullable = false, length = 64)
    private String requirementId;

    @Column(name = "offer_id", nullable = false, length = 64)
    private String offerId;

    @Column(name = "broker_id", nullable = false, length = 64)
    private String brokerId;

    @Column(name = "farmer_id", nullable = false, length = 64)
    private String farmerId;

    @Column(nullable = false, length = 128)
    private String crop;

    @Column(nullable = false)
    private Double quantity;

    @Column(length = 32)
    private String unit = "Quintals";

    @Column(name = "agreed_price", nullable = false)
    private Double agreedPrice;

    @Column(name = "price_unit", length = 32)
    private String priceUnit = "per Quintal";

    @Column(name = "total_value", nullable = false)
    private Double totalValue;

    @Column(name = "pickup_date")
    private LocalDate pickupDate;

    @Column(name = "pickup_location", length = 255)
    private String pickupLocation;

    @Column(length = 32)
    private String status = "CONFIRMED"; // CONFIRMED, PICKUP_SCHEDULED, IN_TRANSIT, COMPLETED, DISPUTED

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public BrokerDealEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRequirementId() { return requirementId; }
    public void setRequirementId(String requirementId) { this.requirementId = requirementId; }
    public String getOfferId() { return offerId; }
    public void setOfferId(String offerId) { this.offerId = offerId; }
    public String getBrokerId() { return brokerId; }
    public void setBrokerId(String brokerId) { this.brokerId = brokerId; }
    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }
    public String getCrop() { return crop; }
    public void setCrop(String crop) { this.crop = crop; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Double getAgreedPrice() { return agreedPrice; }
    public void setAgreedPrice(Double agreedPrice) { this.agreedPrice = agreedPrice; }
    public String getPriceUnit() { return priceUnit; }
    public void setPriceUnit(String priceUnit) { this.priceUnit = priceUnit; }
    public Double getTotalValue() { return totalValue; }
    public void setTotalValue(Double totalValue) { this.totalValue = totalValue; }
    public LocalDate getPickupDate() { return pickupDate; }
    public void setPickupDate(LocalDate pickupDate) { this.pickupDate = pickupDate; }
    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
