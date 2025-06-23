package com.example.hanoistudentgigs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hanoistudentgigs.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_HanoiStudentGigs_NoActionBar);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // FIX: Luôn chuyển đến màn hình chọn vai trò sau khi màn hình chờ kết thúc.
            startActivity(new Intent(SplashActivity.this, RoleSelectionActivity.class));
            finish();
        }, 2000); // Hiển thị trong 2 giây
    }
}
