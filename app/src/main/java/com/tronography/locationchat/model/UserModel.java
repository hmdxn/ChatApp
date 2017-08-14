package com.tronography.locationchat.model;

import android.support.annotation.Nullable;


public class UserModel {
    private String id;
    private String username;
    private String profilePhoto;
    private String bio;
    private String location;

    public UserModel() {
    }

    public UserModel(String id, String username, String profilePhoto, String bio, String location) {
        this.id = id;
        this.username = username;
        this.profilePhoto = profilePhoto;
        this.bio = bio;
        this.location = location;
    }

    public UserModel(String id, String username, @Nullable String profilePhoto) {
        this.id = id;
        this.username = username;
        this.profilePhoto = profilePhoto;
    }

    public UserModel(String username, String id) {
        this.id = id;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                ", id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", profilePhoto=" + profilePhoto +
                '}';
    }


}
