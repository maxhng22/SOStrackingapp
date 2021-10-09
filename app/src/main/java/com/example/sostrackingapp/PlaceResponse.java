package com.example.sostrackingapp;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class PlaceResponse {

    private String name;
    private double rating;
    private int total_user_rating;
    private Bitmap image;
    private String address;
    private String phone;
    private LatLng latLng;
    private boolean status;

    public PlaceResponse(String name, double rating, int total_user_rating, Bitmap image, String address, String phone, LatLng latLng, boolean status) {
        this.name = name;
        this.rating = rating;
        this.total_user_rating = total_user_rating;
        this.image = image;
        this.address = address;
        this.phone = phone;
        this.latLng = latLng;
        this.status = status;
    }

    public PlaceResponse() {
        this.name = "";
        this.rating = 0;
        this.total_user_rating = 0;
        this.address = "";
        this.phone = "";
        this.status = false;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getTotal_user_rating() {
        return total_user_rating;
    }

    public void setTotal_user_rating(int total_user_rating) {
        this.total_user_rating = total_user_rating;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


}