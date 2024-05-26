package com.example.easyevent;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String businessName;
    private String eventType;
    private String date;
    private String time;
    private String location;

    public CartItem(String businessName, String eventType, String date, String time, String location) {
        this.businessName = businessName;
        this.eventType = eventType;
        this.date = date;
        this.time = time;
        this.location = location;
    }

    // Getters και setters
    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
