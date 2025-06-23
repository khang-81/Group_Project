package com.example.hanoistudentgigs.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.models.Job; // Sửa lại thành models
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp; // Import Timestamp của Firebase
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date; // Import Date để lấy thời gian hiện tại
import java.util.UUID;

public class PostJobActivity extends AppCompatActivity {
    private TextInputEditText editTextJobTitle, editTextCompanyName, editTextLocation, editTextSalary, editTextDescription;
    private Button buttonPostJob;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String editingJobId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Đăng tin tuyển dụng");
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        editTextJobTitle = findViewById(R.id.editTextJobTitle);
        editTextCompanyName = findViewById(R.id.editTextCompanyName);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextSalary = findViewById(R.id.editTextSalary);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonPostJob = findViewById(R.id.buttonPostJob);

        if (getIntent().hasExtra("EDIT_JOB_ID")) {
            editingJobId = getIntent().getStringExtra("EDIT_JOB_ID");
            setTitle("Sửa tin đăng");
            buttonPostJob.setText("Lưu thay đổi");
            loadJobDetails();
        } else {
            setTitle("Đăng tin mới");
        }

        buttonPostJob.setOnClickListener(v -> saveJob());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Đóng activity hiện tại để quay về màn hình trước
        return true;
    }

    private void loadJobDetails() {
        if (editingJobId == null) return;
        db.collection("jobs").document(editingJobId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Job job = documentSnapshot.toObject(Job.class);
                        if (job != null) {
                            editTextJobTitle.setText(job.getTitle());
                            editTextCompanyName.setText(job.getCompanyName());
                            editTextLocation.setText(job.getLocationName());
                            editTextSalary.setText(job.getSalaryDescription());
                            editTextDescription.setText(job.getDescription());
                        }
                    } else {
                        Toast.makeText(this, "Không tìm thấy tin đăng.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void saveJob() {
        String title = editTextJobTitle.getText().toString().trim();
        String company = editTextCompanyName.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String salary = editTextSalary.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(company) || TextUtils.isEmpty(location) || TextUtils.isEmpty(salary) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để thực hiện chức năng này.", Toast.LENGTH_SHORT).show();
            return;
        }
        String employerUid = mAuth.getCurrentUser().getUid();

        Job job = new Job();
        job.setTitle(title);
        job.setCompanyName(company);
        job.setLocationName(location);
        job.setSalaryDescription(salary);
        job.setDescription(description);
        job.setEmployerUid(employerUid);
        job.setApproved(false); // Mặc định tin đăng cần được Admin duyệt
        job.setStatus("Open"); // Mặc định trạng thái là Open

        // --- SỬA LỖI TẠI ĐÂY ---
        // Gán trực tiếp đối tượng Timestamp của Firebase thay vì Map
        job.setCreatedAt(new Timestamp(new Date()));
        // --- KẾT THÚC PHẦN SỬA LỖI ---

        if (editingJobId != null) {
            // Chế độ Sửa: Cập nhật job hiện có
            updateJobInFirestore(job);
        } else {
            // Chế độ Tạo mới: Thêm job mới
            addJobToFirestore(job);
        }
    }

    private void addJobToFirestore(Job job) {
        String jobId = UUID.randomUUID().toString();
        job.setId(jobId);
        db.collection("jobs").document(jobId).set(job)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(PostJobActivity.this, "Đăng tin thành công, chờ Admin duyệt.", Toast.LENGTH_LONG).show();
                    finish(); // Quay lại màn hình trước đó
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PostJobActivity.this, "Đăng tin thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateJobInFirestore(Job job) {
        // Khi sửa, không cần thay đổi ID và thời gian tạo
        // Chúng ta sẽ lấy lại job cũ để giữ lại các giá trị này
        // Tuy nhiên, để đơn giản, ví dụ này sẽ ghi đè.
        // Cách tốt hơn là dùng .update() thay vì .set()
        job.setId(editingJobId);
        db.collection("jobs").document(editingJobId)
                .set(job) // set sẽ ghi đè toàn bộ document
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(PostJobActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(PostJobActivity.this, "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
