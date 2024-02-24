package com.abdulhameed.foodieplan.model.data;

public class User {
    private String id;
    private String email;
    private String userName;
    private String password;
    private String profileUrl;

    public User(String id, String email, String userName, String profileUrl) {
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.profileUrl = profileUrl;
    }

    public User(String email, String userName, String password) {
        this.email = email;
        this.userName = userName;
        this.password = password;
    }

    public User() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                ", profileUrl='" + profileUrl + '\'' +
                '}';
    }
}
