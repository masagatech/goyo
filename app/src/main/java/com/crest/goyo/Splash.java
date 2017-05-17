package com.crest.goyo;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.crest.goyo.Utils.Preferences;
import com.crest.goyo.logger.Log;

public class Splash extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    private String mRideId, mType;
    private String TAG="SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        if (getIntent().getExtras() != null) {
            mRideId = getIntent().getExtras().getString("i_ride_id", "");
            mType = getIntent().getExtras().getString("type", "");
        }

        Log.e(TAG,"TAG Login Screen on create"+"fsf");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG,"TAG Login Screen in handler");

                if (!Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID).isEmpty()) {
                    Intent intent = new Intent(Splash.this, MainActivity.class);
                    intent.putExtra("i_ride_id", mRideId);
                    Log.e(TAG,"TAG Login directly"+mRideId);
                    startActivity(intent);
                    finish();
                }
                if (getIntent().getExtras() != null) {
                    if(mType.equals("user_ride_start")){
                        Intent intent = new Intent(Splash.this, StartRideActivity.class);
                        Log.e(TAG,"TAG mType"+mType);
                        intent.putExtra("i_ride_id", mRideId);
                        startActivity(intent);
                        finish();
                    }else if(mType.equals("user_ride_complete")){
                        Intent intent = new Intent(Splash.this, CompleteRide.class);
                        Log.e(TAG,"TAG mType"+mType);
                        intent.putExtra("i_ride_id", mRideId);
                        startActivity(intent);
                        finish();
                    }
                }
                if(Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID).isEmpty()) {
                    Intent intent = new Intent(Splash.this, Login.class);
                    Log.e(TAG,"TAG Login Screen"+"ddsa");
                    startActivity(intent);
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);
    }
}
