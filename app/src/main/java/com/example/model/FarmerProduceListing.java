package com.example.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model representing direct farm produce listed for sale by a farmer.
 */
public class FarmerProduceListing implements Serializable {
    private String id;
    private String farmerId;
    private String farmerName;
    private String farmerPhone;
    private String produceName;
    private String category; // "Vegetables", "Fruits", "Cereals", "Pulses", "Spices", "Oilseeds", "Other Crops"
    private double initialQuantity;
    private double quantityAvailable;
    private String unit; // "kg", "quintal", "ton", "crate", "piece", "dozen"
    private double pricePerKg;
    private String priceUnit; // "kg", "quintal", "crate", "piece", "dozen"
    private String qualityGrade; // "Grade A", "Grade B", "Organic Premium", "FAQ Standard"
    private String harvestDate;
    private String availableFrom;
    private String availableUntil;
    private String description;
    private String status; // "Available", "Partially Sold", "Sold Out", "Paused", "Expired", "Cancelled"
    private String village;
    private String taluka;
    private String district;
    private String imageEmoji;
    private long createdAt;

    public FarmerProduceListing() {
        this.status = "Available";
        this.category = "Vegetables";
        this.unit = "kg";
        this.priceUnit = "kg";
        this.imageEmoji = "🌾";
        this.createdAt = System.currentTimeMillis();
    }

    public FarmerProduceListing(String id, String farmerId, String farmerName, String produceName,
                                double quantityAvailable, String unit, double pricePerKg,
                                String qualityGrade, String harvestDate, String description,
                                String status, String village, String imageEmoji) {
        this.id = id;
        this.farmerId = farmerId;
        this.farmerName = farmerName;
        this.produceName = produceName;
        this.category = "Vegetables";
        this.initialQuantity = quantityAvailable;
        this.quantityAvailable = quantityAvailable;
        this.unit = unit != null ? unit : "kg";
        this.pricePerKg = pricePerKg;
        this.priceUnit = unit != null ? unit : "kg";
        this.qualityGrade = qualityGrade;
        this.harvestDate = harvestDate;
        this.availableFrom = harvestDate;
        this.availableUntil = "30 Days from Harvest";
        this.description = description;
        this.status = status != null ? status : "Available";
        this.village = village;
        this.taluka = "Haveli";
        this.district = "Pune";
        this.imageEmoji = imageEmoji != null ? imageEmoji : "🌾";
        this.createdAt = System.currentTimeMillis();
    }

    public FarmerProduceListing(String id, String farmerId, String farmerName, String farmerPhone,
                                String produceName, String category, double quantityAvailable,
                                String unit, double pricePerKg, String priceUnit, String qualityGrade,
                                String harvestDate, String availableFrom, String availableUntil,
                                String village, String taluka, String district, String description,
                                String status, String imageEmoji) {
        this.id = id;
        this.farmerId = farmerId;
        this.farmerName = farmerName;
        this.farmerPhone = farmerPhone;
        this.produceName = produceName;
        this.category = category != null ? category : "Vegetables";
        this.initialQuantity = quantityAvailable;
        this.quantityAvailable = quantityAvailable;
        this.unit = unit != null ? unit : "kg";
        this.pricePerKg = pricePerKg;
        this.priceUnit = priceUnit != null ? priceUnit : "kg";
        this.qualityGrade = qualityGrade;
        this.harvestDate = harvestDate;
        this.availableFrom = availableFrom;
        this.availableUntil = availableUntil;
        this.village = village;
        this.taluka = taluka;
        this.district = district;
        this.description = description;
        this.status = status != null ? status : "Available";
        this.imageEmoji = imageEmoji != null ? imageEmoji : "🌾";
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getProduceName() { return produceName; }
    public void setProduceName(String produceName) { this.produceName = produceName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getInitialQuantity() { return initialQuantity; }
    public void setInitialQuantity(double initialQuantity) { this.initialQuantity = initialQuantity; }

    public double getQuantityAvailable() { return quantityAvailable; }
    public void setQuantityAvailable(double quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
        if (quantityAvailable <= 0) {
            this.status = "Sold Out";
        } else if (initialQuantity > 0 && quantityAvailable < initialQuantity) {
            this.status = "Partially Sold";
        }
    }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getPricePerKg() { return pricePerKg; }
    public void setPricePerKg(double pricePerKg) { this.pricePerKg = pricePerKg; }

    public String getPriceUnit() { return priceUnit != null ? priceUnit : unit; }
    public void setPriceUnit(String priceUnit) { this.priceUnit = priceUnit; }

    public String getQualityGrade() { return qualityGrade; }
    public void setQualityGrade(String qualityGrade) { this.qualityGrade = qualityGrade; }

    public String getHarvestDate() { return harvestDate; }
    public void setHarvestDate(String harvestDate) { this.harvestDate = harvestDate; }

    public String getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(String availableFrom) { this.availableFrom = availableFrom; }

    public String getAvailableUntil() { return availableUntil; }
    public void setAvailableUntil(String availableUntil) { this.availableUntil = availableUntil; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getTaluka() { return taluka; }
    public void setTaluka(String taluka) { this.taluka = taluka; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getImageEmoji() { return imageEmoji; }
    public void setImageEmoji(String imageEmoji) { this.imageEmoji = imageEmoji; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public String getFullLocation() {
        StringBuilder sb = new StringBuilder();
        if (village != null && !village.isEmpty()) sb.append(village);
        if (taluka != null && !taluka.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(taluka);
        }
        if (district != null && !district.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(district);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FarmerProduceListing)) return false;
        FarmerProduceListing that = (FarmerProduceListing) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "FarmerProduceListing{" +
                "id='" + id + '\'' +
                ", produceName='" + produceName + '\'' +
                ", quantityAvailable=" + quantityAvailable +
                ", pricePerKg=" + pricePerKg +
                ", status='" + status + '\'' +
                '}';
    }
}
