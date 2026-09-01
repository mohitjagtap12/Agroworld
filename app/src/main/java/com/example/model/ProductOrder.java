package com.example.model;

import java.io.Serializable;

/**
 * Model representing an agricultural product order placed by a farmer to a seller.
 */
public class ProductOrder implements Serializable {

    // Status Constants
    public static final String STATUS_ORDER_PLACED = "Order Placed";
    public static final String STATUS_CONFIRMED = "Confirmed";
    public static final String STATUS_PACKED = "Packed";
    public static final String STATUS_OUT_FOR_DELIVERY = "Out for Delivery";
    public static final String STATUS_DELIVERED = "Delivered";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_REJECTED = "Order Rejected";

    private String id;
    private String orderNumber;
    private String farmerId;
    private String farmerName;
    private String farmerPhone;
    private String sellerId;
    private String sellerName;
    private String productId;
    private String productName;
    private String productEmoji;
    private String category;
    private int quantity;
    private String unit;
    private double pricePerUnit;
    private double totalAmount;
    private String deliveryAddress;
    private String village;
    private String taluka;
    private String district;
    private String status;
    private String orderDate;
    private String deliveryMethod;
    private String paymentMethod;
    private String notes;

    public ProductOrder() {
        this.status = STATUS_ORDER_PLACED;
        this.deliveryMethod = "AgroWorld Delivery Partner";
        this.paymentMethod = "Cash on Delivery";
    }

    public ProductOrder(String id, String orderNumber, String farmerId, String farmerName, String farmerPhone,
                        String sellerId, String sellerName, String productId, String productName,
                        String productEmoji, String category, int quantity, String unit,
                        double pricePerUnit, double totalAmount, String deliveryAddress,
                        String village, String taluka, String district, String status,
                        String orderDate, String deliveryMethod, String paymentMethod, String notes) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.farmerId = farmerId;
        this.farmerName = farmerName;
        this.farmerPhone = farmerPhone;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.productId = productId;
        this.productName = productName;
        this.productEmoji = productEmoji != null ? productEmoji : "📦";
        this.category = category;
        this.quantity = quantity;
        this.unit = unit != null ? unit : "kg";
        this.pricePerUnit = pricePerUnit;
        this.totalAmount = totalAmount;
        this.deliveryAddress = deliveryAddress;
        this.village = village;
        this.taluka = taluka;
        this.district = district;
        this.status = status != null ? status : STATUS_ORDER_PLACED;
        this.orderDate = orderDate;
        this.deliveryMethod = deliveryMethod != null ? deliveryMethod : "AgroWorld Delivery Partner";
        this.paymentMethod = paymentMethod != null ? paymentMethod : "Cash on Delivery";
        this.notes = notes;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductEmoji() { return productEmoji; }
    public void setProductEmoji(String productEmoji) { this.productEmoji = productEmoji; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(double pricePerUnit) { this.pricePerUnit = pricePerUnit; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getTaluka() { return taluka; }
    public void setTaluka(String taluka) { this.taluka = taluka; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getDeliveryMethod() { return deliveryMethod; }
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
