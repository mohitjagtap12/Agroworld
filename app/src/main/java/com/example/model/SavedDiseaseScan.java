package com.example.model;

import android.graphics.Bitmap;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Model representing a saved disease scan diagnosis in the farmer's history archive.
 */
public class SavedDiseaseScan implements Serializable {
    private String id;
    private String farmerId;
    private String cropId;
    private String cropName;
    private String diseaseName;
    private String confidence;
    private int confidencePercent;
    private String severity;
    private boolean isHealthy;
    private boolean isLowConfidence;
    private List<String> symptoms;
    private List<String> possibleCauses;
    private List<String> recommendedAction;
    private List<String> prevention;
    private String imageQuality;
    private String modelName;
    private transient Bitmap imageBitmap;
    private long timestamp;
    private String formattedDate;

    public SavedDiseaseScan() {
        this.farmerId = "FARMER_MH_01";
        this.confidencePercent = 90;
        this.severity = "Moderate";
        this.symptoms = new ArrayList<>();
        this.possibleCauses = new ArrayList<>();
        this.recommendedAction = new ArrayList<>();
        this.prevention = new ArrayList<>();
        this.imageQuality = "good";
        this.modelName = "gemini-3.5-flash";
        this.timestamp = System.currentTimeMillis();
        this.formattedDate = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(new Date());
    }

    public SavedDiseaseScan(String id, String cropName, String diseaseName, String confidence,
                            String severity, List<String> symptoms, List<String> possibleCauses,
                            List<String> recommendedAction, List<String> prevention,
                            String imageQuality, String formattedDate) {
        this();
        this.id = id;
        this.cropName = cropName;
        this.diseaseName = diseaseName;
        this.confidence = confidence;
        this.severity = severity != null ? severity : "Moderate";
        if (symptoms != null) this.symptoms = new ArrayList<>(symptoms);
        if (possibleCauses != null) this.possibleCauses = new ArrayList<>(possibleCauses);
        if (recommendedAction != null) this.recommendedAction = new ArrayList<>(recommendedAction);
        if (prevention != null) this.prevention = new ArrayList<>(prevention);
        this.imageQuality = imageQuality != null ? imageQuality : "good";
        this.formattedDate = formattedDate != null ? formattedDate : this.formattedDate;
    }

    public SavedDiseaseScan(String id, String farmerId, String cropId, String cropName,
                            String diseaseName, String confidence, int confidencePercent,
                            String severity, boolean isHealthy, boolean isLowConfidence,
                            List<String> symptoms, List<String> possibleCauses,
                            List<String> recommendedAction, List<String> prevention,
                            String imageQuality, String modelName, Bitmap imageBitmap,
                            long timestamp, String formattedDate) {
        this.id = id;
        this.farmerId = farmerId != null ? farmerId : "FARMER_MH_01";
        this.cropId = cropId != null ? cropId : "";
        this.cropName = cropName;
        this.diseaseName = diseaseName;
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
        this.modelName = modelName != null ? modelName : "gemini-3.5-flash";
        this.imageBitmap = imageBitmap;
        this.timestamp = timestamp != 0 ? timestamp : System.currentTimeMillis();
        this.formattedDate = formattedDate != null ? formattedDate :
                new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(new Date(this.timestamp));
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }

    public String getCropId() { return cropId; }
    public void setCropId(String cropId) { this.cropId = cropId; }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public String getDiseaseName() { return diseaseName; }
    public void setDiseaseName(String diseaseName) { this.diseaseName = diseaseName; }

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

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public Bitmap getImageBitmap() { return imageBitmap; }
    public void setImageBitmap(Bitmap imageBitmap) { this.imageBitmap = imageBitmap; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getFormattedDate() { return formattedDate; }
    public void setFormattedDate(String formattedDate) { this.formattedDate = formattedDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SavedDiseaseScan)) return false;
        SavedDiseaseScan that = (SavedDiseaseScan) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SavedDiseaseScan{" +
                "id='" + id + '\'' +
                ", cropName='" + cropName + '\'' +
                ", diseaseName='" + diseaseName + '\'' +
                ", confidence='" + confidence + '\'' +
                ", formattedDate='" + formattedDate + '\'' +
                '}';
    }
}
