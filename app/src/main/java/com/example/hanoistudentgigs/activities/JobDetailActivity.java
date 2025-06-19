package com.example.hanoistudentgigs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.models.Application;
import com.example.hanoistudentgigs.utils.Constants;

public class JobDetailActivity extends AppCompatActivity {
    private TextView textViewDetailJobTitle, textViewDetailCompanyName, textViewDetailDescription, textViewDetailRequirements;
    private Button buttonApplyNow;
    private FirebaseFirestore db;
    private String jobId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        textViewDetailJobTitle = findViewById(R.id.textViewDetailJobTitle);
        textViewDetailCompanyName = findViewById(R.id.textViewDetailCompanyName);
        textViewDetailDescription = findViewById(R.id.textViewDetailDescription);
        textViewDetailRequirements = findViewById(R.id.textViewDetailRequirements);
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

    private void loadJobDetails() {
        db.collection("jobs").document(jobId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // Ánh xạ dữ liệu từ Firestore vào các TextView
                            textViewDetailJobTitle.setText(document.getString("title"));
                            textViewDetailCompanyName.setText(document.getString("companyName"));
                            textViewDetailDescription.setText(document.getString("description"));
                            textViewDetailRequirements.setText(document.getString("requirements"));
                        } else {
                            Toast.makeText(this, "Không tìm thấy dữ liệu.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Lỗi khi tải dữ liệu.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void applyForJob() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để ứng tuyển.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy thông tin người dùng hiện tại để lưu vào đơn ứng tuyển
        db.collection(Constants.USERS_COLLECTION).document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String studentName = documentSnapshot.getString("fullName");
                        String cvUrl = documentSnapshot.getString("cvUrl");

                        if (cvUrl == null || cvUrl.isEmpty()) {
                            Toast.makeText(this, "Vui lòng tải lên CV trước khi ứng tuyển.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Tạo một đối tượng Application
                        String applicationId = db.collection("jobs").document(jobId)
                                .collection(Constants.APPLICATIONS_COLLECTION).document().getId();
                        Application application = new Application();
                        application.setId(applicationId);
                        application.setJobId(jobId);
                        application.setStudentUid(currentUser.getUid());
                        application.setStudentName(studentName);
                        application.setCvUrl(cvUrl);
                        application.setStatus("Submitted"); // Trạng thái ban đầu

                        // Lưu đơn ứng tuyển vào sub-collection của job tương ứng
                        db.collection(Constants.JOBS_COLLECTION).document(jobId)
                                .collection(Constants.APPLICATIONS_COLLECTION).document(applicationId)
                                .set(application)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(JobDetailActivity.this, "Ứng tuyển thành công!", Toast.LENGTH_SHORT).show();
                                    buttonApplyNow.setEnabled(false);
                                    buttonApplyNow.setText("Đã ứng tuyển");
                                })
                                .addOnFailureListener(e -> Toast.makeText(JobDetailActivity.this, "Ứng tuyển thất bại.", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi lấy thông tin người dùng.", Toast.LENGTH_SHORT).show());
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
