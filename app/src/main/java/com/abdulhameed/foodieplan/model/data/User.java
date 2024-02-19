package com.abdulhameed.foodieplan.model.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User {
    private String id, email, userName, profileUrl;

    public User(String id, String email, String userName, String profileUrl) {
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.profileUrl = profileUrl;
    }

    public User(String id, String email, String userName) {
        this.id = id;
        this.email = email;
        this.userName = userName;
    }

    public User() {
    }

    public static String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null;
        }
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
