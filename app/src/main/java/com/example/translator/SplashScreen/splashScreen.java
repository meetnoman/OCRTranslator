package com.example.translator.SplashScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.translator.R;
import com.example.translator.TextAugmenter;

public class splashScreen extends AppCompatActivity {
    private static boolean splashLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         if (!splashLoaded) {
            setContentView(R.layout.activity_splash_screen);
            int secondsDelayed = 2;

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(splashScreen.this, TextAugmenter.class));
                    finish();
                }
            }, secondsDelayed * 500);

            splashLoaded = true;
        }
        else {
            Intent goToMainActivity = new Intent(this, TextAugmenter.class);
            goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(goToMainActivity);
            finish();
        }

    }
}
