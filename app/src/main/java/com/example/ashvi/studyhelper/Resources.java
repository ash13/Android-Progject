package com.example.ashvi.studyhelper;

import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Resources extends AppCompatActivity {
    Button youtube;
    Button khanAcademy;
    Button wolfram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);

        getSupportActionBar().setTitle("Resources");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        youtube =  findViewById(R.id.youtube);
        khanAcademy = findViewById(R.id.khanAcademy);
        wolfram = findViewById(R.id.wolfram);

        youtube.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.youtube.com/"));
                startActivity(browserIntent);
            }
        });

        khanAcademy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.khanacademy.org/"));
                startActivity(browserIntent);
            }
        });

        wolfram.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.wolframalpha.com/"));
                startActivity(browserIntent);
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
