package com.example.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model representing a customer or bulk buyer order placed directly for farmer produce.
 */
public class FarmerDirectOrder implements Serializable {
    private String id;
    private String listingId;
    private String farmerId;
    private String farmerName;
    private String produceName;
    private String category;
    private String customerId;
    private String customerName;
    private String customerPhone;
    private String deliveryAddress;
    private String village;
    private String taluka;
    private String district;
    private double quantity;
    private String unit;
    private double pricePerUnit;
    private double totalPrice;
    private String orderDate;
    private String deliveryDate;
    private String status; // "Order Placed", "Accepted", "Preparing", "Ready for Pickup", "Out for Delivery", "Delivered", "Completed", "Rejected", "Cancelled"
    private String paymentStatus; // "Paid Online", "Cash on Delivery", "Escrow"
    private String imageEmoji;
    private long timestamp;

    public FarmerDirectOrder() {
        this.status = "Order Placed";
        this.paymentStatus = "Paid Online";
        this.imageEmoji = "🌾";
        this.timestamp = System.currentTimeMillis();
    }

    public FarmerDirectOrder(String id, String listingId, String produceName, String customerId,
                             String customerName, String customerPhone, String deliveryAddress,
                             double quantity, String unit, double totalPrice, String orderDate,
                             String deliveryDate, String status, String paymentStatus) {
        this.id = id;
        this.listingId = listingId;
        this.farmerId = "farmer_1";
        this.farmerName = "Vitthal Deshmukh";
        this.produceName = produceName;
        this.category = "Vegetables";
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.deliveryAddress = deliveryAddress;
        this.village = "Baramati";
        this.taluka = "Baramati";
        this.district = "Pune";
        this.quantity = quantity;
        this.unit = unit;
        this.pricePerUnit = quantity > 0 ? (totalPrice / quantity) : 0;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.status = status != null ? status : "Order Placed";
        this.paymentStatus = paymentStatus != null ? paymentStatus : "Paid Online";
        this.imageEmoji = "🌾";
        this.timestamp = System.currentTimeMillis();
    }

    public FarmerDirectOrder(String id, String listingId, String farmerId, String farmerName,
                             String produceName, String category, String customerId,
                             String customerName, String customerPhone, String deliveryAddress,
                             String village, String taluka, String district,
                             double quantity, String unit, double pricePerUnit, double totalPrice,
                             String orderDate, String deliveryDate, String status,
                             String paymentStatus, String imageEmoji) {
        this.id = id;
        this.listingId = listingId;
        this.farmerId = farmerId;
        this.farmerName = farmerName;
        this.produceName = produceName;
        this.category = category;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.deliveryAddress = deliveryAddress;
        this.village = village;
        this.taluka = taluka;
        this.district = district;
        this.quantity = quantity;
        this.unit = unit;
        this.pricePerUnit = pricePerUnit;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.status = status != null ? status : "Order Placed";
        this.paymentStatus = paymentStatus != null ? paymentStatus : "Paid Online";
        this.imageEmoji = imageEmoji != null ? imageEmoji : "🌾";
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getListingId() { return listingId; }
    public void setListingId(String listingId) { this.listingId = listingId; }

    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }

    public String getProduceName() { return produceName; }
    public void setProduceName(String produceName) { this.produceName = produceName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getTaluka() { return taluka; }
    public void setTaluka(String taluka) { this.taluka = taluka; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(double pricePerUnit) { this.pricePerUnit = pricePerUnit; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getImageEmoji() { return imageEmoji; }
    public void setImageEmoji(String imageEmoji) { this.imageEmoji = imageEmoji; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getStatusStepIndex() {
        if ("Order Placed".equalsIgnoreCase(status) || "Pending".equalsIgnoreCase(status)) return 0;
        if ("Accepted".equalsIgnoreCase(status) || "Confirmed".equalsIgnoreCase(status)) return 1;
        if ("Preparing".equalsIgnoreCase(status) || "Packed".equalsIgnoreCase(status)) return 2;
        if ("Ready for Pickup".equalsIgnoreCase(status) || "Ready".equalsIgnoreCase(status)) return 3;
        if ("Out for Delivery".equalsIgnoreCase(status)) return 4;
        if ("Delivered".equalsIgnoreCase(status) || "Completed".equalsIgnoreCase(status)) return 5;
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FarmerDirectOrder)) return false;
        FarmerDirectOrder that = (FarmerDirectOrder) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "FarmerDirectOrder{" +
                "id='" + id + '\'' +
                ", produceName='" + produceName + '\'' +
                ", customerName='" + customerName + '\'' +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                '}';
    }
}
