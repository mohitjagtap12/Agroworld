package com.example.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Model representing a Farmer's posted Labour hiring requirement.
 */
public class LabourRequirement implements Serializable {
    private String id;
    private String farmerId;
    private String farmerName;
    private String farmerPhone;
    private String workType;
    private String customWorkType;
    private String crop;
    private String description;
    private int workersRequired;
    private String skillLevel; // "Skilled", "Semi-skilled", "Unskilled", "Any"
    private String experienceRequired; // "No experience required", "1+ year", "2+ years", "3+ years", "Custom"
    private List<String> requiredSkills;
    private String startDate;
    private String endDate;
    private String startTime;
    private int workingHoursPerDay;
    private String wageType; // "Per Day", "Per Hour", "Fixed Job Amount"
    private double wageAmount;
    private String paymentTerms; // "Daily Cash", "End of Job", "UPI / Direct Transfer"
    private String village;
    private String taluka;
    private String district;
    private String farmLocation;
    private int searchRadiusKm;
    private String specialInstructions;
    private String requiredEquipment;
    private boolean foodProvided;
    private boolean transportProvided;
    private String otherRequirements;
    private String status; // "Finding Labour", "Request Sent", "Accepted", "Confirmed", "Scheduled", "Work Started", "Work Completed", "Completed", "Cancelled"
    private List<String> workerIdsRequested;
    private List<String> workerIdsAccepted;
    private List<String> workerIdsRejected;
    private List<String> workerIdsConfirmed;
    private String createdAt;
    private Double farmerRating;
    private String farmerReview;

    public LabourRequirement() {
        this.farmerId = "FARMER_01";
        this.district = "Pune";
        this.skillLevel = "Skilled";
        this.experienceRequired = "1+ year";
        this.requiredSkills = new ArrayList<>();
        this.workingHoursPerDay = 8;
        this.wageType = "Per Day";
        this.paymentTerms = "Daily Cash";
        this.searchRadiusKm = 10;
        this.foodProvided = true;
        this.transportProvided = false;
        this.status = "Finding Labour";
        this.workerIdsRequested = new ArrayList<>();
        this.workerIdsAccepted = new ArrayList<>();
        this.workerIdsRejected = new ArrayList<>();
        this.workerIdsConfirmed = new ArrayList<>();
        this.createdAt = new SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH).format(new Date());
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getWorkType() { return workType; }
    public void setWorkType(String workType) { this.workType = workType; }

    public String getCustomWorkType() { return customWorkType; }
    public void setCustomWorkType(String customWorkType) { this.customWorkType = customWorkType; }

    public String getCrop() { return crop; }
    public void setCrop(String crop) { this.crop = crop; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getWorkersRequired() { return workersRequired; }
    public void setWorkersRequired(int workersRequired) { this.workersRequired = workersRequired; }

    public String getSkillLevel() { return skillLevel; }
    public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }

    public String getExperienceRequired() { return experienceRequired; }
    public void setExperienceRequired(String experienceRequired) { this.experienceRequired = experienceRequired; }

    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills != null ? new ArrayList<>(requiredSkills) : new ArrayList<>(); }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public int getWorkingHoursPerDay() { return workingHoursPerDay; }
    public void setWorkingHoursPerDay(int workingHoursPerDay) { this.workingHoursPerDay = workingHoursPerDay; }

    public String getWageType() { return wageType; }
    public void setWageType(String wageType) { this.wageType = wageType; }

    public double getWageAmount() { return wageAmount; }
    public void setWageAmount(double wageAmount) { this.wageAmount = wageAmount; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getTaluka() { return taluka; }
    public void setTaluka(String taluka) { this.taluka = taluka; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getFarmLocation() { return farmLocation; }
    public void setFarmLocation(String farmLocation) { this.farmLocation = farmLocation; }

    public int getSearchRadiusKm() { return searchRadiusKm; }
    public void setSearchRadiusKm(int searchRadiusKm) { this.searchRadiusKm = searchRadiusKm; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public String getRequiredEquipment() { return requiredEquipment; }
    public void setRequiredEquipment(String requiredEquipment) { this.requiredEquipment = requiredEquipment; }

    public boolean isFoodProvided() { return foodProvided; }
    public void setFoodProvided(boolean foodProvided) { this.foodProvided = foodProvided; }

    public boolean isTransportProvided() { return transportProvided; }
    public void setTransportProvided(boolean transportProvided) { this.transportProvided = transportProvided; }

    public String getOtherRequirements() { return otherRequirements; }
    public void setOtherRequirements(String otherRequirements) { this.otherRequirements = otherRequirements; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getWorkerIdsRequested() { return workerIdsRequested; }
    public void setWorkerIdsRequested(List<String> workerIdsRequested) { this.workerIdsRequested = workerIdsRequested != null ? new ArrayList<>(workerIdsRequested) : new ArrayList<>(); }

    public List<String> getWorkerIdsAccepted() { return workerIdsAccepted; }
    public void setWorkerIdsAccepted(List<String> workerIdsAccepted) { this.workerIdsAccepted = workerIdsAccepted != null ? new ArrayList<>(workerIdsAccepted) : new ArrayList<>(); }

    public List<String> getWorkerIdsRejected() { return workerIdsRejected; }
    public void setWorkerIdsRejected(List<String> workerIdsRejected) { this.workerIdsRejected = workerIdsRejected != null ? new ArrayList<>(workerIdsRejected) : new ArrayList<>(); }

    public List<String> getWorkerIdsConfirmed() { return workerIdsConfirmed; }
    public void setWorkerIdsConfirmed(List<String> workerIdsConfirmed) { this.workerIdsConfirmed = workerIdsConfirmed != null ? new ArrayList<>(workerIdsConfirmed) : new ArrayList<>(); }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public Double getFarmerRating() { return farmerRating; }
    public void setFarmerRating(Double farmerRating) { this.farmerRating = farmerRating; }

    public String getFarmerReview() { return farmerReview; }
    public void setFarmerReview(String farmerReview) { this.farmerReview = farmerReview; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LabourRequirement)) return false;
        LabourRequirement that = (LabourRequirement) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LabourRequirement{" +
                "id='" + id + '\'' +
                ", workType='" + workType + '\'' +
                ", crop='" + crop + '\'' +
                ", workersRequired=" + workersRequired +
                ", status='" + status + '\'' +
                '}';
    }
}
