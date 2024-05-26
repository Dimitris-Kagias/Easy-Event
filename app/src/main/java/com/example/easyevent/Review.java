package com.example.easyevent;


import java.io.Serializable;

public class Review implements Serializable {
    private String user;
    private String comment;
    private float rating;

    public Review(String user, String comment, float rating) {
        this.user = user;
        this.comment = comment;
        this.rating = rating;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
