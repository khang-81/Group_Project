package com.example.hanoistudentgigs.fragments.admin;

import android.app.AlertDialog;
import android.content.Intent;
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
import com.example.hanoistudentgigs.activities.ProfileEditActivity;

import android.content.DialogInterface;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.HashMap;
import java.util.Map;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.content.Intent;
import com.example.hanoistudentgigs.activities.ProfileEditActivity;
import android.app.Activity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

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
    private ActivityResultLauncher<Intent> editProfileLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Toast.makeText(getContext(), "Danh sách người dùng được cập nhật.", Toast.LENGTH_SHORT).show();
                        // The onResume will handle the refresh
                    }
                });
    }

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
        setupRecyclerView();

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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUsersFromFirestore();
    }

    private void setupRecyclerView() {
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
            public void onEdit(User user) {
                Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
                intent.putExtra("USER_ID", user.getUid());
                editProfileLauncher.launch(intent);
            }
            @Override
            public void onVerify(User user) {
                showVerifyUserDialog(user);
            }
        });
        rvUserList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUserList.setAdapter(userAdapter);
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
            .setMessage("Bạn có chắc muốn xóa người dùng này và toàn bộ dữ liệu liên quan? Hành động này không thể hoàn tác.")
            .setPositiveButton("Xóa", (dialog, which) -> {
                deleteUserAndRelatedData(user);
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void deleteUserAndRelatedData(User user) {
        // Step 1: Create a list of all tasks to get documents for deletion
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();

        // Task to get applications where the user is the student
        tasks.add(db.collection("applications").whereEqualTo("studentUid", user.getUid()).get());

        // If the user is an employer, add tasks for their jobs and related applications
        if ("EMPLOYER".equals(user.getRole())) {
            tasks.add(db.collection("jobs").whereEqualTo("employerUid", user.getUid()).get());
            tasks.add(db.collection("applications").whereEqualTo("employerUid", user.getUid()).get());
        }

        // Step 2: Wait for all read tasks to complete
        Tasks.whenAllSuccess(tasks).addOnSuccessListener(results -> {
            WriteBatch batch = db.batch();

            // Add the user document itself to the batch for deletion
            batch.delete(db.collection("users").document(user.getUid()));

            // Loop through the results of the tasks (list of QuerySnapshot)
            for (Object result : results) {
                if (result instanceof QuerySnapshot) {
                    QuerySnapshot snapshot = (QuerySnapshot) result;
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        batch.delete(doc.getReference());
                    }
                }
            }

            // Step 3: Commit the batch
            batch.commit().addOnSuccessListener(aVoid -> {
                Toast.makeText(getContext(), "Đã xóa người dùng và toàn bộ dữ liệu liên quan!", Toast.LENGTH_SHORT).show();
                loadUsersFromFirestore(); // Refresh the list
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Lỗi khi thực hiện xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Lỗi khi tìm dữ liệu để xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm người dùng mới");

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_user, null);
        final EditText etFullName = dialogView.findViewById(R.id.etFullName);
        final EditText etEmail = dialogView.findViewById(R.id.etEmail);
        final EditText etPhone = dialogView.findViewById(R.id.etPhone);
        final EditText etSchoolName = dialogView.findViewById(R.id.etSchoolName);
        final EditText etMajor = dialogView.findViewById(R.id.etMajor);
        final EditText etYear = dialogView.findViewById(R.id.etYear);
        final EditText etExperience = dialogView.findViewById(R.id.etExperience);
        final EditText etSkillsDescription = dialogView.findViewById(R.id.etSkillsDescription);
        final EditText etAddress = dialogView.findViewById(R.id.etAddress);
        final EditText etWebsite = dialogView.findViewById(R.id.etWebsite);

        if (isStudentTab) {
            etSchoolName.setVisibility(View.VISIBLE);
            etMajor.setVisibility(View.VISIBLE);
            etYear.setVisibility(View.VISIBLE);
            etExperience.setVisibility(View.VISIBLE);
            etSkillsDescription.setVisibility(View.VISIBLE);
            etAddress.setVisibility(View.GONE);
            etWebsite.setVisibility(View.GONE);
            etFullName.setHint("Họ tên");
            etSchoolName.setHint("Trường học");
        } else {
            etSchoolName.setHint("Tên công ty");
            etMajor.setVisibility(View.GONE);
            etYear.setVisibility(View.GONE);
            etExperience.setVisibility(View.GONE);
            etSkillsDescription.setVisibility(View.GONE);
            etAddress.setVisibility(View.VISIBLE);
            etWebsite.setVisibility(View.VISIBLE);
            etFullName.setHint("Tên người đại diện");
        }

        builder.setView(dialogView);
        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String schoolName = etSchoolName.getText().toString().trim();

            if (fullName.isEmpty() || email.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin bắt buộc.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a User object
            String role = isStudentTab ? "STUDENT" : "EMPLOYER";
            String uid = db.collection("users").document().getId();
            User newUser = new User();
            newUser.setUid(uid);
            newUser.setFullName(fullName);
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setRole(role);
            newUser.setVerified(false);

            if(isStudentTab) {
                newUser.setSchoolName(schoolName);
                newUser.setMajor(etMajor.getText().toString().trim());
                newUser.setYear(etYear.getText().toString().trim());
                newUser.setExperience(etExperience.getText().toString().trim());
                newUser.setSkillsDescription(etSkillsDescription.getText().toString().trim());
            } else {
                newUser.setCompanyName(schoolName);
                newUser.setAddress(etAddress.getText().toString().trim());
                newUser.setWebsite(etWebsite.getText().toString().trim());
            }

            // Add to Firestore
            db.collection("users").document(uid).set(newUser)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Thêm người dùng thành công!", Toast.LENGTH_SHORT).show();
                        loadUsersFromFirestore();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
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
            .setTitle("Duyệt tài khoản")
            .setMessage("Bạn có chắc chắn muốn duyệt tài khoản: " + user.getEmail() + " không?")
            .setPositiveButton("Duyệt", (dialog, which) -> {
                db.collection("users").document(user.getUid())
                    .update("verified", true)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Đã duyệt tài khoản!", Toast.LENGTH_SHORT).show();
                        loadUsersFromFirestore();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi duyệt: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
}