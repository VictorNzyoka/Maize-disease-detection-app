package com.example.maizedisease;

public class FarmerModel {
    private String userId;
    private String email;
    private String username;
    private String phoneNumber;
    private String location;
    private String userType;

    public FarmerModel() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public FarmerModel(String userId, String email, String username, String phoneNumber, String location, String userType) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.userType = userType;
    }
}