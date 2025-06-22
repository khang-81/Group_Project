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


    // Các Views dành riêng cho Nhà tuyển dụng
    private LinearLayout employerInfoLayout;
    private TextView textViewCompanyName, textViewAddress, textViewPhone;


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private FirebaseUser currentUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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


    }

    private void loadUserProfile() {
        if (currentUser == null) {
            // Người dùng chưa đăng nhập
            textViewFullName.setText("Chưa đăng nhập");
            textViewEmail.setText("Không có email");
            studentInfoLayout.setVisibility(View.GONE);
            employerInfoLayout.setVisibility(View.GONE);
            return;
        }

        final String uid = currentUser.getUid();
        textViewEmail.setText(currentUser.getEmail()); // Hiển thị email ngay lập tức

        // BƯỚC 1: Đọc VAI TRÒ từ collection "users"
        db.collection(Constants.USERS_COLLECTION).document(uid).get()
                .addOnSuccessListener(userDoc -> {
                    if (!isAdded() || getContext() == null) { // Đảm bảo Fragment còn gắn với Activity
                        return;
                    }

                    if (userDoc.exists()) {
                        String userRole = userDoc.getString("role");

                        // Xử lý cho Sinh viên
                        if (Constants.ROLE_STUDENT.equals(userRole)) {
                            studentInfoLayout.setVisibility(View.VISIBLE);
                            employerInfoLayout.setVisibility(View.GONE);

                            // BƯỚC 2: Nếu là sinh viên, ĐỌC PROFILE CHI TIẾT TỪ STUDENTS_COLLECTION
                            db.collection(Constants.STUDENTS_COLLECTION).document(uid).get()
                                    .addOnSuccessListener(studentProfileDoc -> {
                                        if (!isAdded() || getContext() == null) return;

                                        if (studentProfileDoc.exists()) {
                                            Log.d("ProfileFragment", "Student Profile Data: " + studentProfileDoc.getData()); // Log để kiểm tra dữ liệu

                                            setText(textViewFullName, studentProfileDoc, "fullName");
                                            setText(textViewSchool, studentProfileDoc, "schoolName"); // Dùng schoolName cho nhất quán với RegisterActivity
                                            setText(textViewMajor, studentProfileDoc, "major");
                                            setText(textViewSkills, studentProfileDoc, "skillsDescription");
                                            // Kiểm tra textViewExperience có được ánh xạ không trước khi dùng
                                            // if (textViewExperience != null) {
                                            //     setText(textViewExperience, studentProfileDoc, "experience");
                                            // }

                                            String cvFileName = studentProfileDoc.getString("cvFileName");
                                            if (cvFileName != null && !cvFileName.isEmpty()) {
                                                textViewCvStatus.setText("CV hiện tại: " + cvFileName);
                                                textViewCvStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.successColor));
                                            } else {
                                                textViewCvStatus.setText("Chưa có tệp CV nào được chọn.");
                                                textViewCvStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.errorColor));
                                            }
                                        } else {
                                            Toast.makeText(getContext(), "Hồ sơ sinh viên chưa được thiết lập.", Toast.LENGTH_SHORT).show();
                                            // Đặt lại các TextView thành "Chưa cập nhật" nếu không có dữ liệu
                                            textViewFullName.setText("Chưa cập nhật");
                                            textViewSchool.setText("Chưa cập nhật");
                                            textViewMajor.setText("Chưa cập nhật");
                                            textViewSkills.setText("Chưa cập nhật");
                                            // if (textViewExperience != null) textViewExperience.setText("Chưa cập nhật");
                                            textViewCvStatus.setText("Chưa có tệp CV nào được chọn.");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        if (!isAdded() || getContext() == null) return;
                                        Log.e("ProfileFragment", "Lỗi tải hồ sơ sinh viên: " + e.getMessage(), e);
                                        Toast.makeText(getContext(), "Lỗi tải hồ sơ sinh viên.", Toast.LENGTH_SHORT).show();
                                        textViewFullName.setText("Lỗi tải");
                                        textViewSchool.setText("Lỗi tải");
                                        textViewMajor.setText("Lỗi tải");
                                        textViewSkills.setText("Lỗi tải");
                                        // if (textViewExperience != null) textViewExperience.setText("Lỗi tải");
                                        textViewCvStatus.setText("Lỗi tải");
                                    });

                        }
                        // Xử lý cho Nhà tuyển dụng
                        else if (Constants.ROLE_EMPLOYER.equals(userRole)) {
                            studentInfoLayout.setVisibility(View.GONE);
                            employerInfoLayout.setVisibility(View.VISIBLE);

                            // BƯỚC 2: Nếu là nhà tuyển dụng, ĐỌC PROFILE CHI TIẾT TỪ EMPLOYERS_COLLECTION
                            db.collection(Constants.EMPLOYERS_COLLECTION).document(uid).get()
                                    .addOnSuccessListener(employerProfileDoc -> {
                                        if (!isAdded() || getContext() == null) return;

                                        if (employerProfileDoc.exists()) {
                                            Log.d("ProfileFragment", "Employer Profile Data: " + employerProfileDoc.getData()); // Log để kiểm tra dữ liệu

                                            setText(textViewFullName, employerProfileDoc, "companyName"); // Hiển thị tên công ty là tên chính
                                            setText(textViewCompanyName, employerProfileDoc, "companyName");
                                            setText(textViewAddress, employerProfileDoc, "address");
                                            setText(textViewPhone, employerProfileDoc, "phone");
                                        } else {
                                            Toast.makeText(getContext(), "Hồ sơ nhà tuyển dụng chưa được thiết lập.", Toast.LENGTH_SHORT).show();
                                            textViewFullName.setText("Chưa cập nhật");
                                            textViewCompanyName.setText("Chưa cập nhật");
                                            textViewAddress.setText("Chưa cập nhật");
                                            textViewPhone.setText("Chưa cập nhật");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        if (!isAdded() || getContext() == null) return;
                                        Log.e("ProfileFragment", "Lỗi tải hồ sơ nhà tuyển dụng: " + e.getMessage(), e);
                                        Toast.makeText(getContext(), "Lỗi tải hồ sơ nhà tuyển dụng.", Toast.LENGTH_SHORT).show();
                                        textViewFullName.setText("Lỗi tải");
                                        textViewCompanyName.setText("Lỗi tải");
                                        textViewAddress.setText("Lỗi tải");
                                        textViewPhone.setText("Lỗi tải");
                                    });
                        } else {
                            // Vai trò không xác định
                            Toast.makeText(getContext(), "Không tìm thấy vai trò người dùng hợp lệ.", Toast.LENGTH_SHORT).show();
                            textViewFullName.setText("Chưa cập nhật");
                            studentInfoLayout.setVisibility(View.GONE);
                            employerInfoLayout.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng cơ bản.", Toast.LENGTH_SHORT).show();
                        textViewFullName.setText("Chưa cập nhật");
                        studentInfoLayout.setVisibility(View.GONE);
                        employerInfoLayout.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    if (!isAdded() || getContext() == null) return;
                    Log.e("ProfileFragment", "Lỗi tải thông tin người dùng cơ bản: " + e.getMessage(), e);
                    Toast.makeText(getContext(), "Lỗi tải thông tin người dùng.", Toast.LENGTH_SHORT).show();
                    textViewFullName.setText("Lỗi tải");
                    studentInfoLayout.setVisibility(View.GONE);
                    employerInfoLayout.setVisibility(View.GONE);
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
    }}

