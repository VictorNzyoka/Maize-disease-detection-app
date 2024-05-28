package com.example.maizedisease;

public class UserModel {
    private String userId;
    private String username;
    private String email;
    private String phoneNumber;
    private String userType;

    public UserModel() {
        // Default constructor required for Firebase deserialization
    }

    public UserModel(String userId, String username, String email, String phoneNumber, String userType) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUserType() {
        return userType;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}