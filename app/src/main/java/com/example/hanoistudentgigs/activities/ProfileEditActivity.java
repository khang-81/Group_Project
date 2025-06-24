package com.example.hanoistudentgigs.activities;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView; // Thêm import cho TextView
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.utils.Constants;
import java.util.HashMap;
import java.util.Map;

public class ProfileEditActivity extends AppCompatActivity {
    // Views cho Sinh viên
    private LinearLayout studentFieldsLayout;
    private TextInputEditText editTextEditStudentFullName, editTextEditSchool, editTextEditMajor, editTextEditSkills, editTextEditExperience, editTextEditYear, editTextEditStudentPhone;

    private Uri cvFileUri;

    // Views cho Nhà tuyển dụng
    private LinearLayout employerFieldsLayout;
    private TextInputEditText editTextEditCompanyName, editTextEditAddress, editTextEditPhone, editTextEditWebsite;

    private ActivityResultLauncher<String> selectCvLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    cvFileUri = uri; // Đảm bảo gán đúng
                    Log.d("CV_UPLOAD", "Selected CV URI: " + uri.toString()); // Log để kiểm tra
                    // Cập nhật UI để hiển thị tên file CV
                    textViewCvFileName.setText(getFileNameFromUri(uri));
                    // TODO: Bạn có thể cần xử lý tải file CV lên Firebase Storage ở đây
                } else {
                    cvFileUri = null;
                    Log.d("CV_UPLOAD", "No CV selected or selection cancelled.");
                    textViewCvFileName.setText("Chưa có tệp CV nào được chọn");
                }
            }
    );
    private Button buttonSaveChanges;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userRole;
    private String userIdToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Check if an admin is editing a user profile
        if (getIntent().hasExtra("USER_ID")) {
            userIdToEdit = getIntent().getStringExtra("USER_ID");
        } else if (mAuth.getCurrentUser() != null) {
            userIdToEdit = mAuth.getCurrentUser().getUid();
        } else {
            Toast.makeText(this, "Không tìm thấy người dùng.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        studentFieldsLayout = findViewById(R.id.studentFieldsLayout);
        editTextEditStudentFullName = findViewById(R.id.editTextEditStudentFullName);
        editTextEditSchool = findViewById(R.id.editTextEditSchool);
        editTextEditMajor = findViewById(R.id.editTextEditMajor);
//        editTextEditYear = findViewById(R.id.editTextEditYear);
//        editTextEditStudentPhone = findViewById(R.id.editTextEditStudentPhone);
        editTextEditSkills = findViewById(R.id.editTextEditSkills);
        editTextEditExperience = findViewById(R.id.editTextEditExperience);

        employerFieldsLayout = findViewById(R.id.employerFieldsLayout);
        editTextEditCompanyName = findViewById(R.id.editTextEditCompanyName);
        editTextEditAddress = findViewById(R.id.editTextEditAddress);
        editTextEditPhone = findViewById(R.id.editTextEditPhone);
        editTextEditWebsite = findViewById(R.id.editTextEditWebsite);

        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);

        loadCurrentProfile();

        buttonSaveChanges.setOnClickListener(v -> saveChanges());

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadCurrentProfile() {
        if (userIdToEdit == null) return;

        db.collection(Constants.USERS_COLLECTION).document(userIdToEdit).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                userRole = document.getString("role");
                if (Constants.ROLE_STUDENT.equals(userRole)) {
                    // Hiển thị form cho Sinh viên và điền dữ liệu
                    studentFieldsLayout.setVisibility(View.VISIBLE);
                    employerFieldsLayout.setVisibility(View.GONE);
                    editTextEditStudentFullName.setText(document.getString("fullName"));
                    editTextEditSchool.setText(document.getString("schoolName"));
                    editTextEditMajor.setText(document.getString("major"));
                    editTextEditYear.setText(document.getString("year"));
                    editTextEditStudentPhone.setText(document.getString("phone"));
                    editTextEditSkills.setText(document.getString("skillsDescription"));
                    editTextEditExperience.setText(document.getString("experience"));
                } else if (Constants.ROLE_EMPLOYER.equals(userRole)) {
                    // Hiển thị form cho Nhà tuyển dụng và điền dữ liệu
                    studentFieldsLayout.setVisibility(View.GONE);
                    employerFieldsLayout.setVisibility(View.VISIBLE);
                    editTextEditCompanyName.setText(document.getString("companyName"));
                    editTextEditAddress.setText(document.getString("address"));
                    editTextEditPhone.setText(document.getString("phone"));
                    editTextEditWebsite.setText(document.getString("website"));
                }
            }

        });
    }

    private void saveChanges() {
        if (userIdToEdit == null) return;

        Map<String, Object> updates = new HashMap<>();

        if (Constants.ROLE_STUDENT.equals(userRole)) {
            updates.put("fullName", editTextEditStudentFullName.getText().toString().trim());
            updates.put("schoolName", editTextEditSchool.getText().toString().trim());
            updates.put("major", editTextEditMajor.getText().toString().trim());
            updates.put("year", editTextEditYear.getText().toString().trim());
            updates.put("phone", editTextEditStudentPhone.getText().toString().trim());
            updates.put("skillsDescription", editTextEditSkills.getText().toString().trim());
            updates.put("experience", editTextEditExperience.getText().toString().trim());
        } else if (Constants.ROLE_EMPLOYER.equals(userRole)) {
            updates.put("companyName", editTextEditCompanyName.getText().toString().trim());
            updates.put("address", editTextEditAddress.getText().toString().trim());
            updates.put("phone", editTextEditPhone.getText().toString().trim());
            updates.put("website", editTextEditWebsite.getText().toString().trim());
        }

        db.collection(Constants.USERS_COLLECTION).document(userIdToEdit).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish(); // Đóng Activity sau khi lưu
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());

    }
}