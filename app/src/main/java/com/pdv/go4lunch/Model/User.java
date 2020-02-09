package com.pdv.go4lunch.Model;

public class User {

    private String Uid;
    private String userName;
    private String urlPicture;
    private String restaurantName;
    private String restaurantId;

    public User (){}

    public User(String Uid, String userName, String urlPicture) {
        this.Uid = Uid;
        this.userName = userName;
        this.urlPicture = urlPicture;
    }

    public User(String Uid, String userName) {
        this.Uid = Uid;
        this.userName = userName;
    }

    public String getId() {
        return Uid;
    }

    public void setId(String Uid) {
        this.Uid = Uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
}
