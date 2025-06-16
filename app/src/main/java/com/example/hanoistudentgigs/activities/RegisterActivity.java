// File: activities/RegisterActivity.java (ĐÃ FIX THEO YÊU CẦU)

package com.example.hanoistudentgigs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.utils.Constants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    // Khai báo tất cả các View từ layout mới
    private TextInputEditText editTextEmail, editTextPassword;
    private TextInputEditText editTextStudentFullName, editTextSchool, editTextMajor;
    private TextInputEditText editTextCompanyName, editTextAddress, editTextPhone, editTextWebsite;
    private LinearLayout studentFieldsLayout, employerFieldsLayout;
    private RadioGroup radioGroupRole;
    private Button buttonRegister;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Ánh xạ tất cả các view
        editTextEmail = findViewById(R.id.editTextEmailRegister);
        editTextPassword = findViewById(R.id.editTextPasswordRegister);
        radioGroupRole = findViewById(R.id.radioGroupRole);
        buttonRegister = findViewById(R.id.buttonRegister);
        progressBar = findViewById(R.id.progressBarRegister);
        studentFieldsLayout = findViewById(R.id.studentFieldsLayout);
        editTextStudentFullName = findViewById(R.id.editTextStudentFullName);
        editTextSchool = findViewById(R.id.editTextSchool);
        editTextMajor = findViewById(R.id.editTextMajor);
        employerFieldsLayout = findViewById(R.id.employerFieldsLayout);
        editTextCompanyName = findViewById(R.id.editTextCompanyName);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextWebsite = findViewById(R.id.editTextWebsite);

        // Logic để hiển thị các trường phù hợp với vai trò
        radioGroupRole.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioStudent) {
                studentFieldsLayout.setVisibility(View.VISIBLE);
                employerFieldsLayout.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioEmployer) {
                studentFieldsLayout.setVisibility(View.GONE);
                employerFieldsLayout.setVisibility(View.VISIBLE);
            }
        });

        buttonRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        // Lấy dữ liệu từ các trường chung
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        int selectedRoleId = radioGroupRole.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            Toast.makeText(this, "Vui lòng chọn vai trò của bạn.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Xác định vai trò và thu thập dữ liệu tương ứng
        String role = selectedRoleId == R.id.radioStudent ? Constants.ROLE_STUDENT : Constants.ROLE_EMPLOYER;
        Map<String, String> userData = new HashMap<>();

        if (role.equals(Constants.ROLE_STUDENT)) {
            String fullName = editTextStudentFullName.getText().toString().trim();
            String school = editTextSchool.getText().toString().trim();
            String major = editTextMajor.getText().toString().trim();

            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(school)) {
                Toast.makeText(this, "Vui lòng điền Họ tên và Trường học.", Toast.LENGTH_SHORT).show();
                return;
            }
            userData.put("fullName", fullName);
            userData.put("school", school);
            userData.put("major", major);
        } else { // EMPLOYER
            String companyName = editTextCompanyName.getText().toString().trim();
            String address = editTextAddress.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();
            String website = editTextWebsite.getText().toString().trim();

            if (TextUtils.isEmpty(companyName) || TextUtils.isEmpty(address) || TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "Vui lòng điền Tên, Địa chỉ và SĐT.", Toast.LENGTH_SHORT).show();
                return;
            }
            // Thêm tên của người đại diện (nếu có)
            userData.put("fullName", companyName);
            userData.put("companyName", companyName);
            userData.put("address", address);
            userData.put("phone", phone);
            userData.put("website", website);
        }

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng điền Email và Mật khẩu.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        buttonRegister.setEnabled(false);

        // Tiến hành tạo tài khoản trên Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            saveUserToFirestore(firebaseUser.getUid(), email, role, userData);
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        buttonRegister.setEnabled(true);
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToFirestore(String userId, String email, String role, Map<String, String> additionalData) {
        Map<String, Object> userDocument = new HashMap<>();
        userDocument.put("uid", userId);
        userDocument.put("email", email);
        userDocument.put("role", role);
        userDocument.putAll(additionalData);

        db.collection(Constants.USERS_COLLECTION).document(userId).set(userDocument)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();

                    // --- FIX: Chuyển về màn hình LoginActivity thay vì MainActivity ---
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    // Xóa các activity trên cùng và đưa LoginActivity lên đầu, tránh việc người dùng nhấn back quay lại form đăng ký
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Đóng RegisterActivity
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    buttonRegister.setEnabled(true);
                    Toast.makeText(RegisterActivity.this, "Lỗi khi lưu thông tin: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
