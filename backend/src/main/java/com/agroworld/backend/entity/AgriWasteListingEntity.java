package com.agroworld.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "agri_waste_listings", indexes = {
        @Index(name = "idx_waste_farmer", columnList = "farmer_id"),
        @Index(name = "idx_waste_type", columnList = "waste_type")
})
public class AgriWasteListingEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "farmer_id", nullable = false, length = 64)
    private String farmerId;

    @Column(name = "waste_type", nullable = false, length = 128)
    private String wasteType;

    @Column(name = "waste_name", nullable = false, length = 128)
    private String wasteName;

    @Column(nullable = false)
    private Double quantity;

    @Column(length = 32)
    private String unit = "Tons";

    @Column(nullable = false)
    private Double price;

    @Column(name = "price_unit", length = 32)
    private String priceUnit = "per Ton";

    @Column(name = "available_date")
    private LocalDate availableDate;

    @Column(length = 128)
    private String village;

    @Column(length = 128)
    private String taluka;

    @Column(length = 128)
    private String district;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 512)
    private String image;

    @Column(length = 32)
    private String status = "AVAILABLE"; // AVAILABLE, PARTIALLY_SOLD, SOLD_OUT, CANCELLED

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public AgriWasteListingEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }
    public String getWasteType() { return wasteType; }
    public void setWasteType(String wasteType) { this.wasteType = wasteType; }
    public String getWasteName() { return wasteName; }
    public void setWasteName(String wasteName) { this.wasteName = wasteName; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getPriceUnit() { return priceUnit; }
    public void setPriceUnit(String priceUnit) { this.priceUnit = priceUnit; }
    public LocalDate getAvailableDate() { return availableDate; }
    public void setAvailableDate(LocalDate availableDate) { this.availableDate = availableDate; }
    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }
    public String getTaluka() { return taluka; }
    public void setTaluka(String taluka) { this.taluka = taluka; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
