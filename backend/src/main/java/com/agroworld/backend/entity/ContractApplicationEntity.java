package com.agroworld.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contract_applications", uniqueConstraints = {
        @UniqueConstraint(name = "uk_contract_farmer", columnNames = {"contract_id", "farmer_id"})
})
public class ContractApplicationEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "contract_id", nullable = false, length = 64)
    private String contractId;

    @Column(name = "farmer_id", nullable = false, length = 64)
    private String farmerId;

    @Column(name = "crop_id", length = 64)
    private String cropId;

    @Column(name = "land_area", nullable = false)
    private Double landArea;

    @Column(name = "expected_quantity", nullable = false)
    private Double expectedQuantity;

    @Column(name = "expected_harvest_date")
    private LocalDate expectedHarvestDate;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(length = 32)
    private String status = "SUBMITTED"; // SUBMITTED, UNDER_REVIEW, ACCEPTED, REJECTED

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt = LocalDateTime.now();

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    public ContractApplicationEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getContractId() { return contractId; }
    public void setContractId(String contractId) { this.contractId = contractId; }
    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }
    public String getCropId() { return cropId; }
    public void setCropId(String cropId) { this.cropId = cropId; }
    public Double getLandArea() { return landArea; }
    public void setLandArea(Double landArea) { this.landArea = landArea; }
    public Double getExpectedQuantity() { return expectedQuantity; }
    public void setExpectedQuantity(Double expectedQuantity) { this.expectedQuantity = expectedQuantity; }
    public LocalDate getExpectedHarvestDate() { return expectedHarvestDate; }
    public void setExpectedHarvestDate(LocalDate expectedHarvestDate) { this.expectedHarvestDate = expectedHarvestDate; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}
