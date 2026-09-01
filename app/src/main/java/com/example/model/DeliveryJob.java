package com.example.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Common Delivery Job Model representing a unified logistics delivery job across:
 * 1. SELLER_PRODUCT (Agri-Store -> Farmer)
 * 2. FARM_PRODUCE (Farmer -> Customer / Consumer)
 * 3. AGRI_WASTE (Farmer -> Biomass / Waste Buyer)
 * 4. BROKER_DEAL (Farmer -> APMC Wholesale Broker / Mandi)
 */
public class DeliveryJob implements Serializable {

    // Order Types
    public static final String TYPE_SELLER_PRODUCT = "SELLER_PRODUCT";
    public static final String TYPE_FARM_PRODUCE = "FARM_PRODUCE";
    public static final String TYPE_AGRI_WASTE = "AGRI_WASTE";
    public static final String TYPE_BROKER_DEAL = "BROKER_DEAL";

    // Status Lifecycle Constants
    public static final String STATUS_CREATED = "CREATED";
    public static final String STATUS_AVAILABLE = "AVAILABLE";
    public static final String STATUS_ASSIGNED = "ASSIGNED";
    public static final String STATUS_PICKUP_SCHEDULED = "PICKUP_SCHEDULED";
    public static final String STATUS_PICKED_UP = "PICKED_UP";
    public static final String STATUS_IN_TRANSIT = "IN_TRANSIT";
    public static final String STATUS_DELIVERED = "DELIVERED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_FAILED = "FAILED";

    private String id; // e.g. DEL-2026-000001
    private String orderId; // Reference to product/produce/waste/broker order
    private String orderType; // SELLER_PRODUCT, FARM_PRODUCE, AGRI_WASTE, BROKER_DEAL
    private String itemEmoji;
    private String itemsSummary; // e.g. "Wheat Seeds - 20 kg"
    private double quantity;
    private String unit; // "kg", "tons", "packets", "bags", "boxes"

    // Source / Pickup
    private String sourceUserId;
    private String sourceName;
    private String sourceLocation;
    private String sourcePhone;

    // Destination / Drop
    private String destinationUserId;
    private String destinationName;
    private String destinationLocation;
    private String destinationPhone;

    // Dates & Financials
    private String pickupDate;
    private String deliveryDate;
    private double deliveryFee;
    private double distanceKm;

    // Delivery Partner Assignment & Execution
    private String status; // CREATED -> AVAILABLE -> ASSIGNED -> PICKUP_SCHEDULED -> PICKED_UP -> IN_TRANSIT -> DELIVERED -> COMPLETED
    private String assignedPartnerId;
    private String assignedPartnerName;
    private String assignedPartnerPhone;

    // Vehicle and Handover
    private String vehicleRequired; // "Two Wheeler", "Auto Rickshaw", "Pickup Truck", "Mini Truck", "Heavy Truck"
    private String deliveryNotes;
    private String recipientName;
    private String confirmationMethod; // "OTP Verified", "Recipient Handover", "Digital Sign"
    private String confirmationNotes;

    // Timestamps
    private String createdAt;
    private String updatedAt;
    private String completedAt;
    private long timestamp;

    public DeliveryJob() {
        this.status = STATUS_AVAILABLE;
        this.timestamp = System.currentTimeMillis();
    }

    public DeliveryJob(String id, String orderId, String orderType, String itemEmoji,
                       String itemsSummary, double quantity, String unit,
                       String sourceUserId, String sourceName, String sourceLocation, String sourcePhone,
                       String destinationUserId, String destinationName, String destinationLocation, String destinationPhone,
                       String pickupDate, String deliveryDate, double deliveryFee, double distanceKm,
                       String status, String assignedPartnerId, String assignedPartnerName, String assignedPartnerPhone,
                       String vehicleRequired, String deliveryNotes, String createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.orderType = orderType;
        this.itemEmoji = itemEmoji != null ? itemEmoji : "📦";
        this.itemsSummary = itemsSummary;
        this.quantity = quantity;
        this.unit = unit != null ? unit : "kg";
        this.sourceUserId = sourceUserId;
        this.sourceName = sourceName;
        this.sourceLocation = sourceLocation;
        this.sourcePhone = sourcePhone;
        this.destinationUserId = destinationUserId;
        this.destinationName = destinationName;
        this.destinationLocation = destinationLocation;
        this.destinationPhone = destinationPhone;
        this.pickupDate = pickupDate;
        this.deliveryDate = deliveryDate;
        this.deliveryFee = deliveryFee;
        this.distanceKm = distanceKm;
        this.status = status != null ? status : STATUS_AVAILABLE;
        this.assignedPartnerId = assignedPartnerId;
        this.assignedPartnerName = assignedPartnerName;
        this.assignedPartnerPhone = assignedPartnerPhone;
        this.vehicleRequired = vehicleRequired != null ? vehicleRequired : "Pickup Truck";
        this.deliveryNotes = deliveryNotes;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getItemEmoji() {
        return itemEmoji;
    }

    public void setItemEmoji(String itemEmoji) {
        this.itemEmoji = itemEmoji;
    }

    public String getItemsSummary() {
        return itemsSummary;
    }

    public void setItemsSummary(String itemsSummary) {
        this.itemsSummary = itemsSummary;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(String sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(String sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public String getSourcePhone() {
        return sourcePhone;
    }

    public void setSourcePhone(String sourcePhone) {
        this.sourcePhone = sourcePhone;
    }

    public String getDestinationUserId() {
        return destinationUserId;
    }

    public void setDestinationUserId(String destinationUserId) {
        this.destinationUserId = destinationUserId;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public String getDestinationPhone() {
        return destinationPhone;
    }

    public void setDestinationPhone(String destinationPhone) {
        this.destinationPhone = destinationPhone;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignedPartnerId() {
        return assignedPartnerId;
    }

    public void setAssignedPartnerId(String assignedPartnerId) {
        this.assignedPartnerId = assignedPartnerId;
    }

    public String getAssignedPartnerName() {
        return assignedPartnerName;
    }

    public void setAssignedPartnerName(String assignedPartnerName) {
        this.assignedPartnerName = assignedPartnerName;
    }

    public String getAssignedPartnerPhone() {
        return assignedPartnerPhone;
    }

    public void setAssignedPartnerPhone(String assignedPartnerPhone) {
        this.assignedPartnerPhone = assignedPartnerPhone;
    }

    public String getVehicleRequired() {
        return vehicleRequired;
    }

    public void setVehicleRequired(String vehicleRequired) {
        this.vehicleRequired = vehicleRequired;
    }

    public String getDeliveryNotes() {
        return deliveryNotes;
    }

    public void setDeliveryNotes(String deliveryNotes) {
        this.deliveryNotes = deliveryNotes;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getConfirmationMethod() {
        return confirmationMethod;
    }

    public void setConfirmationMethod(String confirmationMethod) {
        this.confirmationMethod = confirmationMethod;
    }

    public String getConfirmationNotes() {
        return confirmationNotes;
    }

    public void setConfirmationNotes(String confirmationNotes) {
        this.confirmationNotes = confirmationNotes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOrderTypeLabel() {
        if (TYPE_SELLER_PRODUCT.equalsIgnoreCase(orderType)) {
            return "🏪 Seller Product Order";
        } else if (TYPE_FARM_PRODUCE.equalsIgnoreCase(orderType)) {
            return "🌾 Farm Fresh Produce";
        } else if (TYPE_AGRI_WASTE.equalsIgnoreCase(orderType)) {
            return "♻️ Agri Waste Biomass";
        } else if (TYPE_BROKER_DEAL.equalsIgnoreCase(orderType)) {
            return "📈 Wholesale Broker Deal";
        }
        return "📦 Direct Freight";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeliveryJob that = (DeliveryJob) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
