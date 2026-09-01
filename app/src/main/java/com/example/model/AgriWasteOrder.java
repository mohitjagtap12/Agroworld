package com.example.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model representing an order for Agri Waste placed by an industrial buyer.
 */
public class AgriWasteOrder implements Serializable {
    private String id;
    private String wasteId;
    private String farmerId;
    private String farmerName;
    private String farmerPhone;
    private String buyerId;
    private String buyerName;
    private String buyerPhone;
    private String buyerType; // "Dairy Farm", "Biomass Plant", "Compost Unit", "Nursery", "Trader"
    private String wasteName;
    private String wasteType;
    private double quantity;
    private String unit;
    private double agreedPrice;
    private String priceUnit;
    private double totalAmount;
    private String pickupMethod; // "Buyer Pickup", "Delivery Partner"
    private String deliveryAddress;
    private String village;
    private String taluka;
    private String district;
    private String deliveryPartnerId;
    private String deliveryPartnerName;
    private String deliveryPartnerPhone;
    private String status; // "Waiting for Farmer", "Accepted", "Pickup Scheduled", "Picked Up", "Out for Delivery", "Delivered", "Completed", "Rejected"
    private String orderDate;
    private String pickupDate;
    private String completedDate;
    private String notes;

    public AgriWasteOrder() {
        this.buyerType = "Biomass Buyer";
        this.status = "Waiting for Farmer";
        this.notes = "";
    }

    public AgriWasteOrder(String id, String wasteId, String farmerId, String farmerName,
                          String farmerPhone, String buyerId, String buyerName, String buyerPhone,
                          String buyerType, String wasteName, String wasteType, double quantity,
                          String unit, double agreedPrice, String priceUnit, double totalAmount,
                          String pickupMethod, String deliveryAddress, String village,
                          String taluka, String district, String deliveryPartnerId,
                          String deliveryPartnerName, String deliveryPartnerPhone, String status,
                          String orderDate, String pickupDate, String completedDate, String notes) {
        this.id = id;
        this.wasteId = wasteId;
        this.farmerId = farmerId;
        this.farmerName = farmerName;
        this.farmerPhone = farmerPhone;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.buyerPhone = buyerPhone;
        this.buyerType = buyerType != null ? buyerType : "Biomass Buyer";
        this.wasteName = wasteName;
        this.wasteType = wasteType;
        this.quantity = quantity;
        this.unit = unit;
        this.agreedPrice = agreedPrice;
        this.priceUnit = priceUnit;
        this.totalAmount = totalAmount;
        this.pickupMethod = pickupMethod;
        this.deliveryAddress = deliveryAddress;
        this.village = village;
        this.taluka = taluka;
        this.district = district;
        this.deliveryPartnerId = deliveryPartnerId;
        this.deliveryPartnerName = deliveryPartnerName;
        this.deliveryPartnerPhone = deliveryPartnerPhone;
        this.status = status != null ? status : "Waiting for Farmer";
        this.orderDate = orderDate;
        this.pickupDate = pickupDate;
        this.completedDate = completedDate;
        this.notes = notes != null ? notes : "";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getWasteId() { return wasteId; }
    public void setWasteId(String wasteId) { this.wasteId = wasteId; }

    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

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

    public String getWasteType() { return wasteType; }
    public void setWasteType(String wasteType) { this.wasteType = wasteType; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getAgreedPrice() { return agreedPrice; }
    public void setAgreedPrice(double agreedPrice) { this.agreedPrice = agreedPrice; }

    public String getPriceUnit() { return priceUnit; }
    public void setPriceUnit(String priceUnit) { this.priceUnit = priceUnit; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getPickupMethod() { return pickupMethod; }
    public void setPickupMethod(String pickupMethod) { this.pickupMethod = pickupMethod; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getTaluka() { return taluka; }
    public void setTaluka(String taluka) { this.taluka = taluka; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getDeliveryPartnerId() { return deliveryPartnerId; }
    public void setDeliveryPartnerId(String deliveryPartnerId) { this.deliveryPartnerId = deliveryPartnerId; }

    public String getDeliveryPartnerName() { return deliveryPartnerName; }
    public void setDeliveryPartnerName(String deliveryPartnerName) { this.deliveryPartnerName = deliveryPartnerName; }

    public String getDeliveryPartnerPhone() { return deliveryPartnerPhone; }
    public void setDeliveryPartnerPhone(String deliveryPartnerPhone) { this.deliveryPartnerPhone = deliveryPartnerPhone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getPickupDate() { return pickupDate; }
    public void setPickupDate(String pickupDate) { this.pickupDate = pickupDate; }

    public String getCompletedDate() { return completedDate; }
    public void setCompletedDate(String completedDate) { this.completedDate = completedDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgriWasteOrder)) return false;
        AgriWasteOrder that = (AgriWasteOrder) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AgriWasteOrder{" +
                "id='" + id + '\'' +
                ", wasteName='" + wasteName + '\'' +
                ", buyerName='" + buyerName + '\'' +
                ", quantity=" + quantity +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                '}';
    }
}
