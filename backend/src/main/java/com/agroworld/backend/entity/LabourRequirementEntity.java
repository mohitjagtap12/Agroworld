package com.agroworld.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "labour_requirements", indexes = {
        @Index(name = "idx_labour_farmer", columnList = "farmer_id"),
        @Index(name = "idx_labour_status", columnList = "status")
})
public class LabourRequirementEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "farmer_id", nullable = false, length = 64)
    private String farmerId;

    @Column(name = "crop_id", length = 64)
    private String cropId;

    @Column(name = "work_type", nullable = false, length = 128)
    private String workType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "workers_required", nullable = false)
    private Integer workersRequired = 1;

    @Column(name = "skill_level", length = 32)
    private String skillLevel = "SEMI_SKILLED";

    @Column(name = "experience_required")
    private Integer experienceRequired = 0;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "start_time", length = 32)
    private String startTime = "08:00 AM";

    @Column(name = "working_hours")
    private Double workingHours = 8.0;

    @Column(name = "wage_type", length = 32)
    private String wageType = "DAILY";

    @Column(name = "wage_amount", nullable = false)
    private Double wageAmount;

    @Column(length = 128)
    private String village;

    @Column(length = 128)
    private String taluka;

    @Column(length = 128)
    private String district;

    @Column(length = 32)
    private String status = "OPEN"; // OPEN, FILLED, IN_PROGRESS, COMPLETED, CANCELLED

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public LabourRequirementEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }
    public String getCropId() { return cropId; }
    public void setCropId(String cropId) { this.cropId = cropId; }
    public String getWorkType() { return workType; }
    public void setWorkType(String workType) { this.workType = workType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getWorkersRequired() { return workersRequired; }
    public void setWorkersRequired(Integer workersRequired) { this.workersRequired = workersRequired; }
    public String getSkillLevel() { return skillLevel; }
    public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }
    public Integer getExperienceRequired() { return experienceRequired; }
    public void setExperienceRequired(Integer experienceRequired) { this.experienceRequired = experienceRequired; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public Double getWorkingHours() { return workingHours; }
    public void setWorkingHours(Double workingHours) { this.workingHours = workingHours; }
    public String getWageType() { return wageType; }
    public void setWageType(String wageType) { this.wageType = wageType; }
    public Double getWageAmount() { return wageAmount; }
    public void setWageAmount(Double wageAmount) { this.wageAmount = wageAmount; }
    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }
    public String getTaluka() { return taluka; }
    public void setTaluka(String taluka) { this.taluka = taluka; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
