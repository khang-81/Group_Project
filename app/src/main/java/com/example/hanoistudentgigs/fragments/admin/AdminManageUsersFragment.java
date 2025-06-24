package com.example.hanoistudentgigs.fragments.admin;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.EditText;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.hanoistudentgigs.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.hanoistudentgigs.adapters.UserAdapter;
import com.example.hanoistudentgigs.models.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminManageUsersFragment extends Fragment {
    private Button btnTabStudent, btnTabEmployer;
    private EditText editTextSearchUser;
    private FloatingActionButton buttonAddUser;
    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private List<User> allUsers = new ArrayList<>();
    private List<User> filteredUsers = new ArrayList<>();
    private boolean isStudentTab = true;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_manage_users, container, false);
        btnTabStudent = view.findViewById(R.id.btnTabStudent);
        btnTabEmployer = view.findViewById(R.id.btnTabEmployer);
        editTextSearchUser = view.findViewById(R.id.editTextSearchUser);
        buttonAddUser = view.findViewById(R.id.buttonAddUser);
        recyclerViewUsers = view.findViewById(R.id.recyclerViewUsers);

        db = FirebaseFirestore.getInstance();
        userAdapter = new UserAdapter(filteredUsers, new UserAdapter.OnUserActionListener() {
            @Override
            public void onDelete(User user) { confirmAndDeleteUser(user); }
            @Override
            public void onView(User user) { showUserDetailDialog(user); }
            @Override
            public void onVerify(User user) { showVerifyUserDialog(user); }
            @Override
            public void onEdit(User user) { showEditUserDialog(user); }
        });
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewUsers.setAdapter(userAdapter);

        buttonAddUser.setOnClickListener(v -> showAddUserDialog());
        editTextSearchUser.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filterUsers(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });
        btnTabStudent.setOnClickListener(v -> {
            btnTabStudent.setSelected(true);
            btnTabEmployer.setSelected(false);
            switchTab(true);
        });
        btnTabEmployer.setOnClickListener(v -> {
            btnTabStudent.setSelected(false);
            btnTabEmployer.setSelected(true);
            switchTab(false);
        });
        btnTabStudent.setSelected(true);
        btnTabEmployer.setSelected(false);
        switchTab(true);
        return view;
    }

    private void switchTab(boolean student) {
        isStudentTab = student;
        btnTabStudent.setSelected(student);
        btnTabEmployer.setSelected(!student);
        loadUsersFromFirestore();
    }

    private void filterUsers(String query) {
        filteredUsers.clear();
        for (User user : allUsers) {
            if ((isStudentTab && user.getRole().equals("STUDENT")) || (!isStudentTab && user.getRole().equals("EMPLOYER"))) {
                if ((user.getFullName() != null && user.getFullName().toLowerCase().contains(query.toLowerCase())) ||
                        (user.getEmail() != null && user.getEmail().toLowerCase().contains(query.toLowerCase()))) {
                    filteredUsers.add(user);
                }
            }
        }
        userAdapter.notifyDataSetChanged();
    }

    private void loadUsersFromFirestore() {
        allUsers.clear();
        filteredUsers.clear();
        String role = isStudentTab ? "STUDENT" : "EMPLOYER";
        db.collection("users")
                .whereEqualTo("role", role)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            user.setUid(document.getId());
                            allUsers.add(user);
                        }
                        filterUsers(editTextSearchUser.getText().toString());
                    }
                });
    }

    private void confirmAndDeleteUser(User user) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa người dùng này và toàn bộ dữ liệu liên quan?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // Xóa user
                    db.collection("users").document(user.getUid()).delete()
                            .addOnSuccessListener(aVoid -> {
                                // Xóa application liên quan (studentUid hoặc employerUid)
                                db.collection("applications")
                                        .whereEqualTo("studentUid", user.getUid())
                                        .get()
                                        .addOnSuccessListener(queryDocumentSnapshots -> {
                                            for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                                                doc.getReference().delete();
                                            }
                                            // Nếu là employer, xóa application theo employerUid
                                            if (user.getRole() != null && user.getRole().equals("EMPLOYER")) {
                                                db.collection("applications")
                                                        .whereEqualTo("employerUid", user.getUid())
                                                        .get()
                                                        .addOnSuccessListener(employerApps -> {
                                                            for (com.google.firebase.firestore.DocumentSnapshot doc : employerApps) {
                                                                doc.getReference().delete();
                                                            }
                                                            // Xóa job liên quan
                                                            db.collection("jobs")
                                                                    .whereEqualTo("employerUid", user.getUid())
                                                                    .get()
                                                                    .addOnSuccessListener(jobs -> {
                                                                        for (com.google.firebase.firestore.DocumentSnapshot jobDoc : jobs) {
                                                                            jobDoc.getReference().delete();
                                                                        }
                                                                        Toast.makeText(getContext(), "Đã xóa người dùng và dữ liệu liên quan!", Toast.LENGTH_SHORT).show();
                                                                        loadUsersFromFirestore();
                                                                    });
                                                        });
                                            } else {
                                                Toast.makeText(getContext(), "Đã xóa người dùng và dữ liệu liên quan!", Toast.LENGTH_SHORT).show();
                                                loadUsersFromFirestore();
                                            }
                                        });
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm người dùng mới");

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_user, null);
        builder.setView(dialogView);

        final EditText etFullName = dialogView.findViewById(R.id.etName);
        final EditText etEmail = dialogView.findViewById(R.id.etEmail);
        final EditText etPhone = dialogView.findViewById(R.id.etPhone);
        final EditText etSchoolName = dialogView.findViewById(R.id.etSchoolOrCompany);
        final EditText etCompanyName = dialogView.findViewById(R.id.etCompanyName);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String fullName = etFullName.getText().toString().trim();
            String schoolName = etSchoolName.getText().toString().trim();
            String companyName = etCompanyName.getText().toString().trim();
            if (fullName.isEmpty() || email.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đủ tên và email", Toast.LENGTH_SHORT).show();
                return;
            }
            String uid = db.collection("users").document().getId();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("uid", uid);
            userMap.put("fullName", isStudentTab ? fullName : "");
            userMap.put("companyName", isStudentTab ? "" : fullName);
            userMap.put("email", email);
            userMap.put("role", isStudentTab ? "STUDENT" : "EMPLOYER");
            userMap.put("phone", phone);
            if (isStudentTab) {
                userMap.put("schoolName", schoolName);
            } else {
                userMap.put("companyName", companyName);
            }
            db.collection("users").document(uid).set(userMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Đã thêm người dùng!", Toast.LENGTH_SHORT).show();
                        loadUsersFromFirestore();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi thêm: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showUserDetailDialog(User user) {
        StringBuilder info = new StringBuilder();
        info.append("Họ tên: ").append(user.getFullName() != null ? user.getFullName() : "").append("\n");
        info.append("Email: ").append(user.getEmail() != null ? user.getEmail() : "").append("\n");
        info.append("Role: ").append(user.getRole() != null ? user.getRole() : "").append("\n");
        if (user.getRole() != null && user.getRole().equals("STUDENT")) {
            info.append("Trường học: ").append(user.getSchoolName() != null ? user.getSchoolName() : "").append("\n");
            info.append("Chuyên ngành: ").append(user.getMajor() != null ? user.getMajor() : "").append("\n");
            info.append("Năm: ").append(user.getYear() != null ? user.getYear() : "").append("\n");
            info.append("Kinh nghiệm: ").append(user.getExperience() != null ? user.getExperience() : "").append("\n");
            info.append("Kỹ năng: ").append(user.getSkillsDescription() != null ? user.getSkillsDescription() : "").append("\n");
            info.append("Đã xác thực: ").append(user.isVerified() ? "Có" : "Không").append("\n");
            info.append("SĐT: ").append(user.getPhone() != null ? user.getPhone() : "");
        } else if (user.getRole() != null && user.getRole().equals("EMPLOYER")) {
            info.append("Tên công ty: ").append(user.getCompanyName() != null ? user.getCompanyName() : "").append("\n");
            info.append("Địa chỉ: ").append(user.getAddress() != null ? user.getAddress() : "").append("\n");
            info.append("SĐT: ").append(user.getPhone() != null ? user.getPhone() : "").append("\n");
            info.append("Website: ").append(user.getWebsite() != null ? user.getWebsite() : "").append("\n");
            info.append("Đã xác thực: ").append(user.isVerified() ? "Có" : "Không");
        }
        new AlertDialog.Builder(getContext())
                .setTitle("Thông tin người dùng")
                .setMessage(info.toString())
                .setPositiveButton("Đóng", null)
                .show();
    }

    private void showVerifyUserDialog(User user) {
        new AlertDialog.Builder(getContext())
                .setTitle("")
                .setMessage("Bạn có chắc chắn xác thực tài khoản: " + user.getEmail() + " không?")
                .setPositiveButton("Có", (dialog, which) -> {
                    db.collection("users").document(user.getUid())
                            .update("verified", true)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Đã xác thực tài khoản!", Toast.LENGTH_SHORT).show();
                                loadUsersFromFirestore();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi xác thực: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void showEditUserDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sửa thông tin người dùng");
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_user, null, false);
        builder.setView(dialogView);
        // Sinh viên
        EditText etFullName = dialogView.findViewById(R.id.etName);
        EditText etSchoolName = dialogView.findViewById(R.id.etSchoolOrCompany);
        EditText etMajor = dialogView.findViewById(R.id.etMajor);
        EditText etYear = dialogView.findViewById(R.id.etYear);
        EditText etExperience = dialogView.findViewById(R.id.etExperience);
        EditText etSkillsDescription = dialogView.findViewById(R.id.etSkillsDescription);
        // Chung
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        EditText etPhone = dialogView.findViewById(R.id.etPhone);
        // Nhà tuyển dụng
        EditText etCompanyName = dialogView.findViewById(R.id.etCompanyName);
        EditText etAddress = dialogView.findViewById(R.id.etAddress);
        EditText etWebsite = dialogView.findViewById(R.id.etWebsite);

        if (isStudentTab) {
            // Hiện trường sinh viên, ẩn employer
            etFullName.setVisibility(View.VISIBLE);
            etSchoolName.setVisibility(View.VISIBLE);
            etMajor.setVisibility(View.VISIBLE);
            etYear.setVisibility(View.VISIBLE);
            etExperience.setVisibility(View.VISIBLE);
            etSkillsDescription.setVisibility(View.VISIBLE);
            etCompanyName.setVisibility(View.GONE);
            etAddress.setVisibility(View.GONE);
            etWebsite.setVisibility(View.GONE);
            // Đổ dữ liệu
            etFullName.setText(user.getFullName());
            etSchoolName.setText(user.getSchoolName());
            etMajor.setText(user.getMajor());
            etYear.setText(user.getYear());
            etExperience.setText(user.getExperience());
            etSkillsDescription.setText(user.getSkillsDescription());
        } else {
            // Hiện trường employer, ẩn sinh viên
            etFullName.setVisibility(View.GONE);
            etSchoolName.setVisibility(View.GONE);
            etMajor.setVisibility(View.GONE);
            etYear.setVisibility(View.GONE);
            etExperience.setVisibility(View.GONE);
            etSkillsDescription.setVisibility(View.GONE);
            etCompanyName.setVisibility(View.VISIBLE);
            etAddress.setVisibility(View.VISIBLE);
            etWebsite.setVisibility(View.VISIBLE);
            // Đổ dữ liệu
            etCompanyName.setText(user.getCompanyName());
            etAddress.setText(user.getAddress());
            etWebsite.setText(user.getWebsite());
        }
        // Chung
        etEmail.setText(user.getEmail());
        etPhone.setText(user.getPhone());

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String uid = user.getUid();
            Map<String, Object> updates = new HashMap<>();
            if (isStudentTab) {
                String fullName = etFullName.getText().toString().trim();
                String schoolName = etSchoolName.getText().toString().trim();
                String major = etMajor.getText().toString().trim();
                String year = etYear.getText().toString().trim();
                String experience = etExperience.getText().toString().trim();
                String skillsDescription = etSkillsDescription.getText().toString().trim();
                updates.put("fullName", fullName);
                updates.put("email", email);
                updates.put("phone", phone);
                updates.put("schoolName", schoolName);
                updates.put("major", major);
                updates.put("year", year);
                updates.put("experience", experience);
                updates.put("skillsDescription", skillsDescription);
                // Xoá trắng employer
                updates.put("companyName", "");
                updates.put("address", "");
                updates.put("website", "");
            } else {
                String companyName = etCompanyName.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                String website = etWebsite.getText().toString().trim();
                updates.put("companyName", companyName);
                updates.put("email", email);
                updates.put("phone", phone);
                updates.put("address", address);
                updates.put("website", website);
                // Xoá trắng student
                updates.put("fullName", companyName);
                updates.put("schoolName", "");
                updates.put("major", "");
                updates.put("year", "");
                updates.put("experience", "");
                updates.put("skillsDescription", "");
            }
            db.collection("users").document(uid)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Đã cập nhật người dùng!", Toast.LENGTH_SHORT).show();
                    loadUsersFromFirestore();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}
