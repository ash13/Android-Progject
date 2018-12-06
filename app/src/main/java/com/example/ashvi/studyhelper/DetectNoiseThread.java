package com.example.ashvi.studyhelper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DetectNoiseThread extends AppCompatActivity {
    /* constants */
    private static final int POLL_INTERVAL = 300;

    /** running state **/
    private boolean mRunning = false;

    /** config state **/
    private int mThreshold;

    int RECORD_AUDIO = 0;
    private PowerManager.WakeLock mWakeLock;
    private NotificationCompat.Builder notification_builder;
    private NotificationManagerCompat notification_manager;

    private Handler mHandler = new Handler();

    /* References to view elements */
    private TextView mStatusView,tv_noice;

    /* sound data source */
    private DetectNoise mSensor;
    private Button stopDetect;
    private Button startDetect;
    ProgressBar bar;
    /****************** Define runnable thread again and again detect noise *********/

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            //Log.i("Noise", "runnable mSleepTask");
//            start();
        }
    };

    // Create runnable thread to Monitor Voice
    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSensor.getAmplitude();
            //Log.i("Noise", "runnable mPollTask");
            updateDisplay("Monitoring Voice...", amp);

            if ((amp > mThreshold)) {
                callForHelp(amp);
                //Log.i("Noise", "==== onCreate ===");
            }
            // Runnable(mPollTask) will again execute after POLL_INTERVAL
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);
        }
    };
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Defined SoundLevelView in main.xml file
        setContentView(R.layout.activity_detect_noise);
        mStatusView =  findViewById(R.id.status);
        tv_noice = findViewById(R.id.tv_noice);
        bar = findViewById(R.id.progressBar1);
        startDetect = findViewById(R.id.startNoiseDetect);
        stopDetect = findViewById(R.id.stopNoiseDetect);
        // Used to record voice
        mSensor = new DetectNoise();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "NoiseAlert");
        // Start button
        startDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == startDetect){
                    start();

                }
            }
        });
        // Stop button
        stopDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == stopDetect){
                    stop();

                }
            }
        });
    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        //Log.i("Noise", "==== onResume ===");
//
//
//        if (!mRunning) {
//            mRunning = true;
//            start();
//        }
//    }
    @Override
    public void onStop() {
        super.onStop();
        //Stop noise monitoring
        stop();
    }
    private void start() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO);
        }
        initializeApplicationConstants();
        mSensor.start();
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
        //Noise monitoring start
        // Runnable(mPollTask) will execute after POLL_INTERVAL
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }
    private void stop() {
        Log.d("Noise", "==== Stop Noise Monitoring===");
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        mSensor.stop();
        bar.setProgress(0);
        updateDisplay("stopped...", 0.0);
        mRunning = false;
    }

    private void initializeApplicationConstants() {
        // Set Noise Threshold
        mThreshold = 14;

    }

    private void updateDisplay(String status, double signalEMA) {
        mStatusView.setText(status);
        bar.setProgress((int)signalEMA);
        Log.d("SOUND", String.valueOf(signalEMA));
        tv_noice.setText(signalEMA+"dB");
    }

    private void callForHelp(double signalEMA) {

        //stop();
        // Show alert when noise threshold crossed
        Toast.makeText(getApplicationContext(), "Place is too loud. Please move somewhere else to study.",
                Toast.LENGTH_LONG).show();
        Log.d("SOUND", String.valueOf(signalEMA));
        sendNotification();
        tv_noice.setText(signalEMA+"dB");
    }
    public void sendNotification(){
        Intent open_activity_intent = new Intent(this, DetectNoiseThread.class);
        PendingIntent pending_intent = PendingIntent
                .getActivity(this, 0, open_activity_intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager notification_manager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String chanel_id = "3000";
            CharSequence name = "Channel Name";
            String description = "Chanel Description";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(chanel_id, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.BLUE);
            notification_manager.createNotificationChannel(mChannel);
            notification_builder = new NotificationCompat.Builder(this, chanel_id);
        } else {
            notification_builder = new NotificationCompat.Builder(this);
        }
        notification_builder.setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Study Helper")
                .setContentText("Place is too loud. Please move somewhere else to study.")
                .setAutoCancel(true)
                .setContentIntent(pending_intent);
        notification_manager.notify(3000,notification_builder.build());
    }
}
