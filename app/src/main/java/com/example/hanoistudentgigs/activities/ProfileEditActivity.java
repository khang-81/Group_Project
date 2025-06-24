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
    private Button buttonSelectCv; // Khai báo buttonSelectCv
    private TextView textViewCvFileName; // Khai báo textViewCvFileName

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

        // Ánh xạ các views SINH VIÊN
        studentFieldsLayout = findViewById(R.id.studentFieldsLayout);
        editTextEditStudentFullName = findViewById(R.id.editTextEditStudentFullName);
        editTextEditSchool = findViewById(R.id.editTextEditSchool);
        editTextEditMajor = findViewById(R.id.editTextEditMajor);
        editTextEditYear = findViewById(R.id.editTextEditYear);
        editTextEditStudentPhone = findViewById(R.id.editTextEditStudentPhone);
        editTextEditSkills = findViewById(R.id.editTextEditSkills);
        editTextEditExperience = findViewById(R.id.editTextEditExperience);
        buttonSelectCv = findViewById(R.id.buttonSelectCv); // Ánh xạ buttonSelectCv
        textViewCvFileName = findViewById(R.id.textViewCvFileName); // Ánh xạ textViewCvFileName

        // Ánh xạ các views NHÀ TUYỂN DỤNG
        employerFieldsLayout = findViewById(R.id.employerFieldsLayout);
        editTextEditCompanyName = findViewById(R.id.editTextEditCompanyName);
        editTextEditAddress = findViewById(R.id.editTextEditAddress);
        editTextEditPhone = findViewById(R.id.editTextEditPhone);
        editTextEditWebsite = findViewById(R.id.editTextEditWebsite);

        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);

        loadCurrentProfile();

        buttonSaveChanges.setOnClickListener(v -> saveChanges());

        // Thiết lập OnClickListener cho buttonSelectCv
        buttonSelectCv.setOnClickListener(v -> selectCvLauncher.launch("application/pdf"));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadCurrentProfile() {
        if (userIdToEdit == null) return;

        // BƯỚC 1: Lấy vai trò từ USERS_COLLECTION
        db.collection(Constants.USERS_COLLECTION).document(userIdToEdit).get().addOnSuccessListener(userDoc -> {
            if (userDoc.exists()) {
                userRole = userDoc.getString("role");
                Log.d("ProfileEdit", "User Role: " + userRole);

                if (Constants.ROLE_STUDENT.equals(userRole)) {
                    studentFieldsLayout.setVisibility(View.VISIBLE);
                    employerFieldsLayout.setVisibility(View.GONE);

                    // BƯỚC 2 (CHO SINH VIÊN): Lấy dữ liệu chi tiết từ STUDENTS_COLLECTION
                    db.collection(Constants.STUDENTS_COLLECTION).document(userIdToEdit).get().addOnSuccessListener(studentDoc -> {
                        if (studentDoc.exists()) {
                            // Điền dữ liệu cho sinh viên
                            editTextEditStudentFullName.setText(studentDoc.getString("fullName"));
                            editTextEditSchool.setText(studentDoc.getString("schoolName"));
                            editTextEditMajor.setText(studentDoc.getString("major"));
                            editTextEditYear.setText(studentDoc.getString("year"));
                            editTextEditStudentPhone.setText(studentDoc.getString("phone"));
                            editTextEditSkills.setText(studentDoc.getString("skillsDescription"));
                            editTextEditExperience.setText(studentDoc.getString("experience"));

                            String cvFileName = studentDoc.getString("cvFileName");
                            if (cvFileName != null && !cvFileName.isEmpty()) {
                                textViewCvFileName.setText(cvFileName);
                            } else {
                                textViewCvFileName.setText("Chưa có tệp CV nào được chọn");
                            }
                        } else {
                            // Document sinh viên không tồn tại - có thể là hồ sơ trống hoặc lỗi
                            Toast.makeText(ProfileEditActivity.this, "Không tìm thấy hồ sơ sinh viên chi tiết.", Toast.LENGTH_SHORT).show();
                            Log.w("ProfileEdit", "Student document not found for userId: " + userIdToEdit);
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(ProfileEditActivity.this, "Lỗi tải hồ sơ sinh viên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ProfileEdit", "Error loading student profile: " + e.getMessage(), e);
                    });

                } else if (Constants.ROLE_EMPLOYER.equals(userRole)) {
                    studentFieldsLayout.setVisibility(View.GONE);
                    employerFieldsLayout.setVisibility(View.VISIBLE);

                    // BƯỚC 2 (CHO NHÀ TUYỂN DỤNG): Lấy dữ liệu chi tiết từ EMPLOYERS_COLLECTION
                    db.collection(Constants.EMPLOYERS_COLLECTION).document(userIdToEdit).get().addOnSuccessListener(employerDoc -> {
                        if (employerDoc.exists()) {
                            // Điền dữ liệu cho nhà tuyển dụng
                            editTextEditCompanyName.setText(employerDoc.getString("companyName"));
                            editTextEditAddress.setText(employerDoc.getString("address"));
                            editTextEditPhone.setText(employerDoc.getString("phone"));
                            editTextEditWebsite.setText(employerDoc.getString("website"));
                        } else {
                            Toast.makeText(ProfileEditActivity.this, "Không tìm thấy hồ sơ nhà tuyển dụng chi tiết.", Toast.LENGTH_SHORT).show();
                            Log.w("ProfileEdit", "Employer document not found for userId: " + userIdToEdit);
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(ProfileEditActivity.this, "Lỗi tải hồ sơ nhà tuyển dụng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ProfileEdit", "Error loading employer profile: " + e.getMessage(), e);
                    });

                } else {
                    // Xử lý trường hợp vai trò không xác định hoặc admin (nếu có)
                    Toast.makeText(ProfileEditActivity.this, "Vai trò người dùng không xác định.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Log.d("ProfileEdit", "User document (main) not found for userId: " + userIdToEdit);
                Toast.makeText(ProfileEditActivity.this, "Không tìm thấy thông tin người dùng cơ bản.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Log.e("ProfileEdit", "Error loading user basic info: " + e.getMessage(), e);
            Toast.makeText(ProfileEditActivity.this, "Lỗi khi tải thông tin người dùng cơ bản: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void saveChanges() {
        if (userIdToEdit == null || userRole == null) {
            Toast.makeText(this, "Không thể lưu. Thiếu thông tin người dùng hoặc vai trò.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Bước này có thể không cần nếu vai trò không thay đổi,
        // nhưng để an toàn, bạn có thể cập nhật vai trò lại vào USERS_COLLECTION nếu cần
        // Map<String, Object> userUpdates = new HashMap<>();
        // userUpdates.put("role", userRole);
        // db.collection(Constants.USERS_COLLECTION).document(userIdToEdit).update(userUpdates);


        if (Constants.ROLE_STUDENT.equals(userRole)) {
            Map<String, Object> studentUpdates = new HashMap<>();
            studentUpdates.put("fullName", editTextEditStudentFullName.getText().toString().trim());
            studentUpdates.put("schoolName", editTextEditSchool.getText().toString().trim());
            studentUpdates.put("major", editTextEditMajor.getText().toString().trim());
            studentUpdates.put("year", editTextEditYear.getText().toString().trim());
            studentUpdates.put("phone", editTextEditStudentPhone.getText().toString().trim());
            studentUpdates.put("skillsDescription", editTextEditSkills.getText().toString().trim());
            studentUpdates.put("experience", editTextEditExperience.getText().toString().trim());

            // TODO: Xử lý lưu CV fileUri lên Firebase Storage và lấy URL để lưu vào Firestore
            if (cvFileUri != null) {
                // Đây là nơi bạn sẽ upload cvFileUri lên Firebase Storage
                // Sau khi upload thành công, lấy URL và lưu vào profileUpdates
                // Ví dụ: uploadCvAndSaveUrl(cvFileUri, userIdToEdit, studentUpdates);
                // Tạm thời, nếu bạn đã có tên file trong document, giữ lại
                // Hoặc nếu bạn muốn lưu tên file mới khi chọn:
                // studentUpdates.put("cvFileName", getFileNameFromUri(cvFileUri));
                Toast.makeText(this, "Chức năng tải CV chưa được triển khai hoàn chỉnh!", Toast.LENGTH_LONG).show();
                Log.w("ProfileEdit", "CV file selected but upload logic is not implemented.");
            } else {
                // Nếu người dùng không chọn CV mới và trước đó có CV, có thể giữ lại tên cũ
                // Hoặc xóa nếu muốn tùy chọn
            }


            // Lưu dữ liệu vào STUDENTS_COLLECTION
            db.collection(Constants.STUDENTS_COLLECTION).document(userIdToEdit).update(studentUpdates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Cập nhật hồ sơ sinh viên thành công!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Cập nhật hồ sơ sinh viên thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        } else if (Constants.ROLE_EMPLOYER.equals(userRole)) {
            Map<String, Object> employerUpdates = new HashMap<>();
            employerUpdates.put("companyName", editTextEditCompanyName.getText().toString().trim());
            employerUpdates.put("address", editTextEditAddress.getText().toString().trim());
            employerUpdates.put("phone", editTextEditPhone.getText().toString().trim());
            employerUpdates.put("website", editTextEditWebsite.getText().toString().trim());

            // Lưu dữ liệu vào EMPLOYERS_COLLECTION
            db.collection(Constants.EMPLOYERS_COLLECTION).document(userIdToEdit).update(employerUpdates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Cập nhật hồ sơ nhà tuyển dụng thành công!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Cập nhật hồ sơ nhà tuyển dụng thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    // Helper method để lấy tên file từ Uri
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}