package com.example.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model representing a farmer's formal application for a corporate contract farming deal.
 */
public class ContractApplication implements Serializable {
    private String id;
    private String contractId;
    private String companyId;
    private String companyName;
    private String farmerId;
    private String farmerName;
    private String farmerPhone;
    private String village;
    private String taluka;
    private String district;
    private String cropName;
    private String cropEmoji;
    private String cropVariety;
    private double landAreaAcres;
    private double expectedQuantityTons;
    private String expectedHarvestDate;
    private String qualityGradeNotes;
    private String additionalMessage;
    private double agreedPricePerTon;
    private String status; // "Submitted", "Under Review", "Accepted", "Rejected", "Confirmed", "Active", "Harvest Ready", "Delivered", "Completed"
    private String submittedDate;
    private String reviewedDate;
    private int milestoneProgressPercent;
    private String currentMilestone;

    public ContractApplication() {
        this.status = "Submitted";
        this.milestoneProgressPercent = 10;
        this.currentMilestone = "Application Submitted";
        this.cropEmoji = "🌾";
    }

    public ContractApplication(String id, String contractId, String companyId, String companyName,
                               String farmerId, String farmerName, String farmerPhone,
                               String village, String taluka, String district,
                               String cropName, String cropEmoji, String cropVariety,
                               double landAreaAcres, double expectedQuantityTons,
                               String expectedHarvestDate, String qualityGradeNotes,
                               String additionalMessage, double agreedPricePerTon,
                               String status, String submittedDate, String reviewedDate,
                               int milestoneProgressPercent, String currentMilestone) {
        this.id = id;
        this.contractId = contractId;
        this.companyId = companyId;
        this.companyName = companyName;
        this.farmerId = farmerId;
        this.farmerName = farmerName;
        this.farmerPhone = farmerPhone;
        this.village = village;
        this.taluka = taluka;
        this.district = district;
        this.cropName = cropName;
        this.cropEmoji = cropEmoji != null ? cropEmoji : "🌾";
        this.cropVariety = cropVariety;
        this.landAreaAcres = landAreaAcres;
        this.expectedQuantityTons = expectedQuantityTons;
        this.expectedHarvestDate = expectedHarvestDate;
        this.qualityGradeNotes = qualityGradeNotes;
        this.additionalMessage = additionalMessage;
        this.agreedPricePerTon = agreedPricePerTon;
        this.status = status != null ? status : "Submitted";
        this.submittedDate = submittedDate;
        this.reviewedDate = reviewedDate;
        this.milestoneProgressPercent = milestoneProgressPercent;
        this.currentMilestone = currentMilestone != null ? currentMilestone : "Application Submitted";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContractId() { return contractId; }
    public void setContractId(String contractId) { this.contractId = contractId; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getTaluka() { return taluka; }
    public void setTaluka(String taluka) { this.taluka = taluka; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public String getCropEmoji() { return cropEmoji; }
    public void setCropEmoji(String cropEmoji) { this.cropEmoji = cropEmoji; }

    public String getCropVariety() { return cropVariety; }
    public void setCropVariety(String cropVariety) { this.cropVariety = cropVariety; }

    public double getLandAreaAcres() { return landAreaAcres; }
    public void setLandAreaAcres(double landAreaAcres) { this.landAreaAcres = landAreaAcres; }

    public double getExpectedQuantityTons() { return expectedQuantityTons; }
    public void setExpectedQuantityTons(double expectedQuantityTons) { this.expectedQuantityTons = expectedQuantityTons; }

    public String getExpectedHarvestDate() { return expectedHarvestDate; }
    public void setExpectedHarvestDate(String expectedHarvestDate) { this.expectedHarvestDate = expectedHarvestDate; }

    public String getQualityGradeNotes() { return qualityGradeNotes; }
    public void setQualityGradeNotes(String qualityGradeNotes) { this.qualityGradeNotes = qualityGradeNotes; }

    public String getAdditionalMessage() { return additionalMessage; }
    public void setAdditionalMessage(String additionalMessage) { this.additionalMessage = additionalMessage; }

    public double getAgreedPricePerTon() { return agreedPricePerTon; }
    public void setAgreedPricePerTon(double agreedPricePerTon) { this.agreedPricePerTon = agreedPricePerTon; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(String submittedDate) { this.submittedDate = submittedDate; }

    public String getReviewedDate() { return reviewedDate; }
    public void setReviewedDate(String reviewedDate) { this.reviewedDate = reviewedDate; }

    public int getMilestoneProgressPercent() { return milestoneProgressPercent; }
    public void setMilestoneProgressPercent(int milestoneProgressPercent) { this.milestoneProgressPercent = milestoneProgressPercent; }

    public String getCurrentMilestone() { return currentMilestone; }
    public void setCurrentMilestone(String currentMilestone) { this.currentMilestone = currentMilestone; }

    public double calculateTotalContractValue() {
        return agreedPricePerTon * expectedQuantityTons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContractApplication)) return false;
        ContractApplication that = (ContractApplication) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
