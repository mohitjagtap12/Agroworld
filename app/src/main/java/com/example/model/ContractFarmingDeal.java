package com.example.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Model representing a corporate contract farming agreement or offer.
 */
public class ContractFarmingDeal implements Serializable {
    private String id;
    private String companyId;
    private String companyName;
    private String companyPhone;
    private String cropName;
    private String cropEmoji;
    private String variety;
    private double requiredQuantityTons;
    private String unit; // "Tons", "Quintals"
    private double offeredPricePerTon;
    private String priceUnit; // "Ton", "Quintal"
    private String harvestPeriod;
    private String location; // delivery location / procurement cluster
    private String qualitySpecs;
    private String termsAndConditions;
    private int advancePaymentPercent;
    private String applicationDeadline;
    private String contractDuration;
    private String paymentTerms;
    private String additionalConditions;
    private String status; // "Open", "Draft", "Published", "Active", "Closed", "Completed"
    private String datePublished;

    public ContractFarmingDeal() {
        this.status = "Open";
        this.advancePaymentPercent = 20;
        this.unit = "Tons";
        this.priceUnit = "Ton";
        this.cropEmoji = "🌾";
    }

    public ContractFarmingDeal(String id, String companyId, String companyName, String companyPhone,
                               String cropName, String cropEmoji, String variety,
                               double requiredQuantityTons, String unit,
                               double offeredPricePerTon, String priceUnit,
                               String harvestPeriod, String location, String qualitySpecs,
                               String termsAndConditions, int advancePaymentPercent,
                               String applicationDeadline, String contractDuration,
                               String paymentTerms, String additionalConditions,
                               String status, String datePublished) {
        this.id = id;
        this.companyId = companyId != null ? companyId : "comp_01";
        this.companyName = companyName;
        this.companyPhone = companyPhone != null ? companyPhone : "+91 20 2554 8899";
        this.cropName = cropName;
        this.cropEmoji = cropEmoji != null ? cropEmoji : "🌾";
        this.variety = variety;
        this.requiredQuantityTons = requiredQuantityTons;
        this.unit = unit != null ? unit : "Tons";
        this.offeredPricePerTon = offeredPricePerTon;
        this.priceUnit = priceUnit != null ? priceUnit : "Ton";
        this.harvestPeriod = harvestPeriod;
        this.location = location;
        this.qualitySpecs = qualitySpecs;
        this.termsAndConditions = termsAndConditions;
        this.advancePaymentPercent = advancePaymentPercent;
        this.applicationDeadline = applicationDeadline;
        this.contractDuration = contractDuration;
        this.paymentTerms = paymentTerms;
        this.additionalConditions = additionalConditions;
        this.status = status != null ? status : "Open";
        this.datePublished = datePublished;
    }

    // Convenience constructor for backward compatibility
    public ContractFarmingDeal(String id, String companyName, String cropName, String variety,
                               double requiredQuantityTons, double offeredPricePerTon,
                               String harvestPeriod, String location, String qualitySpecs,
                               String termsAndConditions, int advancePaymentPercent,
                               String status, String datePublished) {
        this(id, "comp_01", companyName, "+91 20 2554 8899", cropName, "🌾", variety,
                requiredQuantityTons, "Tons", offeredPricePerTon, "Ton",
                harvestPeriod, location, qualitySpecs, termsAndConditions, advancePaymentPercent,
                "15 Sept 2026", "6 Months", "25% Advance, remaining on delivery",
                "100% buyback guarantee at designated collection center", status, datePublished);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyPhone() { return companyPhone; }
    public void setCompanyPhone(String companyPhone) { this.companyPhone = companyPhone; }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public String getCropEmoji() { return cropEmoji; }
    public void setCropEmoji(String cropEmoji) { this.cropEmoji = cropEmoji; }

    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }

    public double getRequiredQuantityTons() { return requiredQuantityTons; }
    public void setRequiredQuantityTons(double requiredQuantityTons) { this.requiredQuantityTons = requiredQuantityTons; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getOfferedPricePerTon() { return offeredPricePerTon; }
    public void setOfferedPricePerTon(double offeredPricePerTon) { this.offeredPricePerTon = offeredPricePerTon; }

    public String getPriceUnit() { return priceUnit; }
    public void setPriceUnit(String priceUnit) { this.priceUnit = priceUnit; }

    public String getHarvestPeriod() { return harvestPeriod; }
    public void setHarvestPeriod(String harvestPeriod) { this.harvestPeriod = harvestPeriod; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getQualitySpecs() { return qualitySpecs; }
    public void setQualitySpecs(String qualitySpecs) { this.qualitySpecs = qualitySpecs; }

    public String getTermsAndConditions() { return termsAndConditions; }
    public void setTermsAndConditions(String termsAndConditions) { this.termsAndConditions = termsAndConditions; }

    public int getAdvancePaymentPercent() { return advancePaymentPercent; }
    public void setAdvancePaymentPercent(int advancePaymentPercent) { this.advancePaymentPercent = advancePaymentPercent; }

    public String getApplicationDeadline() { return applicationDeadline; }
    public void setApplicationDeadline(String applicationDeadline) { this.applicationDeadline = applicationDeadline; }

    public String getContractDuration() { return contractDuration; }
    public void setContractDuration(String contractDuration) { this.contractDuration = contractDuration; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }

    public String getAdditionalConditions() { return additionalConditions; }
    public void setAdditionalConditions(String additionalConditions) { this.additionalConditions = additionalConditions; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDatePublished() { return datePublished; }
    public void setDatePublished(String datePublished) { this.datePublished = datePublished; }

    /**
     * Checks if this contract matches any of the farmer's registered crops.
     */
    public boolean matchesFarmerCrops(List<FarmerCrop> farmerCrops) {
        if (farmerCrops == null || farmerCrops.isEmpty() || cropName == null) return false;
        String normalizedContractCrop = cropName.toLowerCase().trim();
        for (FarmerCrop crop : farmerCrops) {
            if (crop.getName() == null) continue;
            String normalizedFarmerCrop = crop.getName().toLowerCase().trim();
            if (normalizedContractCrop.contains(normalizedFarmerCrop) ||
                    normalizedFarmerCrop.contains(normalizedContractCrop) ||
                    matchCropKeywords(normalizedContractCrop, normalizedFarmerCrop)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchCropKeywords(String a, String b) {
        if ((a.contains("onion") && b.contains("onion")) ||
            (a.contains("wheat") && b.contains("wheat")) ||
            (a.contains("tomato") && b.contains("tomato")) ||
            (a.contains("rice") && b.contains("rice")) ||
            (a.contains("sugar") && b.contains("sugar")) ||
            (a.contains("potato") && b.contains("potato")) ||
            (a.contains("mango") && b.contains("mango")) ||
            (a.contains("soy") && b.contains("soy"))) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContractFarmingDeal)) return false;
        ContractFarmingDeal that = (ContractFarmingDeal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
