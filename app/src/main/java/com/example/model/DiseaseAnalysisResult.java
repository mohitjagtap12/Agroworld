package com.example.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Model representing the structured output from AI Crop Disease Analysis.
 */
public class DiseaseAnalysisResult implements Serializable {
    private boolean isSuccess;
    private String crop;
    private String disease;
    private String confidence; // "High", "Moderate", "Low"
    private int confidencePercent;
    private String severity; // "Low", "Moderate", "High", "Severe", "None", "Unknown"
    private boolean isHealthy;
    private boolean isLowConfidence;
    private List<String> symptoms;
    private List<String> possibleCauses;
    private List<String> recommendedAction;
    private List<String> prevention;
    private String imageQuality; // "good", "adequate", "poor", "unclear"
    private boolean isUnclearOrPoorQuality;
    private String errorMessage;
    private String statusMessage;
    private String modelName;

    public DiseaseAnalysisResult() {
        this.confidencePercent = 90;
        this.severity = "Moderate";
        this.symptoms = new ArrayList<>();
        this.possibleCauses = new ArrayList<>();
        this.recommendedAction = new ArrayList<>();
        this.prevention = new ArrayList<>();
        this.imageQuality = "good";
        this.modelName = "gemini-3.5-flash";
    }

    public DiseaseAnalysisResult(boolean isSuccess, String crop, String disease, String confidence,
                                 int confidencePercent, String severity, boolean isHealthy,
                                 boolean isLowConfidence, List<String> symptoms,
                                 List<String> possibleCauses, List<String> recommendedAction,
                                 List<String> prevention, String imageQuality,
                                 boolean isUnclearOrPoorQuality, String errorMessage,
                                 String statusMessage, String modelName) {
        this.isSuccess = isSuccess;
        this.crop = crop;
        this.disease = disease;
        this.confidence = confidence;
        this.confidencePercent = confidencePercent;
        this.severity = severity != null ? severity : "Moderate";
        this.isHealthy = isHealthy;
        this.isLowConfidence = isLowConfidence;
        this.symptoms = symptoms != null ? new ArrayList<>(symptoms) : new ArrayList<>();
        this.possibleCauses = possibleCauses != null ? new ArrayList<>(possibleCauses) : new ArrayList<>();
        this.recommendedAction = recommendedAction != null ? new ArrayList<>(recommendedAction) : new ArrayList<>();
        this.prevention = prevention != null ? new ArrayList<>(prevention) : new ArrayList<>();
        this.imageQuality = imageQuality != null ? imageQuality : "good";
        this.isUnclearOrPoorQuality = isUnclearOrPoorQuality;
        this.errorMessage = errorMessage;
        this.statusMessage = statusMessage;
        this.modelName = modelName != null ? modelName : "gemini-3.5-flash";
    }

    public boolean isSuccess() { return isSuccess; }
    public void setSuccess(boolean success) { isSuccess = success; }

    public String getCrop() { return crop; }
    public void setCrop(String crop) { this.crop = crop; }

    public String getDisease() { return disease; }
    public void setDisease(String disease) { this.disease = disease; }

    public String getConfidence() { return confidence; }
    public void setConfidence(String confidence) { this.confidence = confidence; }

    public int getConfidencePercent() { return confidencePercent; }
    public void setConfidencePercent(int confidencePercent) { this.confidencePercent = confidencePercent; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public boolean isHealthy() { return isHealthy; }
    public void setHealthy(boolean healthy) { isHealthy = healthy; }

    public boolean isLowConfidence() { return isLowConfidence; }
    public void setLowConfidence(boolean lowConfidence) { isLowConfidence = lowConfidence; }

    public List<String> getSymptoms() { return symptoms; }
    public void setSymptoms(List<String> symptoms) { this.symptoms = symptoms != null ? new ArrayList<>(symptoms) : new ArrayList<>(); }

    public List<String> getPossibleCauses() { return possibleCauses; }
    public void setPossibleCauses(List<String> possibleCauses) { this.possibleCauses = possibleCauses != null ? new ArrayList<>(possibleCauses) : new ArrayList<>(); }

    public List<String> getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(List<String> recommendedAction) { this.recommendedAction = recommendedAction != null ? new ArrayList<>(recommendedAction) : new ArrayList<>(); }

    public List<String> getPrevention() { return prevention; }
    public void setPrevention(List<String> prevention) { this.prevention = prevention != null ? new ArrayList<>(prevention) : new ArrayList<>(); }

    public String getImageQuality() { return imageQuality; }
    public void setImageQuality(String imageQuality) { this.imageQuality = imageQuality; }

    public boolean isUnclearOrPoorQuality() { return isUnclearOrPoorQuality; }
    public void setUnclearOrPoorQuality(boolean unclearOrPoorQuality) { isUnclearOrPoorQuality = unclearOrPoorQuality; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    @Override
    public String toString() {
        return "DiseaseAnalysisResult{" +
                "isSuccess=" + isSuccess +
                ", crop='" + crop + '\'' +
                ", disease='" + disease + '\'' +
                ", confidence='" + confidence + '\'' +
                ", severity='" + severity + '\'' +
                '}';
    }
}
