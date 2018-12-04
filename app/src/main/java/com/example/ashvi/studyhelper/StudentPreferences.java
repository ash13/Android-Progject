package com.example.ashvi.studyhelper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.support.annotation.NonNull;
import android.widget.Toolbar;

import java.io.ByteArrayOutputStream;


public class StudentPreferences extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;


    private String namePreference;
    private String majorPreference;
    private String hoursPreference;

    private EditText namePref;
    private EditText majorPref;
    private EditText hoursPref;


    private Button done;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private User u;
    private String uid;
    private DatabaseReference mDatabase;
    private Bitmap profilePicture;

    private LinearLayout parentLinearLayout;

    private Button cameraBtn;
    private ImageView profilePic;
    private String imageB64;
    private Bitmap profilePicBitmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        namePref = findViewById(R.id.namePref);
        majorPref = findViewById(R.id.majorPref);
        hoursPref = findViewById(R.id.hoursPref);
        done = findViewById(R.id.done_button);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        parentLinearLayout = (LinearLayout) findViewById(R.id.parent_linear_layout);
        cameraBtn = findViewById(R.id.cameraBtn);
        profilePic = findViewById(R.id.profilePicture);
        getSupportActionBar().setTitle("Preferences");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        profilePicture = profilePic.getDrawingCache();

        //Getting current user
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();


        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            CAMERA_REQUEST);
                } else{
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        }});

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("User_details");

        Query nameQuery = ref.orderByChild("id").equalTo(currentUser.getUid());
        nameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            //System.out.print("I am inside event");

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    u = singleSnapshot.getValue(User.class);
                    updateProfilePic();

                    String u_name = u.name;
                    String u_id = u.id;
                    String u_major = u.major;
                    String u_hours = u.hours;

                    namePreference = u_name;
                    majorPreference = u_major;
                    hoursPreference = u_hours;

                    namePref.setText(namePreference);
                    majorPref.setText(majorPreference);
                    hoursPref.setText(hoursPreference);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(StudentPreferences.this, "Cancelled sorry",
                        Toast.LENGTH_SHORT).show();
            }
        });

        //Sending data on clicking the done button
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == done){
                    Toast.makeText(StudentPreferences.this, "Should be sending data here",
                            Toast.LENGTH_SHORT).show();
                    //SendData();
                    namePreference = namePref.getText().toString().trim();
                    majorPreference = majorPref.getText().toString().trim();
                    hoursPreference = hoursPref.getText().toString().trim();
                    //Creating new entry to users with major and name
                    getImageData(profilePicBitmap);
                    User new_user = new User(namePreference, uid, majorPreference, hoursPreference, imageB64);
                    mDatabase.child("User_details").child(uid).setValue(new_user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                  @Override
                                  public void onSuccess(Void aVoid) {
                                      Toast.makeText(StudentPreferences.this, "Sent Data to db",
                                              Toast.LENGTH_SHORT).show();
                                  }
                              })

                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(StudentPreferences.this, "Data failed to be sent to db",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });






                }
            }
        });


    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public void onAddField(View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.field, null);
        // Add the new row before the add field button.
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);
    }

    public void onDelete(View v) {
        parentLinearLayout.removeView((View) v.getParent());
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            profilePicBitmap = (Bitmap) data.getExtras().get("data");
            profilePic.setImageBitmap(profilePicBitmap);
            profilePicture = profilePicBitmap;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }
    public void getImageData(Bitmap bmp) {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, bao); // bmp is bitmap from user image file
        bmp.recycle();
        byte[] byteArray = bao.toByteArray();
        imageB64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        //  store & retrieve this string to firebase
    }
    private void updateProfilePic (){
        imageB64 = u.profilePicture;
        if (imageB64 != null){
            byte[] decodedString = Base64.decode(imageB64, Base64.DEFAULT);
            profilePicBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profilePic.setImageBitmap(profilePicBitmap);
        }
    }
}
