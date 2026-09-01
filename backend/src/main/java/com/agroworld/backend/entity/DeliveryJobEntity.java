package com.agroworld.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_jobs", indexes = {
        @Index(name = "idx_delivery_partner", columnList = "assigned_partner_id"),
        @Index(name = "idx_delivery_status", columnList = "status"),
        @Index(name = "idx_delivery_order", columnList = "order_id, order_type")
})
public class DeliveryJobEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "order_id", nullable = false, length = 64)
    private String orderId;

    @Column(name = "order_type", nullable = false, length = 32)
    private String orderType; // SELLER_PRODUCT, FARM_PRODUCE, AGRI_WASTE, BROKER_DEAL

    @Column(name = "source_user_id", nullable = false, length = 64)
    private String sourceUserId;

    @Column(name = "destination_user_id", nullable = false, length = 64)
    private String destinationUserId;

    @Column(name = "pickup_location", nullable = false, length = 255)
    private String pickupLocation;

    @Column(name = "destination_location", nullable = false, length = 255)
    private String destinationLocation;

    @Column(name = "items_summary", nullable = false, length = 255)
    private String itemsSummary;

    @Column(nullable = false)
    private Double quantity;

    @Column(length = 32)
    private String unit = "Kg";

    @Column(name = "pickup_date")
    private LocalDate pickupDate;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(name = "delivery_fee", nullable = false)
    private Double deliveryFee;

    @Column(name = "assigned_partner_id", length = 64)
    private String assignedPartnerId;

    @Column(length = 32)
    private String status = "AVAILABLE"; // AVAILABLE, ASSIGNED, PICKUP_SCHEDULED, PICKED_UP, IN_TRANSIT, DELIVERED, COMPLETED, CANCELLED

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public DeliveryJobEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public String getSourceUserId() { return sourceUserId; }
    public void setSourceUserId(String sourceUserId) { this.sourceUserId = sourceUserId; }
    public String getDestinationUserId() { return destinationUserId; }
    public void setDestinationUserId(String destinationUserId) { this.destinationUserId = destinationUserId; }
    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public String getDestinationLocation() { return destinationLocation; }
    public void setDestinationLocation(String destinationLocation) { this.destinationLocation = destinationLocation; }
    public String getItemsSummary() { return itemsSummary; }
    public void setItemsSummary(String itemsSummary) { this.itemsSummary = itemsSummary; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public LocalDate getPickupDate() { return pickupDate; }
    public void setPickupDate(LocalDate pickupDate) { this.pickupDate = pickupDate; }
    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }
    public Double getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(Double deliveryFee) { this.deliveryFee = deliveryFee; }
    public String getAssignedPartnerId() { return assignedPartnerId; }
    public void setAssignedPartnerId(String assignedPartnerId) { this.assignedPartnerId = assignedPartnerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
