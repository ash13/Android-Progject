package com.example.ashvi.studyhelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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



public class StudentPreferences extends AppCompatActivity {


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        namePref = findViewById(R.id.namePref);
        majorPref = findViewById(R.id.majorPref);
        hoursPref = findViewById(R.id.hoursPref);
        done = findViewById(R.id.done_button);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        getSupportActionBar().setTitle("Preferences");
        //Setting on Click on the Done button. Here the data entered should be sent to the database.

        //Getting current user
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("User_details");

        Query nameQuery = ref.orderByChild("id").equalTo(currentUser.getUid());
        nameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            //System.out.print("I am inside event");

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    u = singleSnapshot.getValue(User.class);

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
                    User new_user = new User(namePreference, uid, majorPreference, hoursPreference);
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
}
