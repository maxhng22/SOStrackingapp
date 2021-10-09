package com.example.sostrackingapp;

public class User {
    private String username;

    public User(String username, String email, String type, String phone) {
        this.username = username;
        this.email = email;
        this.type = type;
        this.phone = phone;
    }

    private String email;
    private String type;
    private String phone;
    private double latitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private double longitude;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public User() {
        this.latitude=0;
        this.longitude=0;
        this.username = "";
        this.email = "";
        this.phone = "";

    }

}
