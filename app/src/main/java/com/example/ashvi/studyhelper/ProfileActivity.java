package com.example.ashvi.studyhelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import android.support.annotation.NonNull;
import android.widget.Toast;
import  com.google.firebase.auth.FirebaseUser;
import static java.lang.System.*;
import static java.lang.System.out;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextView Email;
    private TextView Uid;
    private TextView user_name;
    private Button logout;
    private Button preferences;
    private Button resources;

    private ImageView profilePicture;
    private Bitmap profilePic;
    private String profilePicString;

    private DatabaseReference mDatabase;
    private FirebaseDatabase database;
    private User u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Email = (TextView)findViewById(R.id.profileEmail);
        Uid = (TextView)findViewById(R.id.profileUid);
        mAuth = FirebaseAuth.getInstance();
        logout = (Button)findViewById(R.id.button_logout);
        preferences = (Button) findViewById(R.id.preferences);
        resources = (Button) findViewById(R.id.resource);
        user_name = (TextView)findViewById(R.id.user_name);
        user = mAuth.getCurrentUser();
        profilePicture = findViewById(R.id.profile_image);
        profilePic = profilePicture.getDrawingCache();



        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("User_details");
        final FirebaseUser reqd_User;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("User_details");

        Query nameQuery = ref.orderByChild("id").equalTo(user.getUid());
        nameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            //System.out.print("I am inside event");

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    u = singleSnapshot.getValue(User.class);

                    Toast.makeText(ProfileActivity.this, "Got Something I guess:"+ u.toString(),
                            Toast.LENGTH_SHORT).show();
                    String u_name = u.name;
                    String u_id = u.id;
                    String major = u.major;
                    profilePicString = u.profilePicture;
                    if (profilePicString != null){
                        byte[] decodedString = Base64.decode(profilePicString, Base64.DEFAULT);
                        profilePic = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        profilePicture.setImageBitmap(profilePic);
                    }
                    user_name.setText("Hello "+u_name+"Your major :"+major);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Cancelled sorry",
                        Toast.LENGTH_SHORT).show();
            }
        });



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v==logout){
                    if (user != null) {
                        mAuth.signOut();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                }
            }
        });


        if (user != null){
            String email = user.getEmail();
            String uid = user.getUid();
            Email.setText(email);
            Uid.setText(uid);




        }


        //Setting onClick listener for the preferences button

        preferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == preferences){
                    if (user != null) {
                        //Testing with LoginActivity Class
                        startActivity(new Intent(getApplicationContext(), StudentPreferences.class));
                    }
                }
            }
        });
        resources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == resources){
                    if (user != null) {
                        //Testing with LoginActivity Class
                        startActivity(new Intent(getApplicationContext(), Resources.class));
                    }
                }
            }
        });
    }
}