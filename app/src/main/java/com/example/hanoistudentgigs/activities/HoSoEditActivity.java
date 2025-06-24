package com.example.hanoistudentgigs.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hanoistudentgigs.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class HoSoEditActivity extends AppCompatActivity {

    private EditText edtCompanyName, edtAddress, edtWebsite, edtPhone;
    private Button btnSave;
    private ImageView btnBack;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hosoedit);

        edtCompanyName = findViewById(R.id.edtCompanyName);
        edtAddress = findViewById(R.id.edtAddress);
        edtWebsite = findViewById(R.id.edtWebsite);
        edtPhone = findViewById(R.id.edtPhone);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserInfo();

        btnSave.setOnClickListener(v -> saveChanges());

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadUserInfo() {
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (uid == null) return;

        db.collection("users").document(uid).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                edtCompanyName.setText(snapshot.getString("companyName"));
                edtAddress.setText(snapshot.getString("address"));
                edtWebsite.setText(snapshot.getString("website"));
                edtPhone.setText(snapshot.getString("phone"));
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show());
    }

    private void saveChanges() {
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (uid == null) return;

        String companyName = edtCompanyName.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String website = edtWebsite.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        Map<String, Object> updates = new HashMap<>();
        updates.put("companyName", companyName);
        updates.put("address", address);
        updates.put("website", website);
        updates.put("phone", phone);

        db.collection("users").document(uid).update(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show());
    }
}
