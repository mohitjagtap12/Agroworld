package com.example.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model representing an Agricultural Waste listing posted by a farmer.
 */
public class AgriWasteItem implements Serializable {
    private String id;
    private String farmerId;
    private String farmerName;
    private String farmerPhone;
    private String wasteType; // "Wheat Straw", "Rice Straw", "Sugarcane Residue", "Maize Stalk", etc.
    private String wasteName;
    private String category; // "Straw", "Crop Stalks", "Sugarcane Residue", "Husk / Shell", "Other Crop Residue"
    private double quantity;
    private double initialQuantity;
    private String unit; // "kg", "quintal", "ton", "bundle", "bags"
    private double price;
    private String priceUnit; // "₹/kg", "₹/quintal", "₹/ton", "₹/bundle", "₹/bag"
    private String availableDate;
    private String village;
    private String taluka;
    private String district;
    private double distanceKm;
    private String description;
    private String imageEmoji;
    private String pickupPreference; // "Buyer Pickup", "Delivery Partner", "Both Supported"
    private String status; // "Available", "Sold Out", "Cancelled"
    private String createdAt;
    private String updatedAt;

    public AgriWasteItem() {
        this.distanceKm = 4.5;
        this.imageEmoji = "🌾";
        this.pickupPreference = "Both Supported";
        this.status = "Available";
        this.createdAt = "26 Aug 2026";
        this.updatedAt = "26 Aug 2026";
    }

    public AgriWasteItem(String id, String farmerId, String farmerName, String farmerPhone,
                         String wasteType, String wasteName, String category, double quantity,
                         double initialQuantity, String unit, double price, String priceUnit,
                         String availableDate, String village, String taluka, String district,
                         double distanceKm, String description, String imageEmoji,
                         String pickupPreference, String status, String createdAt, String updatedAt) {
        this.id = id;
        this.farmerId = farmerId;
        this.farmerName = farmerName;
        this.farmerPhone = farmerPhone;
        this.wasteType = wasteType;
        this.wasteName = wasteName;
        this.category = category;
        this.quantity = quantity;
        this.initialQuantity = initialQuantity;
        this.unit = unit;
        this.price = price;
        this.priceUnit = priceUnit;
        this.availableDate = availableDate;
        this.village = village;
        this.taluka = taluka;
        this.district = district;
        this.distanceKm = distanceKm;
        this.description = description;
        this.imageEmoji = imageEmoji != null ? imageEmoji : "🌾";
        this.pickupPreference = pickupPreference != null ? pickupPreference : "Both Supported";
        this.status = status != null ? status : "Available";
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getWasteType() { return wasteType; }
    public void setWasteType(String wasteType) { this.wasteType = wasteType; }

    public String getWasteName() { return wasteName; }
    public void setWasteName(String wasteName) { this.wasteName = wasteName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public double getInitialQuantity() { return initialQuantity; }
    public void setInitialQuantity(double initialQuantity) { this.initialQuantity = initialQuantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getPriceUnit() { return priceUnit; }
    public void setPriceUnit(String priceUnit) { this.priceUnit = priceUnit; }

    public String getAvailableDate() { return availableDate; }
    public void setAvailableDate(String availableDate) { this.availableDate = availableDate; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getTaluka() { return taluka; }
    public void setTaluka(String taluka) { this.taluka = taluka; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageEmoji() { return imageEmoji; }
    public void setImageEmoji(String imageEmoji) { this.imageEmoji = imageEmoji; }

    public String getPickupPreference() { return pickupPreference; }
    public void setPickupPreference(String pickupPreference) { this.pickupPreference = pickupPreference; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgriWasteItem)) return false;
        AgriWasteItem that = (AgriWasteItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AgriWasteItem{" +
                "id='" + id + '\'' +
                ", wasteName='" + wasteName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", status='" + status + '\'' +
                '}';
    }
}
