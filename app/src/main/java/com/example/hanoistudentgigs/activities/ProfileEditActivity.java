    package com.example.hanoistudentgigs.activities;

    import android.net.Uri;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.LinearLayout;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.activity.result.ActivityResultLauncher;
    import androidx.activity.result.contract.ActivityResultContracts;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.annotation.Nullable; // Đảm bảo sử dụng androidx.annotation.Nullable
    import com.google.android.material.textfield.TextInputEditText;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.firestore.FirebaseFirestore;

    import com.google.firebase.storage.StorageReference;

    import com.example.hanoistudentgigs.R;
    import com.example.hanoistudentgigs.utils.Constants;
    import java.io.InputStream;
    import java.util.HashMap;
    import java.util.Map;
    import java.util.UUID;

    public class ProfileEditActivity extends AppCompatActivity {
        // Views cho Sinh viên
        private LinearLayout studentFieldsLayout;
        private TextInputEditText editTextEditStudentFullName, editTextEditSchool, editTextEditMajor, editTextEditSkills, editTextEditExperience;
        private Button buttonSelectCv;
        private TextView textViewCvFileName;

        private Uri cvFileUri;

        private String selectedFileExtension;

        // Views cho Nhà tuyển dụng
        private LinearLayout employerFieldsLayout;
        private TextInputEditText editTextEditCompanyName, editTextEditAddress, editTextEditPhone, editTextEditWebsite;

        private ActivityResultLauncher<String> selectCvLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        cvFileUri = uri;
                        String fileName = getFileName(uri);
                        selectedFileExtension = getFileExtension(fileName);
                        textViewCvFileName.setText("Đã chọn: " + fileName); // Hiển thị tên tệp vừa chọn
                        currentCvFileName = fileName; // CẬP NHẬT TÊN FILE KHI CHỌN MỚI
                        Log.d("CV_UPLOAD", "Selected CV URI: " + uri.toString() + ", Extension: " + selectedFileExtension);
                        Toast.makeText(this, "Đã chọn tệp CV.", Toast.LENGTH_SHORT).show();
                    } else {
                        cvFileUri = null;
                        selectedFileExtension = null;
                        textViewCvFileName.setText("Chưa có tệp CV nào được chọn"); // Đặt lại nếu hủy chọn
                        currentCvFileName = null; // ĐẶT LẠI TÊN FILE NẾU HỦY CHỌN
                        Log.d("CV_UPLOAD", "Không có CV nào được chọn hoặc hủy bỏ.");
                        Toast.makeText(this, "Không có tệp CV nào được chọn.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        private Button buttonSaveChanges;
        private FirebaseFirestore db;
        private FirebaseAuth mAuth;

        private String userRole;

        private String currentCvFileName;
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
            buttonSelectCv = findViewById(R.id.buttonSelectCv);
            textViewCvFileName = findViewById(R.id.textViewCvFileName);


            employerFieldsLayout = findViewById(R.id.employerFieldsLayout);
            editTextEditCompanyName = findViewById(R.id.editTextEditCompanyName);
            editTextEditAddress = findViewById(R.id.editTextEditAddress);
            editTextEditPhone = findViewById(R.id.editTextEditPhone);
            editTextEditWebsite = findViewById(R.id.editTextEditWebsite);

            buttonSaveChanges = findViewById(R.id.buttonSaveChanges);

            loadCurrentProfile();

            buttonSelectCv.setOnClickListener(v -> selectCvLauncher.launch("*/*"));

            buttonSaveChanges.setOnClickListener(v -> saveChanges());
        }

        // Trong ProfileEditActivity.java
// ...




        private void loadCurrentProfile() {
            if (mAuth.getCurrentUser() == null) return;
            String uid = mAuth.getCurrentUser().getUid();

            db.collection(Constants.USERS_COLLECTION).document(uid).get().addOnSuccessListener(document -> {
                if (document.exists()) {
                    userRole = document.getString("role");
                    if (Constants.ROLE_STUDENT.equals(userRole)) {
                        studentFieldsLayout.setVisibility(View.VISIBLE);
                        employerFieldsLayout.setVisibility(View.GONE);
                        editTextEditStudentFullName.setText(document.getString("fullName"));
                        editTextEditSchool.setText(document.getString("schoolName"));
                        editTextEditMajor.setText(document.getString("major"));
                        editTextEditSkills.setText(document.getString("skillsDescription"));
                        editTextEditExperience.setText(document.getString("experience"));

                        // THÊM LẠI LOGIC NÀY để đọc tên file CV từ Firestore
                        currentCvFileName = document.getString("cvFileName"); // Đọc tên file CV từ Firestore
                        if (currentCvFileName != null && !currentCvFileName.isEmpty()) {
                            textViewCvFileName.setText("CV hiện tại: " + currentCvFileName);
                        } else {
                            textViewCvFileName.setText("Chưa có tệp CV nào được chọn.");
                        }
                    } else if (Constants.ROLE_EMPLOYER.equals(userRole)) {
                        employerFieldsLayout.setVisibility(View.GONE); // Sửa lỗi ẩn đi mất của bạn ở đoạn này.
                        employerFieldsLayout.setVisibility(View.VISIBLE); // Sửa lỗi hiển thị
                        editTextEditCompanyName.setText(document.getString("companyName"));
                        editTextEditAddress.setText(document.getString("address"));
                        editTextEditPhone.setText(document.getString("phone"));
                        editTextEditWebsite.setText(document.getString("website"));
                    }
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Lỗi tải hồ sơ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ProfileEditActivity", "Error loading profile", e);
            });
        }

        private void saveChanges() {
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(this, "Người dùng chưa đăng nhập.", Toast.LENGTH_SHORT).show();
                return;
            }
            String uid = mAuth.getCurrentUser().getUid();

            // KHÔNG CẦN GỌI uploadCvFile() ở đây nữa
            // cvFileUri sẽ chỉ được sử dụng cho mục đích cục bộ hoặc gửi đi (email/chia sẻ)

            // Chỉ cần cập nhật Firestore với các thông tin khác
            updateFirestoreProfile(uid); // Bỏ tham số cvUrl
        }


        private void updateFirestoreProfile(String uid) {
            Map<String, Object> updates = new HashMap<>();

            if (Constants.ROLE_STUDENT.equals(userRole)) {
                updates.put("fullName", editTextEditStudentFullName.getText().toString().trim());
                updates.put("schoolName", editTextEditSchool.getText().toString().trim());
                updates.put("major", editTextEditMajor.getText().toString().trim());
                updates.put("skillsDescription", editTextEditSkills.getText().toString().trim());
                updates.put("experience", editTextEditExperience.getText().toString().trim());

                // THÊM DÒNG NÀY ĐỂ LƯU TÊN FILE CV VÀO FIRESTORE
                updates.put("cvFileName", currentCvFileName); // <--- THÊM DÒNG NÀY

            } else if (Constants.ROLE_EMPLOYER.equals(userRole)) {
                updates.put("companyName", editTextEditCompanyName.getText().toString().trim());
                updates.put("address", editTextEditAddress.getText().toString().trim());
                updates.put("phone", editTextEditPhone.getText().toString().trim());
                updates.put("website", editTextEditWebsite.getText().toString().trim());
            }

            db.collection(Constants.USERS_COLLECTION).document(uid).update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ProfileEditActivity", "Error updating Firestore profile", e);
                    });
        }

        /**
         * Phương thức trợ giúp để đặt lại trạng thái lựa chọn CV và cập nhật UI.
         */


        private String getFileName(Uri uri) {
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

        private String getFileExtension(String fileName) {
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
                return fileName.substring(dotIndex + 1);
            }
            return "";
        }


    }
