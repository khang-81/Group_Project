package com.example.hanoistudentgigs.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.models.User;
import com.example.hanoistudentgigs.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEditActivity extends AppCompatActivity {
    private static final String TAG = "ProfileEditActivity";

    // Views chung
    private CircleImageView imageViewProfile;
    private Button buttonSaveChanges;
    private Uri imageUri, cvFileUri;

    // Views cho Student
    private LinearLayout studentFieldsContainer;
    private TextInputEditText editTextStudentFullName, editTextSchool, editTextMajor, editTextYear, editTextSkills, editTextExperience;
    private Button buttonUploadCv;
    private TextView textViewCvFileName;

    // Views cho Employer
    private LinearLayout employerFieldsContainer;
    private TextInputEditText editTextCompanyName, editTextAddress, editTextPhone, editTextWebsite;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private String currentUserRole;
    private User currentUserData;

    // --- ActivityResultLaunchers để chọn ảnh và tệp ---
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    imageUri = result.getData().getData();
                    imageViewProfile.setImageURI(imageUri);
                }
            });

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    cvFileUri = result.getData().getData();
                    textViewCvFileName.setText(getFileNameFromUri(cvFileUri));
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Ánh xạ Views
        initViews();

        // Lấy dữ liệu người dùng và thiết lập giao diện
        loadUserData();

        // Thiết lập sự kiện click
        imageViewProfile.setOnClickListener(v -> openImagePicker());
        buttonUploadCv.setOnClickListener(v -> openFilePicker());
        buttonSaveChanges.setOnClickListener(v -> saveAllChanges());
    }

    private void initViews() {
        // Views chung
        imageViewProfile = findViewById(R.id.imageViewProfileEdit);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);

        // Containers
        studentFieldsContainer = findViewById(R.id.studentFieldsContainer);
        employerFieldsContainer = findViewById(R.id.employerFieldsContainer);

        // Student Views
        editTextStudentFullName = findViewById(R.id.editTextEditStudentFullName);
        editTextSchool = findViewById(R.id.editTextEditSchool);
        editTextMajor = findViewById(R.id.editTextEditMajor);
        editTextYear = findViewById(R.id.editTextEditYear); // Giả định ID này tồn tại
        editTextSkills = findViewById(R.id.editTextEditSkills);
        editTextExperience = findViewById(R.id.editTextEditExperience);
        buttonUploadCv = findViewById(R.id.buttonUploadCv);
        textViewCvFileName = findViewById(R.id.textViewCvFileName);

        // Employer Views
        editTextCompanyName = findViewById(R.id.editTextEditCompanyName);
        editTextAddress = findViewById(R.id.editTextEditAddress);
        editTextPhone = findViewById(R.id.editTextEditPhone);
        editTextWebsite = findViewById(R.id.editTextEditWebsite);
    }

    private void loadUserData() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(this, "Người dùng chưa đăng nhập.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String uid = firebaseUser.getUid();
        db.collection(Constants.USERS_COLLECTION).document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentUserData = documentSnapshot.toObject(User.class);
                        if (currentUserData != null) {
                            currentUserRole = currentUserData.getRole();
                            populateUI();
                        }
                    } else {
                        Toast.makeText(this, "Không tìm thấy dữ liệu người dùng.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi tải dữ liệu.", Toast.LENGTH_SHORT).show());
    }

    private void populateUI() {
        if (currentUserData == null || currentUserRole == null) return;

        // Điền ảnh đại diện
        if (currentUserData.getProfileImageUrl() != null && !currentUserData.getProfileImageUrl().isEmpty()) {
            Glide.with(this).load(currentUserData.getProfileImageUrl()).into(imageViewProfile);
        }

        // Hiển thị các trường dựa trên vai trò
        if (Constants.ROLE_STUDENT.equals(currentUserRole)) {
            studentFieldsContainer.setVisibility(View.VISIBLE);
            employerFieldsContainer.setVisibility(View.GONE);

            editTextStudentFullName.setText(currentUserData.getFullName());
            editTextSchool.setText(currentUserData.getSchoolName());
            editTextMajor.setText(currentUserData.getMajor());
            editTextYear.setText(currentUserData.getYear());
            editTextSkills.setText(currentUserData.getSkillsDescription());
            editTextExperience.setText(currentUserData.getExperience());
            textViewCvFileName.setText(currentUserData.getCvUrl() != null ? "CV đã được tải lên" : "Chưa có tệp CV nào được chọn");

        } else if (Constants.ROLE_EMPLOYER.equals(currentUserRole)) {
            studentFieldsContainer.setVisibility(View.GONE);
            employerFieldsContainer.setVisibility(View.VISIBLE);

            editTextCompanyName.setText(currentUserData.getCompanyName());
            editTextAddress.setText(currentUserData.getAddress());
            editTextPhone.setText(currentUserData.getPhone());
            editTextWebsite.setText(currentUserData.getWebsite());
        }
    }

    private void saveAllChanges() {
        if (imageUri != null) {
            uploadImageAndThenSaveData();
        } else if (cvFileUri != null) {
            uploadCvAndThenSaveData(null);
        } else {
            saveDataToFirestore(null, null);
        }
    }

    private void uploadImageAndThenSaveData() {
        // Tải ảnh đại diện lên trước
        final StorageReference imageRef = storage.getReference().child("profile_images/" + mAuth.getCurrentUser().getUid());
        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            if (cvFileUri != null) {
                uploadCvAndThenSaveData(imageUrl);
            } else {
                saveDataToFirestore(imageUrl, currentUserData.getCvUrl());
            }
        })).addOnFailureListener(e -> Toast.makeText(this, "Lỗi tải ảnh đại diện", Toast.LENGTH_SHORT).show());
    }

    private void uploadCvAndThenSaveData(String imageUrl) {
        // Tải tệp CV lên
        final StorageReference cvRef = storage.getReference().child("cvs/" + mAuth.getCurrentUser().getUid() + "/" + getFileNameFromUri(cvFileUri));
        cvRef.putFile(cvFileUri).addOnSuccessListener(taskSnapshot -> cvRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String cvUrl = uri.toString();
            saveDataToFirestore(imageUrl, cvUrl);
        })).addOnFailureListener(e -> Toast.makeText(this, "Lỗi tải tệp CV", Toast.LENGTH_SHORT).show());
    }

    private void saveDataToFirestore(String newImageUrl, String newCvUrl) {
        String uid = mAuth.getCurrentUser().getUid();
        Map<String, Object> updates = new HashMap<>();

        if (newImageUrl != null) {
            updates.put("profileImageUrl", newImageUrl);
        }
        if (newCvUrl != null) {
            updates.put("cvUrl", newCvUrl);
        }

        if (Constants.ROLE_STUDENT.equals(currentUserRole)) {
            updates.put("fullName", editTextStudentFullName.getText().toString());
            updates.put("schoolName", editTextSchool.getText().toString());
            updates.put("major", editTextMajor.getText().toString());
            updates.put("year", editTextYear.getText().toString());
            updates.put("skillsDescription", editTextSkills.getText().toString());
            updates.put("experience", editTextExperience.getText().toString());
        } else if (Constants.ROLE_EMPLOYER.equals(currentUserRole)) {
            updates.put("companyName", editTextCompanyName.getText().toString());
            updates.put("address", editTextAddress.getText().toString());
            updates.put("phone", editTextPhone.getText().toString());
            updates.put("website", editTextWebsite.getText().toString());
        }

        db.collection(Constants.USERS_COLLECTION).document(uid).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Cập nhật thất bại.", Toast.LENGTH_SHORT).show());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(intent);
    }

    @SuppressLint("Range")
    private String getFileNameFromUri(Uri uri) {
        if (uri == null) return "unknown_file";
        String fileName = null;
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        }
        return fileName != null ? fileName : uri.getLastPathSegment();
    }
}
