package com.example.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Model representing an agricultural worker or squad leader (Mukadam).
 */
public class LabourWorker implements Serializable {
    private String id;
    private String name;
    private String avatarEmoji;
    private String phone;
    private String squadName;
    private String village;
    private String taluka;
    private String district;
    private double distanceKm;
    private String primarySkill;
    private List<String> skills;
    private int experienceYears;
    private String skillLevel; // "Skilled", "Semi-skilled", "Unskilled"
    private double rating;
    private int totalReviews;
    private int completedJobs;
    private double dailyWage;
    private boolean isAvailable;
    private String availableDates;
    private List<String> preferredWork;
    private int workingRadiusKm;

    public LabourWorker() {
        this.avatarEmoji = "👨‍🌾";
        this.district = "Pune";
        this.skills = new ArrayList<>();
        this.skillLevel = "Skilled";
        this.isAvailable = true;
        this.availableDates = "All Days (Available)";
        this.preferredWork = new ArrayList<>();
        this.workingRadiusKm = 15;
    }

    public LabourWorker(String id, String name, String avatarEmoji, String phone, String squadName,
                        String village, String taluka, String district, double distanceKm,
                        String primarySkill, List<String> skills, int experienceYears,
                        String skillLevel, double rating, int totalReviews, int completedJobs,
                        double dailyWage, boolean isAvailable, String availableDates,
                        List<String> preferredWork, int workingRadiusKm) {
        this.id = id;
        this.name = name;
        this.avatarEmoji = avatarEmoji != null ? avatarEmoji : "👨‍🌾";
        this.phone = phone;
        this.squadName = squadName;
        this.village = village;
        this.taluka = taluka;
        this.district = district != null ? district : "Pune";
        this.distanceKm = distanceKm;
        this.primarySkill = primarySkill;
        this.skills = skills != null ? new ArrayList<>(skills) : new ArrayList<>();
        this.experienceYears = experienceYears;
        this.skillLevel = skillLevel != null ? skillLevel : "Skilled";
        this.rating = rating;
        this.totalReviews = totalReviews;
        this.completedJobs = completedJobs;
        this.dailyWage = dailyWage;
        this.isAvailable = isAvailable;
        this.availableDates = availableDates != null ? availableDates : "All Days (Available)";
        this.preferredWork = preferredWork != null ? new ArrayList<>(preferredWork) : new ArrayList<>();
        this.workingRadiusKm = workingRadiusKm;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAvatarEmoji() { return avatarEmoji; }
    public void setAvatarEmoji(String avatarEmoji) { this.avatarEmoji = avatarEmoji; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSquadName() { return squadName; }
    public void setSquadName(String squadName) { this.squadName = squadName; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getTaluka() { return taluka; }
    public void setTaluka(String taluka) { this.taluka = taluka; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public String getPrimarySkill() { return primarySkill; }
    public void setPrimarySkill(String primarySkill) { this.primarySkill = primarySkill; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills != null ? new ArrayList<>(skills) : new ArrayList<>(); }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

    public String getSkillLevel() { return skillLevel; }
    public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getTotalReviews() { return totalReviews; }
    public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }

    public int getCompletedJobs() { return completedJobs; }
    public void setCompletedJobs(int completedJobs) { this.completedJobs = completedJobs; }

    public double getDailyWage() { return dailyWage; }
    public void setDailyWage(double dailyWage) { this.dailyWage = dailyWage; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public String getAvailableDates() { return availableDates; }
    public void setAvailableDates(String availableDates) { this.availableDates = availableDates; }

    public List<String> getPreferredWork() { return preferredWork; }
    public void setPreferredWork(List<String> preferredWork) { this.preferredWork = preferredWork != null ? new ArrayList<>(preferredWork) : new ArrayList<>(); }

    public int getWorkingRadiusKm() { return workingRadiusKm; }
    public void setWorkingRadiusKm(int workingRadiusKm) { this.workingRadiusKm = workingRadiusKm; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LabourWorker)) return false;
        LabourWorker that = (LabourWorker) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LabourWorker{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", squadName='" + squadName + '\'' +
                ", primarySkill='" + primarySkill + '\'' +
                ", dailyWage=" + dailyWage +
                ", rating=" + rating +
                '}';
    }
}
