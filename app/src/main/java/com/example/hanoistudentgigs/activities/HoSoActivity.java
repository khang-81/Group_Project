package com.example.hanoistudentgigs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hanoistudentgigs.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HoSoActivity extends AppCompatActivity {

    private TextView tvCompanyName, tvRole, tvPostedCount, tvApprovedCount, tvExpiredCount, tvDescription;
    private ImageView imgAvatar;
    private Button btnLogout, btnViewPosts;
    private BottomNavigationView bottomNav;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoso);

        // Ánh xạ view
        tvCompanyName = findViewById(R.id.tvCompanyName);
        tvRole = findViewById(R.id.tvRole);
        tvPostedCount = findViewById(R.id.tvPostedCount);
        tvApprovedCount = findViewById(R.id.tvApprovedCount);
        tvExpiredCount = findViewById(R.id.tvExpiredCount);
        tvDescription = findViewById(R.id.tvDescription);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnLogout = findViewById(R.id.btnLogout);
        btnViewPosts = findViewById(R.id.btnViewPosts);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserInfo();

        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(HoSoActivity.this, LoginActivity.class));
            finish();
        });

        btnViewPosts.setOnClickListener(v -> {
            startActivity(new Intent(HoSoActivity.this, QLTinActivity.class));
        });

        TextView btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(v -> {
            startActivity(new Intent(HoSoActivity.this, HoSoEditActivity.class));
        });

        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, TrangChuActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {

                return true;
            }

            return false;
        });

    }

    private void loadUserInfo() {
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (uid == null) return;

        db.collection("users").document(uid).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String name = snapshot.getString("companyName");
                String role = snapshot.getString("role");
                String avatarUrl = snapshot.getString("avatar");
                String description = snapshot.getString("description");

                tvCompanyName.setText(name != null ? name : "(Chưa có tên)");
                tvRole.setText(role != null ? role : "Nhà tuyển dụng");
                tvDescription.setText(description != null ? description : "(Chưa có mô tả)");

                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    Glide.with(this).load(avatarUrl).into(imgAvatar);
                }
            }
        });

        // Thống kê số tin
        db.collection("jobs")
                .whereEqualTo("employerUid", uid)
                .get()
                .addOnSuccessListener(query -> {
                    int total = query.size();
                    int approved = 0;
                    int expired = 0;

                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Boolean isApproved = doc.getBoolean("approved");
                        String status = doc.getString("status");

                        if (Boolean.TRUE.equals(isApproved)) approved++;
                        if ("Hết hạn".equalsIgnoreCase(status)) expired++;
                    }

                    tvPostedCount.setText(String.valueOf(total));
                    tvApprovedCount.setText(String.valueOf(approved));
                    tvExpiredCount.setText(String.valueOf(expired));
                });
    }
}
