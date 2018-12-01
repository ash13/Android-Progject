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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.support.annotation.NonNull;



public class StudentPreferences extends AppCompatActivity {
    EditText student_name;
    EditText student_major;
    String stud_name;
    String stud_major;
    Button done;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    String uid;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentpreferences);
        student_name=(EditText)findViewById(R.id.student_name);
        student_major=(EditText)findViewById(R.id.major_name);
        done=(Button)findViewById(R.id.done_button);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        //Setting on Click on the Done button. Here the data entered should be sent to the database.

        //Getting current user
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        uid=currentUser.getUid();

        //Sending data on clicking the done button
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == done){
                    Toast.makeText(StudentPreferences.this, "Should be sending data here",
                            Toast.LENGTH_SHORT).show();
                    //SendData();
                    stud_name=student_name.getText().toString().trim();
                    stud_major=student_major.getText().toString().trim();
                    //Creating new entry to users with major and name
                    User new_user=new User(stud_name,uid,stud_major);
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
