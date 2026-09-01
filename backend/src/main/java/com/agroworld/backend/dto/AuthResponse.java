package com.agroworld.backend.dto;

import com.agroworld.backend.entity.UserEntity;

public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private String userId;
    private String name;
    private String phone;
    private String role;
    private String village;
    private String district;

    public AuthResponse() {}

    public AuthResponse(String token, UserEntity user) {
        this.token = token;
        this.userId = user.getId();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.role = user.getRole();
        this.village = user.getVillage();
        this.district = user.getDistrict();
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
}
