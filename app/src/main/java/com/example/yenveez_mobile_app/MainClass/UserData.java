package com.example.yenveez_mobile_app.MainClass;

/** This Class is created to return all the user data */

public class UserData {
    private String userId, userName, userEmail, userBio, imageUrl;

    public UserData(String userId, String userName, String userEmail, String imageUrl, String userBio) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.imageUrl = imageUrl;
        this.userBio = userBio;
    }

    public UserData() {
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
