package com.crest.goyo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.crest.goyo.Utils.Preferences;

import io.fabric.sdk.android.Fabric;

public class Splash extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    private String mRideId, mType;
    private String TAG = "SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        if (getIntent().getExtras() != null) {
            mRideId = getIntent().getExtras().getString("i_ride_id", "");
            mType = getIntent().getExtras().getString("type", "");
        } else {

        }

        startService(new Intent(this,UpdateLocationService.class));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID).isEmpty()) {
                    Intent intent = new Intent(Splash.this, MainActivity.class);
                    intent.putExtra("i_ride_id", mRideId);
                    startActivity(intent);
                    finish();
                }
                if (getIntent().getExtras() != null) {
                    if (mType.equals("user_ride_start")) {
                        Intent intent = new Intent(Splash.this, StartRideActivity.class);
                        intent.putExtra("i_ride_id", mRideId);
                        startActivity(intent);
                        finish();
                    } else if (mType.equals("user_ride_complete")) {
                        Intent intent = new Intent(Splash.this, CompleteRide.class);
                        intent.putExtra("i_ride_id", mRideId);
                        startActivity(intent);
                        finish();
                    }
                }
                if (Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID).isEmpty()) {
                    Intent intent = new Intent(Splash.this, Login.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);
    }

}
