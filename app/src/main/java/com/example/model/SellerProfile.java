package com.example.model;

import java.io.Serializable;

/**
 * Model representing a Seller Store Profile.
 */
public class SellerProfile implements Serializable {
    private String sellerId;
    private String shopName;
    private String ownerName;
    private String phone;
    private String village;
    private String taluka;
    private String district;
    private String category;
    private double rating;
    private int totalSalesCount;
    private double totalRevenue;
    private boolean isVerified;
    private String gstin;

    public SellerProfile() {
        this.sellerId = "SELLER_01";
        this.shopName = "Kisan Agri Mart & Seed Center";
        this.ownerName = "Anil Kadam";
        this.phone = "+91 98220 54321";
        this.village = "Narayangaon";
        this.taluka = "Junnar";
        this.district = "Pune";
        this.category = "Agro Chemicals, Seeds & Equipment";
        this.rating = 4.8;
        this.totalSalesCount = 248;
        this.totalRevenue = 385000.0;
        this.isVerified = true;
        this.gstin = "27AABCU9603R1ZM";
    }

    public SellerProfile(String sellerId, String shopName, String ownerName, String phone,
                         String village, String taluka, String district, String category,
                         double rating, int totalSalesCount, double totalRevenue, boolean isVerified, String gstin) {
        this.sellerId = sellerId;
        this.shopName = shopName;
        this.ownerName = ownerName;
        this.phone = phone;
        this.village = village;
        this.taluka = taluka;
        this.district = district;
        this.category = category;
        this.rating = rating;
        this.totalSalesCount = totalSalesCount;
        this.totalRevenue = totalRevenue;
        this.isVerified = isVerified;
        this.gstin = gstin;
    }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }

    public String getTaluka() { return taluka; }
    public void setTaluka(String taluka) { this.taluka = taluka; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getTotalSalesCount() { return totalSalesCount; }
    public void setTotalSalesCount(int totalSalesCount) { this.totalSalesCount = totalSalesCount; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public String getGstin() { return gstin; }
    public void setGstin(String gstin) { this.gstin = gstin; }
}
