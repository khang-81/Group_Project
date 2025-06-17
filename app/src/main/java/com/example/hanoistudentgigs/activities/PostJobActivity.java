package com.example.hanoistudentgigs.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.models.Job;
import java.util.Date; // Vẫn cần nếu bạn muốn lấy thời gian hiện tại
import java.util.HashMap; // Cần để tạo Map
import java.util.Map; // Cần để tạo Map
import java.util.concurrent.TimeUnit; // Cần để chuyển đổi Date sang seconds/nanoseconds

public class PostJobActivity extends AppCompatActivity {
    private TextInputEditText editTextJobTitle, editTextCompanyName, editTextLocation, editTextSalary, editTextDescription;
    private Button buttonPostJob;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job); // Tạo file layout này với các EditText tương ứng

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        editTextJobTitle = findViewById(R.id.editTextJobTitle);
        editTextCompanyName = findViewById(R.id.editTextCompanyName);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextSalary = findViewById(R.id.editTextSalary);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonPostJob = findViewById(R.id.buttonPostJob);

        buttonPostJob.setOnClickListener(v -> postJob());
    }

    private void postJob() {
        String title = editTextJobTitle.getText().toString().trim();
        String company = editTextCompanyName.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String salary = editTextSalary.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(company) || TextUtils.isEmpty(location) || TextUtils.isEmpty(salary) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        String employerUid = mAuth.getCurrentUser().getUid();

        // Tạo một ID ngẫu nhiên cho job document
        String jobId = db.collection("jobs").document().getId();

        Job job = new Job();
        job.setId(jobId);
        job.setTitle(title);
        job.setCompanyName(company);
        job.setLocationName(location);
        job.setSalaryDescription(salary);
        job.setDescription(description);
        job.setEmployerUid(employerUid);
        job.setApproved(false); // Mặc định tin đăng cần được Admin duyệt

        // --- ĐÂY LÀ PHẦN THAY ĐỔI QUAN TRỌNG ĐỂ XỬ LÝ createdAt DƯỚI DẠNG MAP ---
        // Lấy thời gian hiện tại
        Date now = new Date();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime());
        int nanoseconds = (int) (TimeUnit.MILLISECONDS.toNanos(now.getTime() % 1000)); // Lấy phần nano giây còn lại từ milliseconds

        // Tạo Map cho createdAt
        Map<String, Object> createdAtMap = new HashMap<>();
        createdAtMap.put("_seconds", seconds); // Lưu ý tên trường _seconds
        createdAtMap.put("_nanoseconds", nanoseconds); // Lưu ý tên trường _nanoseconds

        job.setCreatedAt(createdAtMap); // Gán Map vào trường createdAt của Job
        // --- KẾT THÚC PHẦN THAY ĐỔI QUAN TRỌNG ---

        db.collection("jobs").document(jobId).set(job)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(PostJobActivity.this, "Đăng tin thành công, chờ Admin duyệt.", Toast.LENGTH_LONG).show();
                    finish(); // Quay lại màn hình trước đó
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PostJobActivity.this, "Đăng tin thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
