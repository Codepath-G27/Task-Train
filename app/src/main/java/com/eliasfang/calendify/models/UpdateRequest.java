package com.eliasfang.calendify.models;

public class UpdateRequest {

    private String email;
    private String displayName;
    private String password;
    private String phoneNumber;

    public UpdateRequest() {
    }

    public UpdateRequest(String email, String displayName, String password, String phoneNumber) {
        this.email = email;
        this.displayName = displayName;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
