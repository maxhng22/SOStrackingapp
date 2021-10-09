package com.example.sostrackingapp;

import android.graphics.Bitmap;

public class Contact {



    public String getUrl_image() {
        return url_image;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }



    public Contact(Bitmap bitmap, String name, String phone, String url_image) {
        this.bitmap = bitmap;
        this.name = name;
        this.phone = phone;
        this.url_image = url_image;
    }

    private String name;
    private String phone;
    private String url_image;
    private Bitmap bitmap;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String key;

    public Contact() {

        this.url_image = "";
        this.name = "";
        this.phone = "";

    }

}
