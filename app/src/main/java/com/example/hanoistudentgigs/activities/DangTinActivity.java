package com.example.hanoistudentgigs.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.models.Job;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DangTinActivity extends AppCompatActivity {

    EditText etTitle, etLocation, etSkills, etExperience, etEducation,
            etSalary, etTime, etDescription, etDeadline, etContact;
    Spinner spnJobType, spnField;
    Button btnPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dangtin);

        // Ánh xạ view
        etTitle = findViewById(R.id.etTitle);
        etLocation = findViewById(R.id.etLocation);
        etSkills = findViewById(R.id.etSkills);
        etExperience = findViewById(R.id.etExperience);
        etEducation = findViewById(R.id.etEducation);
        etSalary = findViewById(R.id.etSalary);
        etTime = findViewById(R.id.etTime);
        etDescription = findViewById(R.id.etDescription);
        etDeadline = findViewById(R.id.etDeadline);
        etContact = findViewById(R.id.etContact);
        spnJobType = findViewById(R.id.spnJobType);
        spnField = findViewById(R.id.spnField);
        btnPost = findViewById(R.id.btnPost);

        // Gắn adapter cho Spinner - LOẠI HÌNH CÔNG VIỆC
        ArrayAdapter<CharSequence> jobTypeAdapter = ArrayAdapter.createFromResource(
                this, R.array.job_types, android.R.layout.simple_spinner_item
        );
        jobTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnJobType.setAdapter(jobTypeAdapter);

        // Gắn adapter cho Spinner - LĨNH VỰC
        ArrayAdapter<CharSequence> fieldAdapter = ArrayAdapter.createFromResource(
                this, R.array.fields, android.R.layout.simple_spinner_item
        );
        fieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnField.setAdapter(fieldAdapter);

        // Chọn ngày cho deadline
        etDeadline.setOnClickListener(v -> showDatePicker());

        // Xử lý nút đăng tin
        btnPost.setOnClickListener(v -> {
            if (validateFields()) {
                String jobId = "job_" + System.currentTimeMillis(); // hoặc UUID.randomUUID().toString()

                Job job = new Job();
                job.setId(jobId);
                job.setTitle(etTitle.getText().toString().trim());
                job.setCompanyName("Công ty TNHH ABC"); // Có thể thay bằng thông tin thực tế
                job.setCompanyLogoUrl("https://example.com/logo.png");
                job.setLocationName(etLocation.getText().toString().trim());
                job.setSalaryDescription(etSalary.getText().toString().trim());
                job.setDescription(etDescription.getText().toString().trim());
                job.setRequirements(etSkills.getText().toString().trim());
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
                    return;
                }
                job.setEmployerUid(user.getUid());; // TODO: thay bằng UID thực tế nếu có
                job.setJobType(spnJobType.getSelectedItem().toString());
                job.setCategoryName(spnField.getSelectedItem().toString());
                job.setApproved(false);
                job.setFeatured(false);
                job.setStatus("Đang tuyển");
                job.setActive(true);
                job.setPostedDate("2025-06-22"); // Hoặc dùng SimpleDateFormat để lấy ngày hiện tại
                job.setMinSalary(5000000); // Hoặc parse từ EditText nếu cần
                job.setSearchKeywords(Arrays.asList("nhân viên", "bán hàng", "part-time"));

                // Thêm createdAt là kiểu Map<String, Object> chứa Timestamp
                Map<String, Object> createdAt = new HashMap<>();
                createdAt.put("timestamp", Timestamp.now());
                job.setCreatedAt(createdAt);

                // Gửi lên Firestore
                FirebaseFirestore.getInstance()
                        .collection("jobs")
                        .document(jobId)
                        .set(job)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(DangTinActivity.this, "Đăng tin thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DangTinActivity.this, TrangChuActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish(); // Kết thúc activity sau khi đăng
                        })
                        .addOnFailureListener(e -> {
                            Log.e("DANG_TIN", "Lỗi khi đăng tin: ", e);
                            Toast.makeText(DangTinActivity.this, "Lỗi khi đăng tin", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        });
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                    etDeadline.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private boolean validateFields() {
        if (etTitle.getText().toString().trim().isEmpty()) {
            etTitle.setError("Bắt buộc");
            return false;
        }
        if (etLocation.getText().toString().trim().isEmpty()) {
            etLocation.setError("Bắt buộc");
            return false;
        }
        if (etSalary.getText().toString().trim().isEmpty()) {
            etSalary.setError("Bắt buộc");
            return false;
        }
        if (etContact.getText().toString().trim().isEmpty()) {
            etContact.setError("Bắt buộc");
            return false;
        }
        return true;
    }
}
