package com.example.hanoistudentgigs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.models.Job;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SuaTinActivity extends AppCompatActivity {
    private EditText etTitle, etLocation, etSalary, etDescription, etContact;
    private Spinner spnJobType, spnField;
    private Button btnSave;
    private FirebaseFirestore db;
    private String jobId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suatin);
        // Firebase
        db = FirebaseFirestore.getInstance();
        // Liên kết UI
        etTitle = findViewById(R.id.etTitle);
        etLocation = findViewById(R.id.etLocation);
        etSalary = findViewById(R.id.etSalary);
        etDescription = findViewById(R.id.etDescription);
        etContact = findViewById(R.id.etContact);
        spnJobType = findViewById(R.id.spnJobType);
        spnField = findViewById(R.id.spnField);
        btnSave = findViewById(R.id.btnUpdate);
        // Adapter spinner
        ArrayAdapter<CharSequence> jobTypeAdapter = ArrayAdapter.createFromResource(
                this, R.array.job_types, android.R.layout.simple_spinner_item);
        jobTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnJobType.setAdapter(jobTypeAdapter);
        ArrayAdapter<CharSequence> fieldAdapter = ArrayAdapter.createFromResource(
                this, R.array.fields, android.R.layout.simple_spinner_item);
        fieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnField.setAdapter(fieldAdapter);
        // Nhận jobId từ Intent
        jobId = getIntent().getStringExtra("JOB_ID");
        if (jobId != null) {
            loadJobData(jobId);
        } else {
            Toast.makeText(this, "Không tìm thấy ID công việc", Toast.LENGTH_SHORT).show();
            finish();
        }
        // Cập nhật
        btnSave.setOnClickListener(v -> {
            if (jobId != null) {
                updateJob(jobId);
            }
        });
    }
    // Tải dữ liệu công việc hiện tại
    private void loadJobData(String jobId) {
        db.collection("jobs").document(jobId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Job job = documentSnapshot.toObject(Job.class);
                    if (job != null) {
                        etTitle.setText(job.getTitle());
                        etLocation.setText(job.getLocationName());
                        etSalary.setText(job.getSalaryDescription());
                        etDescription.setText(job.getDescription());
                        etContact.setText(job.getContact());
                        setSpinnerSelection(spnJobType, job.getJobType());
                        setSpinnerSelection(spnField, job.getCategoryName());
                    }
                })
                .addOnFailureListener(e -> Log.e("SuaTin", "Lỗi tải dữ liệu", e));
    }
    // Cập nhật công việc
    private void updateJob(String jobId) {
        DocumentReference docRef = db.collection("jobs").document(jobId);
        docRef.update(
                "title", etTitle.getText().toString(),
                "locationName", etLocation.getText().toString(),
                "salaryDescription", etSalary.getText().toString(),
                "description", etDescription.getText().toString(),
                "contact", etContact.getText().toString(),
                "jobType", spnJobType.getSelectedItem().toString(),
                "categoryName", spnField.getSelectedItem().toString(),
                "employerUid", FirebaseAuth.getInstance().getCurrentUser().getUid()
        ).addOnSuccessListener(unused -> {
            Toast.makeText(this, "Đã cập nhật công việc", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SuaTinActivity.this, QLTinActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
            Log.e("SuaTin", "Update fail", e);
        });
    }
    // Tìm vị trí item spinner khớp giá trị và set selection
    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<?> adapter = (ArrayAdapter<?>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
