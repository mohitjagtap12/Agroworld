package com.example.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model representing a purchase request / proposal sent by a buyer to a farmer for Agri Waste.
 */
public class AgriWastePurchaseRequest implements Serializable {
    private String id;
    private String listingId;
    private String farmerId;
    private String farmerName;
    private String buyerId;
    private String buyerName;
    private String buyerPhone;
    private String buyerType;
    private String wasteName;
    private double requestedQuantity;
    private String unit;
    private double offeredPrice;
    private String priceUnit;
    private double totalAmount;
    private String proposedPickupDate;
    private String deliveryAddress;
    private String status; // "Pending", "Accepted", "Rejected", "Completed"
    private String requestDate;
    private String notes;

    public AgriWastePurchaseRequest() {
        this.status = "Pending";
    }

    public AgriWastePurchaseRequest(String id, String listingId, String farmerId, String farmerName,
                                   String buyerId, String buyerName, String buyerPhone, String buyerType,
                                   String wasteName, double requestedQuantity, String unit,
                                   double offeredPrice, String priceUnit, double totalAmount,
                                   String proposedPickupDate, String deliveryAddress, String status,
                                   String requestDate, String notes) {
        this.id = id;
        this.listingId = listingId;
        this.farmerId = farmerId;
        this.farmerName = farmerName;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.buyerPhone = buyerPhone;
        this.buyerType = buyerType;
        this.wasteName = wasteName;
        this.requestedQuantity = requestedQuantity;
        this.unit = unit;
        this.offeredPrice = offeredPrice;
        this.priceUnit = priceUnit;
        this.totalAmount = totalAmount;
        this.proposedPickupDate = proposedPickupDate;
        this.deliveryAddress = deliveryAddress;
        this.status = status != null ? status : "Pending";
        this.requestDate = requestDate;
        this.notes = notes != null ? notes : "";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getListingId() { return listingId; }
    public void setListingId(String listingId) { this.listingId = listingId; }

    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }

    public String getBuyerId() { return buyerId; }
    public void setBuyerId(String buyerId) { this.buyerId = buyerId; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public String getBuyerPhone() { return buyerPhone; }
    public void setBuyerPhone(String buyerPhone) { this.buyerPhone = buyerPhone; }

    public String getBuyerType() { return buyerType; }
    public void setBuyerType(String buyerType) { this.buyerType = buyerType; }

    public String getWasteName() { return wasteName; }
    public void setWasteName(String wasteName) { this.wasteName = wasteName; }

    public double getRequestedQuantity() { return requestedQuantity; }
    public void setRequestedQuantity(double requestedQuantity) { this.requestedQuantity = requestedQuantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getOfferedPrice() { return offeredPrice; }
    public void setOfferedPrice(double offeredPrice) { this.offeredPrice = offeredPrice; }

    public String getPriceUnit() { return priceUnit; }
    public void setPriceUnit(String priceUnit) { this.priceUnit = priceUnit; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getProposedPickupDate() { return proposedPickupDate; }
    public void setProposedPickupDate(String proposedPickupDate) { this.proposedPickupDate = proposedPickupDate; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRequestDate() { return requestDate; }
    public void setRequestDate(String requestDate) { this.requestDate = requestDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgriWastePurchaseRequest)) return false;
        AgriWastePurchaseRequest that = (AgriWastePurchaseRequest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AgriWastePurchaseRequest{" +
                "id='" + id + '\'' +
                ", wasteName='" + wasteName + '\'' +
                ", buyerName='" + buyerName + '\'' +
                ", requestedQuantity=" + requestedQuantity +
                ", status='" + status + '\'' +
                '}';
    }
}
