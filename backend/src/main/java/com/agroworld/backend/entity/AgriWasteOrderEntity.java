package com.agroworld.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agri_waste_orders", indexes = {
        @Index(name = "idx_waste_orders_farmer", columnList = "farmer_id"),
        @Index(name = "idx_waste_orders_buyer", columnList = "buyer_id")
})
public class AgriWasteOrderEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "listing_id", nullable = false, length = 64)
    private String listingId;

    @Column(name = "farmer_id", nullable = false, length = 64)
    private String farmerId;

    @Column(name = "buyer_id", nullable = false, length = 64)
    private String buyerId;

    @Column(nullable = false)
    private Double quantity;

    @Column(length = 32)
    private String unit = "Tons";

    @Column(nullable = false)
    private Double price;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "delivery_required")
    private Boolean deliveryRequired = true;

    @Column(length = 32)
    private String status = "PENDING"; // PENDING, ACCEPTED, DISPATCHED, DELIVERED, COMPLETED, CANCELLED

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public AgriWasteOrderEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getListingId() { return listingId; }
    public void setListingId(String listingId) { this.listingId = listingId; }
    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }
    public String getBuyerId() { return buyerId; }
    public void setBuyerId(String buyerId) { this.buyerId = buyerId; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public Boolean getDeliveryRequired() { return deliveryRequired; }
    public void setDeliveryRequired(Boolean deliveryRequired) { this.deliveryRequired = deliveryRequired; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
