package com.example.hanoistudentgigs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hanoistudentgigs.R;

import com.example.hanoistudentgigs.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_HanoiStudentGigs_NoActionBar);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                // Nếu đã đăng nhập, chuyển thẳng vào MainActivity
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                // Nếu chưa, chuyển đến màn hình đăng nhập
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, 2000); // Hiển thị trong 2 giây
    }
}