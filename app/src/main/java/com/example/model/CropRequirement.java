package com.example.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model representing a farming requirement or crop input demand.
 */
public class CropRequirement implements Serializable {
    private String id;
    private String cropName;
    private String requirementType; // "Fertilizer", "Seed", "Pesticide", "Irrigation", "Equipment"
    private String description;
    private double quantity;
    private String unit;
    private String targetDate;
    private String status; // "Open", "Fulfilled", "Cancelled"

    public CropRequirement() {
        this.status = "Open";
    }

    public CropRequirement(String id, String cropName, String requirementType, String description,
                           double quantity, String unit, String targetDate, String status) {
        this.id = id;
        this.cropName = cropName;
        this.requirementType = requirementType;
        this.description = description;
        this.quantity = quantity;
        this.unit = unit;
        this.targetDate = targetDate;
        this.status = status != null ? status : "Open";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public String getRequirementType() { return requirementType; }
    public void setRequirementType(String requirementType) { this.requirementType = requirementType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getTargetDate() { return targetDate; }
    public void setTargetDate(String targetDate) { this.targetDate = targetDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CropRequirement)) return false;
        CropRequirement that = (CropRequirement) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CropRequirement{" +
                "id='" + id + '\'' +
                ", cropName='" + cropName + '\'' +
                ", requirementType='" + requirementType + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
