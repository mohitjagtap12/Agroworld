package com.example.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model representing a farmer's bulk crop supply offer or counter-offer to a broker's buying requirement.
 */
public class FarmerBrokerOffer implements Serializable {
    private String id;
    private String requirementId;
    private String farmerId;
    private String farmerName;
    private String farmerPhone;
    private String farmerLocation;
    private String cropId;
    private String cropName;
    private String cropEmoji;
    private double availableQuantity;
    private String unit; // "Tons", "Quintals"
    private double expectedPrice;
    private String priceUnit; // "quintal", "ton"
    private double counterPrice;
    private double finalAgreedPrice;
    private String availableDate;
    private String qualityDetails;
    private String message;
    private String status; // "Pending", "Negotiating", "Accepted", "Rejected", "Completed", "Cancelled"
    private String lastActorRole; // "FARMER", "BROKER"
    private String negotiationNote;
    private String createdAt;
    private String updatedAt;

    public FarmerBrokerOffer() {
        this.unit = "Tons";
        this.priceUnit = "quintal";
        this.status = "Pending";
        this.cropEmoji = "🌾";
        this.lastActorRole = "FARMER";
    }

    public FarmerBrokerOffer(String id, String requirementId, String farmerId, String farmerName,
                             String farmerPhone, String farmerLocation, String cropId, String cropName,
                             String cropEmoji, double availableQuantity, String unit, double expectedPrice,
                             String priceUnit, double counterPrice, double finalAgreedPrice,
                             String availableDate, String qualityDetails, String message, String status,
                             String lastActorRole, String negotiationNote, String createdAt, String updatedAt) {
        this.id = id;
        this.requirementId = requirementId;
        this.farmerId = farmerId;
        this.farmerName = farmerName;
        this.farmerPhone = farmerPhone;
        this.farmerLocation = farmerLocation;
        this.cropId = cropId;
        this.cropName = cropName;
        this.cropEmoji = cropEmoji != null ? cropEmoji : "🌾";
        this.availableQuantity = availableQuantity;
        this.unit = unit != null ? unit : "Tons";
        this.expectedPrice = expectedPrice;
        this.priceUnit = priceUnit != null ? priceUnit : "quintal";
        this.counterPrice = counterPrice;
        this.finalAgreedPrice = finalAgreedPrice;
        this.availableDate = availableDate;
        this.qualityDetails = qualityDetails;
        this.message = message;
        this.status = status != null ? status : "Pending";
        this.lastActorRole = lastActorRole != null ? lastActorRole : "FARMER";
        this.negotiationNote = negotiationNote;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRequirementId() { return requirementId; }
    public void setRequirementId(String requirementId) { this.requirementId = requirementId; }

    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getFarmerLocation() { return farmerLocation; }
    public void setFarmerLocation(String farmerLocation) { this.farmerLocation = farmerLocation; }

    public String getCropId() { return cropId; }
    public void setCropId(String cropId) { this.cropId = cropId; }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public String getCropEmoji() { return cropEmoji; }
    public void setCropEmoji(String cropEmoji) { this.cropEmoji = cropEmoji; }

    public double getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(double availableQuantity) { this.availableQuantity = availableQuantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getExpectedPrice() { return expectedPrice; }
    public void setExpectedPrice(double expectedPrice) { this.expectedPrice = expectedPrice; }

    public String getPriceUnit() { return priceUnit; }
    public void setPriceUnit(String priceUnit) { this.priceUnit = priceUnit; }

    public double getCounterPrice() { return counterPrice; }
    public void setCounterPrice(double counterPrice) { this.counterPrice = counterPrice; }

    public double getFinalAgreedPrice() { return finalAgreedPrice; }
    public void setFinalAgreedPrice(double finalAgreedPrice) { this.finalAgreedPrice = finalAgreedPrice; }

    public String getAvailableDate() { return availableDate; }
    public void setAvailableDate(String availableDate) { this.availableDate = availableDate; }

    public String getQualityDetails() { return qualityDetails; }
    public void setQualityDetails(String qualityDetails) { this.qualityDetails = qualityDetails; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLastActorRole() { return lastActorRole; }
    public void setLastActorRole(String lastActorRole) { this.lastActorRole = lastActorRole; }

    public String getNegotiationNote() { return negotiationNote; }
    public void setNegotiationNote(String negotiationNote) { this.negotiationNote = negotiationNote; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    /**
     * Calculates current effective value (expected, counter, or agreed) converting units (1 Ton = 10 Quintals).
     */
    public double calculateTotalValue(double price) {
        if (price <= 0) price = (finalAgreedPrice > 0) ? finalAgreedPrice : ((counterPrice > 0) ? counterPrice : expectedPrice);
        double qtyInQuintals = availableQuantity;
        if ("Tons".equalsIgnoreCase(unit) || "Ton".equalsIgnoreCase(unit)) {
            qtyInQuintals = availableQuantity * 10.0;
        }
        if ("quintal".equalsIgnoreCase(priceUnit) || "per quintal".equalsIgnoreCase(priceUnit)) {
            return qtyInQuintals * price;
        } else if ("ton".equalsIgnoreCase(priceUnit) || "per ton".equalsIgnoreCase(priceUnit)) {
            return availableQuantity * price;
        }
        return qtyInQuintals * price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FarmerBrokerOffer)) return false;
        FarmerBrokerOffer that = (FarmerBrokerOffer) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
