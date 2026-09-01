package com.example.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model representing a wholesale broker deal / bulk trading agreement between a farmer and a broker.
 */
public class BrokerDeal implements Serializable {
    private String id;
    private String requirementId;
    private String offerId;
    private String brokerId;
    private String brokerName;
    private String companyName;
    private String phone;
    private String farmerId;
    private String farmerName;
    private String farmerPhone;
    private String cropDemanded; // e.g. "Wheat" or "Pune Red Onions"
    private String cropEmoji;
    private double requiredQty; // Quantity (e.g. 8.0)
    private String unit; // "Tons", "Quintals"
    private double offeredPricePerQuintal; // Agreed price per quintal / ton
    private String priceUnit; // "quintal", "ton"
    private double totalValue; // Total calculated deal amount (e.g. 8 Tons = 80 Quintals * 2525 = ₹2,02,000)
    private String location; // Pickup Location
    private String pickupDate; // Delivery/Handover Date
    private String paymentTerms;
    private String dealStatus; // "Price Agreed", "Deal Confirmed", "Pickup Scheduled", "Crop Handed Over", "Completed", "Cancelled"
    private double mandiRateToday;
    private String createdAt;
    private String completedAt;

    public BrokerDeal() {
        this.unit = "Tons";
        this.priceUnit = "quintal";
        this.cropEmoji = "🌾";
        this.dealStatus = "Deal Confirmed";
        this.paymentTerms = "Immediate RTGS / NEFT on Delivery Slip";
    }

    public BrokerDeal(String id, String brokerName, String companyName, String phone,
                      String cropDemanded, double requiredQty, String unit,
                      double offeredPricePerQuintal, String location, String paymentTerms,
                      String dealStatus, double mandiRateToday) {
        this.id = id;
        this.brokerName = brokerName;
        this.companyName = companyName;
        this.phone = phone;
        this.cropDemanded = cropDemanded;
        this.cropEmoji = "🌾";
        this.requiredQty = requiredQty;
        this.unit = unit != null ? unit : "Quintal";
        this.offeredPricePerQuintal = offeredPricePerQuintal;
        this.priceUnit = "quintal";
        this.location = location;
        this.paymentTerms = paymentTerms != null ? paymentTerms : "Immediate RTGS / NEFT";
        this.dealStatus = dealStatus != null ? dealStatus : "Deal Confirmed";
        this.mandiRateToday = mandiRateToday;
        this.totalValue = calculateTotalValue(requiredQty, unit, offeredPricePerQuintal, priceUnit);
    }

    public BrokerDeal(String id, String requirementId, String offerId, String brokerId,
                      String brokerName, String companyName, String phone, String farmerId,
                      String farmerName, String farmerPhone, String cropDemanded, String cropEmoji,
                      double requiredQty, String unit, double offeredPricePerQuintal, String priceUnit,
                      double totalValue, String location, String pickupDate, String paymentTerms,
                      String dealStatus, double mandiRateToday, String createdAt, String completedAt) {
        this.id = id;
        this.requirementId = requirementId;
        this.offerId = offerId;
        this.brokerId = brokerId;
        this.brokerName = brokerName;
        this.companyName = companyName;
        this.phone = phone;
        this.farmerId = farmerId;
        this.farmerName = farmerName;
        this.farmerPhone = farmerPhone;
        this.cropDemanded = cropDemanded;
        this.cropEmoji = cropEmoji != null ? cropEmoji : "🌾";
        this.requiredQty = requiredQty;
        this.unit = unit != null ? unit : "Tons";
        this.offeredPricePerQuintal = offeredPricePerQuintal;
        this.priceUnit = priceUnit != null ? priceUnit : "quintal";
        this.totalValue = totalValue > 0 ? totalValue : calculateTotalValue(requiredQty, unit, offeredPricePerQuintal, priceUnit);
        this.location = location;
        this.pickupDate = pickupDate;
        this.paymentTerms = paymentTerms != null ? paymentTerms : "Immediate RTGS / NEFT on Delivery Slip";
        this.dealStatus = dealStatus != null ? dealStatus : "Deal Confirmed";
        this.mandiRateToday = mandiRateToday;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    public static double calculateTotalValue(double quantity, String qtyUnit, double price, String priceUnit) {
        if (quantity <= 0 || price <= 0) return 0.0;
        String qUnit = qtyUnit != null ? qtyUnit.trim().toLowerCase() : "quintal";
        String pUnit = priceUnit != null ? priceUnit.trim().toLowerCase() : "quintal";

        double quantityInQuintals = quantity;
        if (qUnit.contains("ton")) {
            quantityInQuintals = quantity * 10.0; // 1 Ton = 10 Quintals
        } else if (qUnit.contains("kg")) {
            quantityInQuintals = quantity / 100.0;
        }

        if (pUnit.contains("quintal")) {
            return quantityInQuintals * price;
        } else if (pUnit.contains("ton")) {
            double quantityInTons = quantityInQuintals / 10.0;
            return quantityInTons * price;
        } else if (pUnit.contains("kg")) {
            double quantityInKg = quantityInQuintals * 100.0;
            return quantityInKg * price;
        }
        return quantityInQuintals * price;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRequirementId() { return requirementId; }
    public void setRequirementId(String requirementId) { this.requirementId = requirementId; }

    public String getOfferId() { return offerId; }
    public void setOfferId(String offerId) { this.offerId = offerId; }

    public String getBrokerId() { return brokerId; }
    public void setBrokerId(String brokerId) { this.brokerId = brokerId; }

    public String getBrokerName() { return brokerName; }
    public void setBrokerName(String brokerName) { this.brokerName = brokerName; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getCropDemanded() { return cropDemanded; }
    public void setCropDemanded(String cropDemanded) { this.cropDemanded = cropDemanded; }

    // Alias for Crop name
    public String getCrop() { return cropDemanded; }
    public void setCrop(String crop) { this.cropDemanded = crop; }

    public String getCropEmoji() { return cropEmoji; }
    public void setCropEmoji(String cropEmoji) { this.cropEmoji = cropEmoji; }

    public double getRequiredQty() { return requiredQty; }
    public void setRequiredQty(double requiredQty) {
        this.requiredQty = requiredQty;
        this.totalValue = calculateTotalValue(requiredQty, unit, offeredPricePerQuintal, priceUnit);
    }

    // Alias for quantity
    public double getQuantity() { return requiredQty; }
    public void setQuantity(double quantity) { setRequiredQty(quantity); }

    public String getUnit() { return unit; }
    public void setUnit(String unit) {
        this.unit = unit;
        this.totalValue = calculateTotalValue(requiredQty, unit, offeredPricePerQuintal, priceUnit);
    }

    public double getOfferedPricePerQuintal() { return offeredPricePerQuintal; }
    public void setOfferedPricePerQuintal(double offeredPricePerQuintal) {
        this.offeredPricePerQuintal = offeredPricePerQuintal;
        this.totalValue = calculateTotalValue(requiredQty, unit, offeredPricePerQuintal, priceUnit);
    }

    // Alias for agreed price
    public double getAgreedPrice() { return offeredPricePerQuintal; }
    public void setAgreedPrice(double agreedPrice) { setOfferedPricePerQuintal(agreedPrice); }

    public String getPriceUnit() { return priceUnit; }
    public void setPriceUnit(String priceUnit) {
        this.priceUnit = priceUnit;
        this.totalValue = calculateTotalValue(requiredQty, unit, offeredPricePerQuintal, priceUnit);
    }

    public double getTotalValue() {
        if (totalValue <= 0) {
            totalValue = calculateTotalValue(requiredQty, unit, offeredPricePerQuintal, priceUnit);
        }
        return totalValue;
    }
    public void setTotalValue(double totalValue) { this.totalValue = totalValue; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPickupLocation() { return location; }
    public void setPickupLocation(String pickupLocation) { this.location = pickupLocation; }

    public String getPickupDate() { return pickupDate; }
    public void setPickupDate(String pickupDate) { this.pickupDate = pickupDate; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }

    public String getDealStatus() { return dealStatus; }
    public void setDealStatus(String dealStatus) { this.dealStatus = dealStatus; }

    public String getStatus() { return dealStatus; }
    public void setStatus(String status) { this.dealStatus = status; }

    public double getMandiRateToday() { return mandiRateToday; }
    public void setMandiRateToday(double mandiRateToday) { this.mandiRateToday = mandiRateToday; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BrokerDeal)) return false;
        BrokerDeal that = (BrokerDeal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BrokerDeal{" +
                "id='" + id + '\'' +
                ", brokerName='" + brokerName + '\'' +
                ", cropDemanded='" + cropDemanded + '\'' +
                ", quantity=" + requiredQty +
                " " + unit +
                ", agreedPrice=" + offeredPricePerQuintal +
                "/" + priceUnit +
                ", totalValue=" + getTotalValue() +
                ", dealStatus='" + dealStatus + '\'' +
                '}';
    }
}
