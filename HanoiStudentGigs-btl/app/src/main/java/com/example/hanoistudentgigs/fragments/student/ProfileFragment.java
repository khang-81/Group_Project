package com.example.hanoistudentgigs.fragments.student;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.activities.LoginActivity;
import com.example.hanoistudentgigs.activities.ProfileEditActivity;
import com.example.hanoistudentgigs.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {

    // Các Views chung
    private TextView textViewFullName, textViewEmail;
    private Button buttonLogout, buttonEditProfile;

    // Các Views dành riêng cho Sinh viên
    private LinearLayout studentInfoLayout;
    private TextView textViewSchool, textViewMajor, textViewSkills, textViewExperience, textViewCvStatus;
    private Button buttonUploadCv;
    private ProgressBar progressBarCv;

    // Các Views dành riêng cho Nhà tuyển dụng
    private LinearLayout employerInfoLayout;
    private TextView textViewCompanyName, textViewAddress, textViewPhone;


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri filePath = result.getData().getData();
                    if (filePath != null) {
                        uploadCv(filePath);
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Ánh xạ các View
        textViewFullName = view.findViewById(R.id.textViewProfileFullName);
        textViewEmail = view.findViewById(R.id.textViewProfileEmail);
        buttonLogout = view.findViewById(R.id.buttonLogout);
        buttonEditProfile = view.findViewById(R.id.buttonEditProfile);
        studentInfoLayout = view.findViewById(R.id.studentInfoLayout);
        textViewSchool = view.findViewById(R.id.textViewProfileSchool);
        textViewMajor = view.findViewById(R.id.textViewProfileMajor);
        textViewSkills = view.findViewById(R.id.textViewProfileSkills);
//        textViewExperience = view.findViewById(R.id.textViewProfileExperience);
        textViewCvStatus = view.findViewById(R.id.textViewCvStatus);
        buttonUploadCv = view.findViewById(R.id.buttonUploadCv);
        progressBarCv = view.findViewById(R.id.progressBarCv);
        employerInfoLayout = view.findViewById(R.id.employerInfoLayout);
        textViewCompanyName = view.findViewById(R.id.textViewProfileCompanyName);
        textViewAddress = view.findViewById(R.id.textViewProfileAddress);
        textViewPhone = view.findViewById(R.id.textViewProfilePhone);

        setClickListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile();
    }

    private void setClickListeners() {
        buttonEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
            startActivity(intent);
        });

        buttonLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        buttonUploadCv.setOnClickListener(v -> {
            // FIX: Cho phép chọn nhiều loại tệp văn bản
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*"); // Cho phép chọn bất kỳ loại file nào
            // Tạo một mảng chứa các loại MIME được chấp nhận
            String[] mimeTypes = {"application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            filePickerLauncher.launch(intent);
        });
    }

    private void loadUserProfile() {
        if (currentUser == null) return;
        db.collection(Constants.USERS_COLLECTION).document(currentUser.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (!isAdded() || getContext() == null || !task.isSuccessful() || task.getResult() == null) {
                        if (isAdded() && getContext() != null) {
                            Toast.makeText(getContext(), "Không thể tải hồ sơ.", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) {
                        Toast.makeText(getContext(), "Không tìm thấy hồ sơ người dùng.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        String role = document.getString("role");
                        setText(textViewEmail, document, "email", "Không có email");
                        if (Constants.ROLE_STUDENT.equals(role)) {
                            studentInfoLayout.setVisibility(View.VISIBLE);
                            employerInfoLayout.setVisibility(View.GONE);
                            setText(textViewFullName, document, "fullName");
                            setText(textViewSchool, document, "schoolName");
                            setText(textViewMajor, document, "major");
                            setText(textViewSkills, document, "skillsDescription");
                            setText(textViewExperience, document, "experience");
                            if (document.contains("cvUrl") && document.getString("cvUrl") != null && !document.getString("cvUrl").isEmpty()) {
                                textViewCvStatus.setText("Đã tải lên CV.");
                                textViewCvStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.successColor));
                            } else {
                                textViewCvStatus.setText("Chưa có CV.");
                                textViewCvStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.errorColor));
                            }
                        } else if (Constants.ROLE_EMPLOYER.equals(role)) {
                            studentInfoLayout.setVisibility(View.GONE);
                            employerInfoLayout.setVisibility(View.VISIBLE);
                            String companyName = document.getString("companyName");
                            setText(textViewFullName, document, "companyName", "Tên công ty");
                            setText(textViewCompanyName, document, "companyName");
                            setText(textViewAddress, document, "address");
                            setText(textViewPhone, document, "phone");
                        }
                    } catch (Exception e) {
                        Log.e("ProfileFragment", "Lỗi khi cập nhật giao diện hồ sơ", e);
                        Toast.makeText(getContext(), "Đã xảy ra lỗi khi hiển thị hồ sơ.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setText(TextView textView, DocumentSnapshot document, String fieldKey) {
        setText(textView, document, fieldKey, "Chưa cập nhật");
    }

    private void setText(TextView textView, DocumentSnapshot document, String fieldKey, String defaultValue) {
        if (textView != null) {
            if (document.contains(fieldKey) && document.getString(fieldKey) != null) {
                textView.setText(document.getString(fieldKey));
            } else {
                textView.setText(defaultValue);
            }
        }
    }

    // FIX: Hàm tiện ích để lấy phần mở rộng của file từ Uri
    private String getFileExtension(Uri uri) {
        if (getContext() != null) {
            ContentResolver cR = getContext().getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cR.getType(uri));
        }
        return null;
    }

    private void uploadCv(Uri filePath) {
        if (currentUser == null || getContext() == null) {
            Toast.makeText(getContext(), "Lỗi: Không thể xác định người dùng.", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBarCv.setVisibility(View.VISIBLE);
        buttonUploadCv.setEnabled(false);

        // FIX: Lấy phần mở rộng của file một cách linh hoạt
        String fileExtension = getFileExtension(filePath);
        if (fileExtension == null) {
            // Nếu không thể xác định, dùng một giá trị mặc định hoặc báo lỗi
            Toast.makeText(getContext(), "Không thể xác định loại tệp.", Toast.LENGTH_SHORT).show();
            progressBarCv.setVisibility(View.GONE);
            buttonUploadCv.setEnabled(true);
            return;
        }

        // FIX: Tạo tên file với phần mở rộng chính xác
        StorageReference cvRef = storage.getReference().child("cvs/" + currentUser.getUid() + "." + fileExtension);

        cvRef.putFile(filePath)
                .addOnSuccessListener(taskSnapshot ->
                        cvRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            db.collection(Constants.USERS_COLLECTION).document(currentUser.getUid()).update("cvUrl", downloadUrl)
                                    .addOnSuccessListener(aVoid -> {
                                        progressBarCv.setVisibility(View.GONE);
                                        buttonUploadCv.setEnabled(true);
                                        Toast.makeText(getContext(), "Tải lên CV thành công!", Toast.LENGTH_SHORT).show();
                                        loadUserProfile();
                                    });
                        })
                )
                .addOnFailureListener(e -> {
                    progressBarCv.setVisibility(View.GONE);
                    buttonUploadCv.setEnabled(true);
                    Toast.makeText(getContext(), "Tải lên thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
