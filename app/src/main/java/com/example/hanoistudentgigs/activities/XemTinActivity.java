package com.example.hanoistudentgigs.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.models.Job;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class XemTinActivity extends AppCompatActivity {
    private TextView txtTitle, txtCompany, txtSalary, txtLocation, txtContact, txtDescription;
    private Button btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xemtin);

        txtTitle = findViewById(R.id.txtTitle);
        txtCompany = findViewById(R.id.txtCompany);
        txtSalary = findViewById(R.id.txtSalary);
        txtLocation = findViewById(R.id.txtLocation);
        txtContact = findViewById(R.id.txtContact);
        txtDescription = findViewById(R.id.txtDescription);
        btnBack = findViewById(R.id.btnBack);

        // Nhận ID của công việc
        String jobId = getIntent().getStringExtra("JOB_ID");
        if (jobId == null) {
            Toast.makeText(this, "Không tìm thấy thông tin công việc", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lấy dữ liệu từ Firestore theo ID
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference jobRef = db.collection("jobs").document(jobId);

        jobRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Job job = documentSnapshot.toObject(Job.class);
                if (job != null) {
                    txtTitle.setText(job.getTitle());
                    txtCompany.setText(job.getCompanyName());
                    txtSalary.setText(job.getSalaryDescription());
                    txtLocation.setText(job.getLocationName());
                    txtContact.setText(job.getContact());
                    txtDescription.setText(job.getDescription());
                }
            } else {
                Toast.makeText(this, "Không tìm thấy công việc", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
