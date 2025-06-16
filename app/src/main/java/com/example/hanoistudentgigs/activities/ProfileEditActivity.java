package com.example.hanoistudentgigs.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
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
    private TextInputEditText editTextEditStudentFullName, editTextEditSchool, editTextEditMajor, editTextEditSkills, editTextEditExperience;

    // Views cho Nhà tuyển dụng
    private LinearLayout employerFieldsLayout;
    private TextInputEditText editTextEditCompanyName, editTextEditAddress, editTextEditPhone, editTextEditWebsite;

    private Button buttonSaveChanges;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ các views
        studentFieldsLayout = findViewById(R.id.studentFieldsLayout);
        editTextEditStudentFullName = findViewById(R.id.editTextEditStudentFullName);
        editTextEditSchool = findViewById(R.id.editTextEditSchool);
        editTextEditMajor = findViewById(R.id.editTextEditMajor);
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

    private void loadCurrentProfile() {
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getCurrentUser().getUid();

        db.collection(Constants.USERS_COLLECTION).document(uid).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                userRole = document.getString("role");
                if (Constants.ROLE_STUDENT.equals(userRole)) {
                    // Hiển thị form cho Sinh viên và điền dữ liệu
                    studentFieldsLayout.setVisibility(View.VISIBLE);
                    employerFieldsLayout.setVisibility(View.GONE);
                    editTextEditStudentFullName.setText(document.getString("fullName"));
                    editTextEditSchool.setText(document.getString("schoolName"));
                    editTextEditMajor.setText(document.getString("major"));
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
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getCurrentUser().getUid();

        Map<String, Object> updates = new HashMap<>();

        if (Constants.ROLE_STUDENT.equals(userRole)) {
            updates.put("fullName", editTextEditStudentFullName.getText().toString().trim());
            updates.put("schoolName", editTextEditSchool.getText().toString().trim());
            updates.put("major", editTextEditMajor.getText().toString().trim());
            updates.put("skillsDescription", editTextEditSkills.getText().toString().trim());
            updates.put("experience", editTextEditExperience.getText().toString().trim());
        } else if (Constants.ROLE_EMPLOYER.equals(userRole)) {
            updates.put("companyName", editTextEditCompanyName.getText().toString().trim());
            updates.put("address", editTextEditAddress.getText().toString().trim());
            updates.put("phone", editTextEditPhone.getText().toString().trim());
            updates.put("website", editTextEditWebsite.getText().toString().trim());
        }

        db.collection(Constants.USERS_COLLECTION).document(uid).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Đóng Activity sau khi lưu
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
