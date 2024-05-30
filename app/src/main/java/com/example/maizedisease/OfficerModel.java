package com.example.maizedisease;

public class OfficerModel {
    private String userId;
    private String email;
    private String username;
    private String phoneNumber;
    private String location;
    private int experience;
    private String userType;

    public OfficerModel() {
    }

    public OfficerModel(String userId, String username, String email, String phoneNumber, String location, int experience, String userType) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.experience = experience;
        this.userType = userType;
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

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}