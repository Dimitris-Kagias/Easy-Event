package com.example.easyevent;

import java.io.Serializable;

public class Offer implements Serializable {
    private String businessName;
    private String availability;
    private String location;
    private String description;

    public Offer(String businessName, String availability, String location, String description) {
        this.businessName = businessName;
        this.availability = availability;
        this.location = location;
        this.description = description;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
