package com.visal.phraze.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.visal.phraze.R;

public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final ImageView splash = findViewById(R.id.splash_screen_logo);
        final ImageView background = findViewById(R.id.splash_screen_background);

        //animating the splashscreen
        background.setAlpha(0f);
        splash.setAlpha(0f);
        splash.setScaleX(5f);
        splash.setScaleY(5f);

        background.animate()
                .alpha(1f)
                .setDuration(1000);

        splash.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(1000);

        //starting activity on a delay
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            }
        }, 2000);
    }
}
