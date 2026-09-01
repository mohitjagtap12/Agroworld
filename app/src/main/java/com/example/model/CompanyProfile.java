package com.example.model;

import java.io.Serializable;

/**
 * Model representing a Corporate Agro-Processing buyer / Contracting company profile.
 */
public class CompanyProfile implements Serializable {
    private String id;
    private String companyName;
    private String businessType;
    private String contactPerson;
    private String phone;
    private String email;
    private String location;
    private String fssaiGstNumber;
    private boolean verified;
    private double rating;

    public CompanyProfile() {
        this.verified = true;
        this.rating = 4.8;
    }

    public CompanyProfile(String id, String companyName, String businessType, String contactPerson,
                          String phone, String email, String location, String fssaiGstNumber,
                          boolean verified, double rating) {
        this.id = id;
        this.companyName = companyName;
        this.businessType = businessType;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
        this.location = location;
        this.fssaiGstNumber = fssaiGstNumber;
        this.verified = verified;
        this.rating = rating;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getFssaiGstNumber() { return fssaiGstNumber; }
    public void setFssaiGstNumber(String fssaiGstNumber) { this.fssaiGstNumber = fssaiGstNumber; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
}
