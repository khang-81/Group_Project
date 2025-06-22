    package com.example.hanoistudentgigs.activities;

    import android.net.Uri;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.LinearLayout;
    import android.widget.TextView;
    import android.widget.Toast;
    import com.google.firebase.firestore.SetOptions;

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

            // BƯỚC 1: Đọc VAI TRÒ từ collection "users"
            db.collection(Constants.USERS_COLLECTION).document(uid).get().addOnSuccessListener(userDoc -> {
                if (userDoc.exists()) {
                    userRole = userDoc.getString("role"); // Lấy vai trò TỪ USERS_COLLECTION

                    // Xử lý cho Sinh viên
                    if (Constants.ROLE_STUDENT.equals(userRole)) {
                        studentFieldsLayout.setVisibility(View.VISIBLE);
                        employerFieldsLayout.setVisibility(View.GONE);

                        // BƯỚC 2: Nếu là sinh viên, ĐỌC PROFILE CHI TIẾT TỪ STUDENTS_COLLECTION
                        // Đây là một TRUY VẤN FIRESTORE MỚI
                        db.collection(Constants.STUDENTS_COLLECTION).document(uid).get().addOnSuccessListener(studentProfileDoc -> {
                            if (studentProfileDoc.exists()) {
                                editTextEditStudentFullName.setText(studentProfileDoc.getString("fullName"));
                                editTextEditSchool.setText(studentProfileDoc.getString("schoolName"));
                                editTextEditMajor.setText(studentProfileDoc.getString("major"));
                                editTextEditSkills.setText(studentProfileDoc.getString("skillsDescription"));
                                editTextEditExperience.setText(studentProfileDoc.getString("experience"));
                                currentCvFileName = studentProfileDoc.getString("cvFileName");
                                if (currentCvFileName != null && !currentCvFileName.isEmpty()) {
                                    textViewCvFileName.setText("CV hiện tại: " + currentCvFileName);
                                } else {
                                    textViewCvFileName.setText("Chưa có tệp CV nào được chọn.");
                                }
                            } else {
                                Toast.makeText(this, "Chưa có hồ sơ sinh viên, vui lòng điền thông tin.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, "Lỗi tải hồ sơ sinh viên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("ProfileEditActivity", "Error loading student profile", e);
                        });

                    }
                    // Xử lý cho Nhà tuyển dụng
                    else if (Constants.ROLE_EMPLOYER.equals(userRole)) {
                        employerFieldsLayout.setVisibility(View.VISIBLE); // Hiển thị layout nhà tuyển dụng
                        studentFieldsLayout.setVisibility(View.GONE); // Ẩn layout sinh viên

                        // Tải dữ liệu cho nhà tuyển dụng từ Constants.EMPLOYERS_COLLECTION
                        db.collection(Constants.EMPLOYERS_COLLECTION).document(uid).get().addOnSuccessListener(employerProfileDoc -> {
                            if (employerProfileDoc.exists()) {
                                editTextEditCompanyName.setText(employerProfileDoc.getString("companyName"));
                                editTextEditAddress.setText(employerProfileDoc.getString("address"));
                                editTextEditPhone.setText(employerProfileDoc.getString("phone"));
                                editTextEditWebsite.setText(employerProfileDoc.getString("website"));
                            } else {
                                Toast.makeText(this, "Chưa có hồ sơ nhà tuyển dụng, vui lòng điền thông tin.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, "Lỗi tải hồ sơ nhà tuyển dụng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("ProfileEditActivity", "Error loading employer profile", e);
                        });
                    } else {
                        Toast.makeText(this, "Không tìm thấy vai trò người dùng hợp lệ.", Toast.LENGTH_SHORT).show();
                        Log.e("ProfileEditActivity", "Invalid user role: " + userRole);
                    }
                } else {
                    Toast.makeText(this, "Không tìm thấy thông tin người dùng cơ bản (UID: " + uid + ").", Toast.LENGTH_SHORT).show();
                    Log.e("ProfileEditActivity", "User basic info not found for UID: " + uid);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Lỗi tải thông tin người dùng cơ bản: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ProfileEditActivity", "Error loading basic user info", e);
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
                // Đổ dữ liệu từ EditText vào Map
                updates.put("fullName", editTextEditStudentFullName.getText().toString().trim());
                updates.put("schoolName", editTextEditSchool.getText().toString().trim());
                updates.put("major", editTextEditMajor.getText().toString().trim());
                updates.put("skillsDescription", editTextEditSkills.getText().toString().trim());
                updates.put("experience", editTextEditExperience.getText().toString().trim());
                updates.put("cvFileName", currentCvFileName);

                // LƯU VÀO STUDENTS_COLLECTION
                db.collection(Constants.STUDENTS_COLLECTION).document(uid).set(updates, SetOptions.merge()) // Đã có import SetOptions
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("ProfileEditActivity", "Error updating Firestore student profile", e); // Log rõ hơn
                        });
            } else if (Constants.ROLE_EMPLOYER.equals(userRole)) {
                // Đổ dữ liệu từ EditText vào Map
                updates.put("companyName", editTextEditCompanyName.getText().toString().trim());
                updates.put("address", editTextEditAddress.getText().toString().trim());
                updates.put("phone", editTextEditPhone.getText().toString().trim());
                updates.put("website", editTextEditWebsite.getText().toString().trim());

                // LƯU VÀO EMPLOYERS_COLLECTION
                db.collection(Constants.EMPLOYERS_COLLECTION).document(uid).set(updates, SetOptions.merge()) // Đã có import SetOptions
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Cập nhật hồ sơ nhà tuyển dụng thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Cập nhật hồ sơ nhà tuyển dụng thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("ProfileEditActivity", "Error updating Firestore employer profile", e); // Log rõ hơn
                        });
            } else {
                Toast.makeText(this, "Không xác định được vai trò để cập nhật.", Toast.LENGTH_SHORT).show();
            }
        }

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
