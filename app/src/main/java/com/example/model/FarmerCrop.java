package com.example.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model representing a crop planted/managed by a farmer.
 */
public class FarmerCrop implements Serializable {
    private String id;
    private String name;
    private String variety;
    private String category;
    private String landArea;
    private String unit;
    private String sowingDate;
    private String harvestDate;
    private String irrigationType;
    private double quantity;
    private double price;
    private String description;
    private String status; // "Planned", "Sown", "Growing", "Ready for Harvest", "Harvested"
    private String imagePreset;
    private String latestHealthStatus; // "Healthy", "Disease Detected", "Under Treatment", "Not Scanned"

    public FarmerCrop() {
        this.category = "Cash Crop";
        this.unit = "Acres";
        this.irrigationType = "Drip Irrigation";
        this.quantity = 10.0;
        this.price = 1950.0;
        this.status = "Growing";
        this.imagePreset = "🌱";
        this.latestHealthStatus = "Healthy";
    }

    public FarmerCrop(String id, String name, String variety, String category, String landArea,
                      String unit, String sowingDate, String harvestDate, String irrigationType,
                      double quantity, double price, String description, String status, String imagePreset) {
        this.id = id;
        this.name = name;
        this.variety = variety;
        this.category = category != null ? category : "Cash Crop";
        this.landArea = landArea;
        this.unit = unit != null ? unit : "Acres";
        this.sowingDate = sowingDate;
        this.harvestDate = harvestDate;
        this.irrigationType = irrigationType != null ? irrigationType : "Drip Irrigation";
        this.quantity = quantity;
        this.price = price;
        this.description = description != null ? description : "";
        this.status = status != null ? status : "Growing";
        this.imagePreset = imagePreset != null ? imagePreset : "🌱";
        this.latestHealthStatus = "Healthy";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLandArea() { return landArea; }
    public void setLandArea(String landArea) { this.landArea = landArea; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getSowingDate() { return sowingDate; }
    public void setSowingDate(String sowingDate) { this.sowingDate = sowingDate; }

    public String getHarvestDate() { return harvestDate; }
    public void setHarvestDate(String harvestDate) { this.harvestDate = harvestDate; }

    public String getIrrigationType() { return irrigationType; }
    public void setIrrigationType(String irrigationType) { this.irrigationType = irrigationType; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getImagePreset() { return imagePreset; }
    public void setImagePreset(String imagePreset) { this.imagePreset = imagePreset; }

    public String getLatestHealthStatus() { return latestHealthStatus; }
    public void setLatestHealthStatus(String latestHealthStatus) { this.latestHealthStatus = latestHealthStatus; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FarmerCrop)) return false;
        FarmerCrop that = (FarmerCrop) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "FarmerCrop{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", variety='" + variety + '\'' +
                ", landArea='" + landArea + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
