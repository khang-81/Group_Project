package com.example.hanoistudentgigs.fragments.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.adapters.UserAdapter;
import com.example.hanoistudentgigs.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminManageUsersFragment extends Fragment {
    private Button btnStudentTab, btnEmployerTab;
    private EditText etSearchUser;
    private RecyclerView rvUserList;
    private UserAdapter userAdapter;
    private List<User> allUsers = new ArrayList<>();
    private List<User> filteredUsers = new ArrayList<>();
    private boolean isStudentTab = true;
    private FirebaseFirestore db;
    private FloatingActionButton fabAddUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_manage_users, container, false);
        btnStudentTab = view.findViewById(R.id.btnStudentTab);
        btnEmployerTab = view.findViewById(R.id.btnEmployerTab);
        etSearchUser = view.findViewById(R.id.etSearchUser);
        rvUserList = view.findViewById(R.id.rvUserList);
        fabAddUser = view.findViewById(R.id.fabAddUser);

        db = FirebaseFirestore.getInstance();
        userAdapter = new UserAdapter(filteredUsers, new UserAdapter.OnUserActionListener() {
            @Override
            public void onDelete(User user) {
                confirmAndDeleteUser(user);
            }
            @Override
            public void onView(User user) {
                showUserDetailDialog(user);
            }
            @Override
            public void onVerify(User user) {
                showVerifyUserDialog(user);
            }
        });
        rvUserList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUserList.setAdapter(userAdapter);

        btnStudentTab.setOnClickListener(v -> switchTab(true));
        btnEmployerTab.setOnClickListener(v -> switchTab(false));

        etSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        fabAddUser.setOnClickListener(v -> showAddUserDialog());

        loadUsersFromFirestore();
        return view;
    }

    private void switchTab(boolean student) {
        isStudentTab = student;
        btnStudentTab.setEnabled(!student);
        btnEmployerTab.setEnabled(student);
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
                        filterUsers(etSearchUser.getText().toString());
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

        final EditText etName = dialogView.findViewById(R.id.etName);
        final EditText etEmail = dialogView.findViewById(R.id.etEmail);
        final EditText etPhone = dialogView.findViewById(R.id.etPhone);
        final EditText etSchoolOrCompany = dialogView.findViewById(R.id.etSchoolOrCompany);

        etSchoolOrCompany.setHint(isStudentTab ? "Trường học" : "Tên công ty");

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String schoolOrCompany = etSchoolOrCompany.getText().toString().trim();
            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đủ tên và email", Toast.LENGTH_SHORT).show();
                return;
            }
            String uid = db.collection("users").document().getId();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("uid", uid);
            userMap.put("fullName", isStudentTab ? name : "");
            userMap.put("companyName", isStudentTab ? "" : name);
            userMap.put("email", email);
            userMap.put("role", isStudentTab ? "STUDENT" : "EMPLOYER");
            userMap.put("SDT", phone);
            userMap.put(isStudentTab ? "school" : "companyName", schoolOrCompany);
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
}