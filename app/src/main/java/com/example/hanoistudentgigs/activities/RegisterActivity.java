// File: activities/RegisterActivity.java (ĐÃ FIX)

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

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        int selectedRoleId = radioGroupRole.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            Toast.makeText(this, "Vui lòng chọn vai trò của bạn.", Toast.LENGTH_SHORT).show();
            return;
        }

        String role = selectedRoleId == R.id.radioStudent ? Constants.ROLE_STUDENT : Constants.ROLE_EMPLOYER;

        // KHÔNG CẦN THU THẬP additionalData VÀO MAP CHUNG NỮA
        // SẼ THU THẬP TRỰC TIẾP TRONG saveUserToFirestore DỰA TRÊN VAI TRÒ

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng điền Email và Mật khẩu.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra các trường bắt buộc theo vai trò
        if (role.equals(Constants.ROLE_STUDENT)) {
            String fullName = editTextStudentFullName.getText().toString().trim();
            String school = editTextSchool.getText().toString().trim();
            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(school)) {
                Toast.makeText(this, "Vui lòng điền Họ tên và Trường học.", Toast.LENGTH_SHORT).show();
                return;
            }
        } else { // EMPLOYER
            String companyName = editTextCompanyName.getText().toString().trim();
            String address = editTextAddress.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();
            if (TextUtils.isEmpty(companyName) || TextUtils.isEmpty(address) || TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "Vui lòng điền Tên công ty, Địa chỉ và SĐT.", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        progressBar.setVisibility(View.VISIBLE);
        buttonRegister.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Gọi hàm lưu thông tin user cơ bản và thông tin profile chi tiết
                            saveUserAndProfileToFirestore(firebaseUser.getUid(), email, role);
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        buttonRegister.setEnabled(true);
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Đổi tên hàm để phản ánh việc lưu cả user và profile
    private void saveUserAndProfileToFirestore(String userId, String email, String role) {
        // Bước 1: Lưu thông tin cơ bản vào collection 'users'
        Map<String, Object> userDocument = new HashMap<>();
        userDocument.put("uid", userId);
        userDocument.put("email", email);
        userDocument.put("role", role); // Chỉ lưu vai trò ở đây

        db.collection(Constants.USERS_COLLECTION).document(userId).set(userDocument)
                .addOnSuccessListener(aVoid -> {
                    // Bước 2: Lưu thông tin chi tiết vào collection 'students' hoặc 'employers'
                    if (role.equals(Constants.ROLE_STUDENT)) {
                        Map<String, Object> studentProfile = new HashMap<>();
                        studentProfile.put("fullName", editTextStudentFullName.getText().toString().trim());
                        studentProfile.put("schoolName", editTextSchool.getText().toString().trim()); // <-- Đã sửa key từ "school" sang "schoolName"
                        studentProfile.put("major", editTextMajor.getText().toString().trim());
                        studentProfile.put("role", Constants.ROLE_STUDENT); // Có thể thêm role vào đây để dễ truy vấn nếu cần
                        // Thêm các trường khác nếu có
                        studentProfile.put("experience", ""); // Khởi tạo rỗng
                        studentProfile.put("skillsDescription", ""); // Khởi tạo rỗng
                        studentProfile.put("cvFileName", null); // Khởi tạo rỗng

                        db.collection(Constants.STUDENTS_COLLECTION).document(userId).set(studentProfile)
                                .addOnSuccessListener(bVoid -> handleRegistrationSuccess())
                                .addOnFailureListener(e -> handleRegistrationFailure("Lỗi khi lưu hồ sơ sinh viên: " + e.getMessage()));
                    } else { // EMPLOYER
                        Map<String, Object> employerProfile = new HashMap<>();
                        employerProfile.put("companyName", editTextCompanyName.getText().toString().trim());
                        employerProfile.put("address", editTextAddress.getText().toString().trim());
                        employerProfile.put("phone", editTextPhone.getText().toString().trim());
                        employerProfile.put("website", editTextWebsite.getText().toString().trim());
                        employerProfile.put("fullName", editTextCompanyName.getText().toString().trim()); // Lưu tên công ty vào fullName cho đồng bộ
                        employerProfile.put("role", Constants.ROLE_EMPLOYER); // Có thể thêm role vào đây
                        // Thêm các trường khác nếu có

                        db.collection(Constants.EMPLOYERS_COLLECTION).document(userId).set(employerProfile)
                                .addOnSuccessListener(bVoid -> handleRegistrationSuccess())
                                .addOnFailureListener(e -> handleRegistrationFailure("Lỗi khi lưu hồ sơ nhà tuyển dụng: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> handleRegistrationFailure("Lỗi khi lưu thông tin người dùng cơ bản: " + e.getMessage()));
    }

    private void handleRegistrationSuccess() {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void handleRegistrationFailure(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        buttonRegister.setEnabled(true);
        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
        // Có thể thêm logic xóa user khỏi Auth nếu bước Firestore thất bại
        // Ví dụ: mAuth.getCurrentUser().delete();
    }
}