package com.example.sostrackingapp;

public class SosMessage {
    private String phone;
    private String message;
    private String status;

    public SosMessage(String phone, String message, String status) {
        this.phone = phone;
        this.message = message;
        this.status = status;
    }

    public SosMessage() {
        this.phone = "";
        this.message = "";
        this.status = "unseen";
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
