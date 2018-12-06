package com.example.ashvi.studyhelper;

import android.graphics.Bitmap;

import com.google.firebase.auth.FirebaseUser;

public class User {

    public String name;
    public String id;
    public String major;
    public String hours;
    public String profilePicture;


    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String name,String id, String major, String hours, String profilePicture) {
        this.name = name;
        this.id = id;
        this.major = major;
        this.hours = hours;
        this.profilePicture = profilePicture;
    }
}