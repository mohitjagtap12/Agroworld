package com.example.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model representing agricultural input products (Seeds, Fertilizers, Crop Protection, Tools, Equipment, Irrigation) in the Seller store.
 */
public class SellerProduct implements Serializable {
    private String id;
    private String sellerId;
    private String sellerName;
    private String sellerPhone;
    private String name;
    private String category; // "Seeds", "Fertilizers", "Crop Protection", "Farm Equipment", "Tools", "Irrigation Equipment", "Other Agricultural Supplies"
    private String brand;
    private String description;
    private double price;
    private String unit; // "kg", "bag", "bottle", "packet", "piece", "roll", "litre"
    private int stock; // available stock quantity
    private double rating;
    private String imageEmoji;
    private String location;
    private String status; // "Active", "Out of Stock"
    private String suitableCrops; // e.g. "Tomato, Chilli, Onion, Rice, All Crops"
    private String usageAdvisory; // Label directions and safety guidance
    private String createdAt;
    private String updatedAt;

    public SellerProduct() {
        this.sellerId = "SELLER_01";
        this.sellerName = "Kisan Agri Mart";
        this.sellerPhone = "+91 98220 54321";
        this.category = "Fertilizers";
        this.stock = 100;
        this.rating = 4.7;
        this.imageEmoji = "🌱";
        this.unit = "kg";
        this.location = "Narayangaon, Junnar";
        this.status = "Active";
        this.suitableCrops = "All Crops";
        this.usageAdvisory = "Follow manufacturer label directions. Use certified personal protective equipment.";
        this.createdAt = "2026-08-15";
        this.updatedAt = "2026-08-31";
    }

    public SellerProduct(String id, String sellerId, String sellerName, String sellerPhone,
                         String name, String category, String brand, String description,
                         double price, String unit, int stock, double rating,
                         String imageEmoji, String location, String status,
                         String suitableCrops, String usageAdvisory) {
        this.id = id;
        this.sellerId = sellerId != null ? sellerId : "SELLER_01";
        this.sellerName = sellerName != null ? sellerName : "Kisan Agri Mart";
        this.sellerPhone = sellerPhone != null ? sellerPhone : "+91 98220 54321";
        this.name = name;
        this.category = category != null ? category : "Fertilizers";
        this.brand = brand;
        this.description = description;
        this.price = price;
        this.unit = unit != null ? unit : "kg";
        this.stock = stock;
        this.rating = rating > 0 ? rating : 4.5;
        this.imageEmoji = imageEmoji != null ? imageEmoji : "🌱";
        this.location = location != null ? location : "Narayangaon, Junnar";
        this.status = stock > 0 ? (status != null ? status : "Active") : "Out of Stock";
        this.suitableCrops = suitableCrops != null ? suitableCrops : "All Crops";
        this.usageAdvisory = usageAdvisory != null ? usageAdvisory : "Follow manufacturer label directions.";
        this.createdAt = "2026-08-15";
        this.updatedAt = "2026-08-31";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getSellerPhone() { return sellerPhone; }
    public void setSellerPhone(String sellerPhone) { this.sellerPhone = sellerPhone; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public int getStock() { return stock; }
    public void setStock(int stock) {
        this.stock = stock;
        if (stock <= 0) {
            this.status = "Out of Stock";
        } else if ("Out of Stock".equalsIgnoreCase(this.status)) {
            this.status = "Active";
        }
    }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getImageEmoji() { return imageEmoji; }
    public void setImageEmoji(String imageEmoji) { this.imageEmoji = imageEmoji; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSuitableCrops() { return suitableCrops; }
    public void setSuitableCrops(String suitableCrops) { this.suitableCrops = suitableCrops; }

    public String getUsageAdvisory() { return usageAdvisory; }
    public void setUsageAdvisory(String usageAdvisory) { this.usageAdvisory = usageAdvisory; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public boolean isOutOfStock() {
        return stock <= 0 || "Out of Stock".equalsIgnoreCase(status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SellerProduct)) return false;
        SellerProduct that = (SellerProduct) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SellerProduct{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                '}';
    }
}
