package com.sadi.hydrobeacon;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.sadi.hydrobeacon.databinding.ActivitySplashBinding;

public class Splash extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        overridePendingTransition(0, 0);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        binding.imageView.startAnimation(fadeIn);
        binding.textView.startAnimation(fadeIn);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Animation fadeOut = AnimationUtils.loadAnimation(Splash.this, R.anim.fade_out);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    Intent intent = new Intent(Splash.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            binding.getRoot().startAnimation(fadeOut);
        }, 3000);
    }
}