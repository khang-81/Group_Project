package com.example.hanoistudentgigs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.bumptech.glide.Glide;
import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.models.Application;
import com.example.hanoistudentgigs.models.Job;
import com.example.hanoistudentgigs.utils.Constants;

public class JobDetailActivity extends AppCompatActivity {
    private TextView textViewDetailJobTitle, textViewDetailCompanyName, textViewDetailDescription, textViewDetailRequirements,
            textViewDetailSalary, textViewDetailLocation, textViewDetailJobType, textViewDetailCategory;
    private Button buttonApplyNow;
    private FirebaseFirestore db;
    private String jobId;
    private String employerUid;
    private FirebaseAuth mAuth;
    private ImageView imageViewCompanyLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize Views
        imageViewCompanyLogo = findViewById(R.id.imageViewDetailCompanyLogo);
        textViewDetailJobTitle = findViewById(R.id.textViewDetailJobTitle);
        textViewDetailCompanyName = findViewById(R.id.textViewDetailCompanyName);
        textViewDetailDescription = findViewById(R.id.textViewDetailDescription);
        textViewDetailRequirements = findViewById(R.id.textViewDetailRequirements);
        textViewDetailSalary = findViewById(R.id.textViewDetailSalary);
        textViewDetailLocation = findViewById(R.id.textViewDetailLocation);
        textViewDetailJobType = findViewById(R.id.textViewDetailJobType);
        textViewDetailCategory = findViewById(R.id.textViewDetailCategory);
        buttonApplyNow = findViewById(R.id.buttonApplyNow);

        // Nhận jobId từ Intent đã gửi từ JobAdapter
        jobId = getIntent().getStringExtra("JOB_ID");

        // Ẩn nút Nộp đơn nếu là admin
        boolean isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);
        if (isAdmin) {
            buttonApplyNow.setVisibility(Button.GONE);
        } else {
            buttonApplyNow.setOnClickListener(v -> applyForJob());
        }

        if (jobId != null) {
            loadJobDetails();
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin công việc.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadJobDetails() {
        db.collection("jobs").document(jobId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            jobId = document.getId();
                            employerUid = document.getString("employerUid");
                            textViewDetailJobTitle.setText(document.getString("title"));
                            textViewDetailCompanyName.setText(document.getString("companyName"));
                            textViewDetailDescription.setText(document.getString("description"));
                            textViewDetailRequirements.setText(document.getString("requirements"));
                            textViewDetailSalary.setText(document.getString("salaryDescription"));
                            textViewDetailLocation.setText(document.getString("locationName"));
                            textViewDetailJobType.setText(document.getString("jobType"));
                            textViewDetailCategory.setText(document.getString("categoryName"));
                            Glide.with(this).load(document.getString("companyLogoUrl")).placeholder(R.drawable.default_company_logo_placeholder).into(imageViewCompanyLogo);

                            // Check if student has already applied
                            checkIfApplied();
                        } else {
                            Toast.makeText(this, "Không tìm thấy dữ liệu.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Lỗi khi tải dữ liệu.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkIfApplied() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            buttonApplyNow.setVisibility(View.GONE);
            return;
        }
        String studentUid = currentUser.getUid();

        db.collection(Constants.USERS_COLLECTION).document(studentUid).get()
            .addOnSuccessListener(userDocument -> {
                if (userDocument.exists() && Constants.ROLE_STUDENT.equals(userDocument.getString("role"))) {
                    buttonApplyNow.setVisibility(View.VISIBLE);
                    // Now check if already applied
                    db.collection("applications")
                        .whereEqualTo("studentUid", studentUid)
                        .whereEqualTo("jobId", jobId)
                        .limit(1)
                        .get()
                        .addOnCompleteListener(applicationTask -> {
                            if (applicationTask.isSuccessful() && !applicationTask.getResult().isEmpty()) {
                                buttonApplyNow.setText("Đã ứng tuyển");
                                buttonApplyNow.setEnabled(false);
                            } else {
                                buttonApplyNow.setText("Nộp đơn ngay");
                                buttonApplyNow.setEnabled(true);
                            }
                        });
                } else {
                    // Not a student, or user data missing
                    buttonApplyNow.setVisibility(View.GONE);
                }
            });
    }

    private void applyForJob() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để ứng tuyển.", Toast.LENGTH_SHORT).show();
            return;
        }

        String studentUid = currentUser.getUid();

        // Get student info before creating an application
        db.collection(Constants.USERS_COLLECTION).document(studentUid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String studentName = documentSnapshot.getString("fullName");
                String cvUrl = documentSnapshot.getString("cvUrl"); // Assuming cvUrl is stored in user's profile

                Application application = new Application();
                application.setJobId(jobId);
                application.setStudentUid(studentUid);
                application.setStudentName(studentName);
                application.setCvUrl(cvUrl);
                application.setStatus("Pending");
                // The appliedDate will be set by @ServerTimestamp

                db.collection("applications").add(application)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(JobDetailActivity.this, "Nộp đơn thành công!", Toast.LENGTH_SHORT).show();
                        buttonApplyNow.setText("Đã ứng tuyển");
                        buttonApplyNow.setEnabled(false);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(JobDetailActivity.this, "Lỗi khi nộp đơn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin hồ sơ.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi khi tải hồ sơ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onBackPressed() {
        boolean isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);
        if (isAdmin) {
            Intent intent = new Intent(this, com.example.hanoistudentgigs.MainActivity.class);
            intent.putExtra("SELECT_ADMIN_APPROVE_TAB", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
