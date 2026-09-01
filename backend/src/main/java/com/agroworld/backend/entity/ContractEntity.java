package com.agroworld.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contracts", indexes = {
        @Index(name = "idx_contracts_company", columnList = "company_id"),
        @Index(name = "idx_contracts_crop", columnList = "crop")
})
public class ContractEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "company_id", nullable = false, length = 64)
    private String companyId;

    @Column(nullable = false, length = 128)
    private String crop;

    @Column(name = "required_quantity", nullable = false)
    private Double requiredQuantity;

    @Column(length = 32)
    private String unit = "Tons";

    @Column(name = "offered_price", nullable = false)
    private Double offeredPrice;

    @Column(name = "price_unit", length = 32)
    private String priceUnit = "per Quintal";

    @Column(name = "quality_requirements", columnDefinition = "TEXT")
    private String qualityRequirements;

    @Column(name = "harvest_period", length = 128)
    private String harvestPeriod;

    @Column(name = "delivery_location", length = 255)
    private String deliveryLocation;

    @Column(name = "application_deadline")
    private LocalDate applicationDeadline;

    @Column(name = "contract_duration", length = 64)
    private String contractDuration;

    @Column(name = "payment_terms", length = 255)
    private String paymentTerms;

    @Column(name = "additional_conditions", columnDefinition = "TEXT")
    private String additionalConditions;

    @Column(length = 32)
    private String status = "ACTIVE"; // ACTIVE, APPLICATIONS_CLOSED, ALLOCATED, COMPLETED, CANCELLED

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public ContractEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
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
    public String getQualityRequirements() { return qualityRequirements; }
    public void setQualityRequirements(String qualityRequirements) { this.qualityRequirements = qualityRequirements; }
    public String getHarvestPeriod() { return harvestPeriod; }
    public void setHarvestPeriod(String harvestPeriod) { this.harvestPeriod = harvestPeriod; }
    public String getDeliveryLocation() { return deliveryLocation; }
    public void setDeliveryLocation(String deliveryLocation) { this.deliveryLocation = deliveryLocation; }
    public LocalDate getApplicationDeadline() { return applicationDeadline; }
    public void setApplicationDeadline(LocalDate applicationDeadline) { this.applicationDeadline = applicationDeadline; }
    public String getContractDuration() { return contractDuration; }
    public void setContractDuration(String contractDuration) { this.contractDuration = contractDuration; }
    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }
    public String getAdditionalConditions() { return additionalConditions; }
    public void setAdditionalConditions(String additionalConditions) { this.additionalConditions = additionalConditions; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
