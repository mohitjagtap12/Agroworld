package com.agroworld.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "broker_requirements", indexes = {
        @Index(name = "idx_broker_req_crop", columnList = "crop"),
        @Index(name = "idx_broker_req_broker", columnList = "broker_id")
})
public class BrokerRequirementEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "broker_id", nullable = false, length = 64)
    private String brokerId;

    @Column(nullable = false, length = 128)
    private String crop;

    @Column(name = "required_quantity", nullable = false)
    private Double requiredQuantity;

    @Column(length = 32)
    private String unit = "Quintals";

    @Column(name = "offered_price", nullable = false)
    private Double offeredPrice;

    @Column(name = "price_unit", length = 32)
    private String priceUnit = "per Quintal";

    @Column(name = "quality_requirement", columnDefinition = "TEXT")
    private String qualityRequirement;

    @Column(name = "required_date")
    private LocalDate requiredDate;

    @Column(name = "pickup_location", length = 255)
    private String pickupLocation;

    @Column(name = "payment_terms", length = 255)
    private String paymentTerms;

    @Column(length = 32)
    private String status = "OPEN"; // OPEN, IN_NEGOTIATION, DEAL_CLOSED, EXPIRED

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public BrokerRequirementEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBrokerId() { return brokerId; }
    public void setBrokerId(String brokerId) { this.brokerId = brokerId; }
    public String getCrop() { return crop; }
    public void setCrop(String crop) { this.crop = crop; }
    public Double getRequiredQuantity() { return requiredQuantity; }
    public void setRequiredQuantity(Double requiredQuantity) { this.requiredQuantity = requiredQuantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Double getOfferedPrice() { return offeredPrice; }
    public void setOfferedPrice(Double offeredPrice) { this.offeredPrice = offeredPrice; }
    public String getPriceUnit() { return priceUnit; }
    public void setPriceUnit(String priceUnit) { this.priceUnit = priceUnit; }
    public String getQualityRequirement() { return qualityRequirement; }
    public void setQualityRequirement(String qualityRequirement) { this.qualityRequirement = qualityRequirement; }
    public LocalDate getRequiredDate() { return requiredDate; }
    public void setRequiredDate(LocalDate requiredDate) { this.requiredDate = requiredDate; }
    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
