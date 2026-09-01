package com.example.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * Model representing a job request / application assigned to a labour worker.
 */
public class LabourApplication implements Serializable {
    private String id;
    private String requirementId;
    private String farmerId;
    private String farmerName;
    private String farmerPhone;
    private String labourId;
    private String labourName;
    private String labourPhone;
    private String workType;
    private String crop;
    private String startDate;
    private String endDate;
    private String startTime;
    private int workingHours;
    private double wage;
    private String wageType;
    private String village;
    private String taluka;
    private double distanceKm;
    private boolean foodProvided;
    private boolean transportProvided;
    private String specialInstructions;
    private String status; // "Pending", "Accepted", "Rejected", "Confirmed", "Scheduled", "In Progress", "Completed", "Cancelled"
    private String sentAt;
    private String respondedAt;
    private String startedAt;
    private String completedAt;
    private Double farmerRating;
    private Double labourRating;
    private String farmerReview;
    private String labourReview;

    public LabourApplication() {
        this.status = "Pending";
        this.sentAt = new SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH).format(new Date());
    }

    public LabourApplication(String id, String requirementId, String farmerId, String farmerName,
                             String farmerPhone, String labourId, String labourName, String labourPhone,
                             String workType, String crop, String startDate, String endDate,
                             String startTime, int workingHours, double wage, String wageType,
                             String village, String taluka, double distanceKm, boolean foodProvided,
                             boolean transportProvided, String specialInstructions, String status) {
        this.id = id;
        this.requirementId = requirementId;
        this.farmerId = farmerId;
        this.farmerName = farmerName;
        this.farmerPhone = farmerPhone;
        this.labourId = labourId;
        this.labourName = labourName;
        this.labourPhone = labourPhone;
        this.workType = workType;
        this.crop = crop;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.workingHours = workingHours;
        this.wage = wage;
        this.wageType = wageType;
        this.village = village;
        this.taluka = taluka;
        this.distanceKm = distanceKm;
        this.foodProvided = foodProvided;
        this.transportProvided = transportProvided;
        this.specialInstructions = specialInstructions;
        this.status = status != null ? status : "Pending";
        this.sentAt = new SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH).format(new Date());
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRequirementId() { return requirementId; }
    public void setRequirementId(String requirementId) { this.requirementId = requirementId; }

    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getLabourId() { return labourId; }
    public void setLabourId(String labourId) { this.labourId = labourId; }

    public String getLabourName() { return labourName; }
    public void setLabourName(String labourName) { this.labourName = labourName; }

    public String getLabourPhone() { return labourPhone; }
    public void setLabourPhone(String labourPhone) { this.labourPhone = labourPhone; }

    public String getWorkType() { return workType; }
    public void setWorkType(String workType) { this.workType = workType; }

    public String getCrop() { return crop; }
    public void setCrop(String crop) { this.crop = crop; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public int getWorkingHours() { return workingHours; }
    public void setWorkingHours(int workingHours) { this.workingHours = workingHours; }

    public double getWage() { return wage; }
    public void setWage(double wage) { this.wage = wage; }

    public String getWageType() { return wageType; }
    public void setWageType(String wageType) { this.wageType = wageType; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getTaluka() { return taluka; }
    public void setTaluka(String taluka) { this.taluka = taluka; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public boolean isFoodProvided() { return foodProvided; }
    public void setFoodProvided(boolean foodProvided) { this.foodProvided = foodProvided; }

    public boolean isTransportProvided() { return transportProvided; }
    public void setTransportProvided(boolean transportProvided) { this.transportProvided = transportProvided; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSentAt() { return sentAt; }
    public void setSentAt(String sentAt) { this.sentAt = sentAt; }

    public String getRespondedAt() { return respondedAt; }
    public void setRespondedAt(String respondedAt) { this.respondedAt = respondedAt; }

    public String getStartedAt() { return startedAt; }
    public void setStartedAt(String startedAt) { this.startedAt = startedAt; }

    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }

    public Double getFarmerRating() { return farmerRating; }
    public void setFarmerRating(Double farmerRating) { this.farmerRating = farmerRating; }

    public Double getLabourRating() { return labourRating; }
    public void setLabourRating(Double labourRating) { this.labourRating = labourRating; }

    public String getFarmerReview() { return farmerReview; }
    public void setFarmerReview(String farmerReview) { this.farmerReview = farmerReview; }

    public String getLabourReview() { return labourReview; }
    public void setLabourReview(String labourReview) { this.labourReview = labourReview; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LabourApplication)) return false;
        LabourApplication that = (LabourApplication) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LabourApplication{" +
                "id='" + id + '\'' +
                ", workType='" + workType + '\'' +
                ", labourName='" + labourName + '\'' +
                ", farmerName='" + farmerName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
