package com.wajeba.items;

import java.io.Serializable;

public class ItemReview implements Serializable {

    String id, userName, rating, message;

    public ItemReview(String id, String userName, String rating, String message) {
        this.id = id;
        this.userName = userName;
        this.rating = rating;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
