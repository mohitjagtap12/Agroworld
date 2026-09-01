package com.example.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Model representing a wholesale broker buying requirement / bulk demand for agricultural produce.
 */
public class BrokerRequirement implements Serializable {
    private String id;
    private String brokerId;
    private String brokerName;
    private String brokerFirmName;
    private String brokerPhone;
    private String crop;
    private String cropEmoji;
    private double requiredQuantity;
    private String unit; // "Tons", "Quintals"
    private double offeredPrice;
    private String priceUnit; // "quintal", "ton"
    private String qualityRequirement;
    private String requiredDate;
    private String pickupLocation;
    private String paymentTerms;
    private String additionalRequirements;
    private double sampleMandiPrice;
    private String status; // "Open", "Under Negotiation", "Closed", "Completed"
    private String createdAt;
    private String updatedAt;

    public BrokerRequirement() {
        this.unit = "Tons";
        this.priceUnit = "quintal";
        this.status = "Open";
        this.cropEmoji = "🌾";
        this.paymentTerms = "Immediate RTGS / NEFT on Weighbridge Slip";
    }

    public BrokerRequirement(String id, String brokerId, String brokerName, String brokerFirmName,
                             String brokerPhone, String crop, String cropEmoji, double requiredQuantity,
                             String unit, double offeredPrice, String priceUnit, String qualityRequirement,
                             String requiredDate, String pickupLocation, String paymentTerms,
                             String additionalRequirements, double sampleMandiPrice, String status,
                             String createdAt, String updatedAt) {
        this.id = id;
        this.brokerId = brokerId;
        this.brokerName = brokerName;
        this.brokerFirmName = brokerFirmName;
        this.brokerPhone = brokerPhone;
        this.crop = crop;
        this.cropEmoji = cropEmoji != null ? cropEmoji : "🌾";
        this.requiredQuantity = requiredQuantity;
        this.unit = unit != null ? unit : "Tons";
        this.offeredPrice = offeredPrice;
        this.priceUnit = priceUnit != null ? priceUnit : "quintal";
        this.qualityRequirement = qualityRequirement;
        this.requiredDate = requiredDate;
        this.pickupLocation = pickupLocation;
        this.paymentTerms = paymentTerms != null ? paymentTerms : "Immediate RTGS / NEFT on Weighbridge Slip";
        this.additionalRequirements = additionalRequirements;
        this.sampleMandiPrice = sampleMandiPrice;
        this.status = status != null ? status : "Open";
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBrokerId() { return brokerId; }
    public void setBrokerId(String brokerId) { this.brokerId = brokerId; }

    public String getBrokerName() { return brokerName; }
    public void setBrokerName(String brokerName) { this.brokerName = brokerName; }

    public String getBrokerFirmName() { return brokerFirmName; }
    public void setBrokerFirmName(String brokerFirmName) { this.brokerFirmName = brokerFirmName; }

    public String getBrokerPhone() { return brokerPhone; }
    public void setBrokerPhone(String brokerPhone) { this.brokerPhone = brokerPhone; }

    public String getCrop() { return crop; }
    public void setCrop(String crop) { this.crop = crop; }

    public String getCropEmoji() { return cropEmoji; }
    public void setCropEmoji(String cropEmoji) { this.cropEmoji = cropEmoji; }

    public double getRequiredQuantity() { return requiredQuantity; }
    public void setRequiredQuantity(double requiredQuantity) { this.requiredQuantity = requiredQuantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getOfferedPrice() { return offeredPrice; }
    public void setOfferedPrice(double offeredPrice) { this.offeredPrice = offeredPrice; }

    public String getPriceUnit() { return priceUnit; }
    public void setPriceUnit(String priceUnit) { this.priceUnit = priceUnit; }

    public String getQualityRequirement() { return qualityRequirement; }
    public void setQualityRequirement(String qualityRequirement) { this.qualityRequirement = qualityRequirement; }

    public String getRequiredDate() { return requiredDate; }
    public void setRequiredDate(String requiredDate) { this.requiredDate = requiredDate; }

    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }

    public String getAdditionalRequirements() { return additionalRequirements; }
    public void setAdditionalRequirements(String additionalRequirements) { this.additionalRequirements = additionalRequirements; }

    public double getSampleMandiPrice() { return sampleMandiPrice; }
    public void setSampleMandiPrice(double sampleMandiPrice) { this.sampleMandiPrice = sampleMandiPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    /**
     * Checks if this broker requirement matches any crop grown by the farmer.
     */
    public boolean matchesFarmerCrops(List<FarmerCrop> farmerCrops) {
        if (farmerCrops == null || farmerCrops.isEmpty() || crop == null) return false;
        String reqCropLower = crop.toLowerCase().trim();
        for (FarmerCrop fc : farmerCrops) {
            if (fc.getName() == null) continue;
            String fcNameLower = fc.getName().toLowerCase().trim();
            if (fcNameLower.contains(reqCropLower) || reqCropLower.contains(fcNameLower)) {
                return true;
            }
            // Check keywords
            if (reqCropLower.contains("wheat") && fcNameLower.contains("wheat")) return true;
            if (reqCropLower.contains("onion") && fcNameLower.contains("onion")) return true;
            if (reqCropLower.contains("rice") && (fcNameLower.contains("rice") || fcNameLower.contains("paddy") || fcNameLower.contains("indrayani"))) return true;
            if (reqCropLower.contains("cane") && fcNameLower.contains("cane")) return true;
            if (reqCropLower.contains("soy") && fcNameLower.contains("soy")) return true;
            if (reqCropLower.contains("mango") && fcNameLower.contains("mango")) return true;
            if (reqCropLower.contains("tomato") && fcNameLower.contains("tomato")) return true;
        }
        return false;
    }

    /**
     * Calculates total estimated procurement value converting units accurately (1 Ton = 10 Quintals).
     */
    public double calculateTotalEstimatedValue() {
        double qtyInQuintals = requiredQuantity;
        if ("Tons".equalsIgnoreCase(unit) || "Ton".equalsIgnoreCase(unit)) {
            qtyInQuintals = requiredQuantity * 10.0;
        }
        if ("quintal".equalsIgnoreCase(priceUnit) || "per quintal".equalsIgnoreCase(priceUnit)) {
            return qtyInQuintals * offeredPrice;
        } else if ("ton".equalsIgnoreCase(priceUnit) || "per ton".equalsIgnoreCase(priceUnit)) {
            return requiredQuantity * offeredPrice;
        }
        return qtyInQuintals * offeredPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BrokerRequirement)) return false;
        BrokerRequirement that = (BrokerRequirement) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
