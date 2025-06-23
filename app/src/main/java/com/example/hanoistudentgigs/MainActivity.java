package com.example.hanoistudentgigs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.hanoistudentgigs.activities.LoginActivity;
import com.example.hanoistudentgigs.fragments.admin.AdminApproveJobsFragment;
import com.example.hanoistudentgigs.fragments.admin.AdminDashboardFragment;
import com.example.hanoistudentgigs.fragments.admin.AdminManageUsersFragment;
import com.example.hanoistudentgigs.fragments.employer.EmployerDashboardFragment;
import com.example.hanoistudentgigs.fragments.student.ProfileFragment;
import com.example.hanoistudentgigs.fragments.student.StudentApplicationsFragment;
import com.example.hanoistudentgigs.fragments.student.StudentHomeFragment;
import com.example.hanoistudentgigs.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private BottomNavigationView bottomNav;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        bottomNav = findViewById(R.id.bottom_navigation);

        if (mAuth.getCurrentUser() == null) {
            sendToLogin();
            return;
        }
        // Logic để lấy vai trò người dùng và thiết lập giao diện là chính xác.
        setupUserInterface(savedInstanceState);
    }

    private void setupUserInterface(Bundle savedInstanceState) {
        String uid = mAuth.getCurrentUser().getUid();
        db.collection(Constants.USERS_COLLECTION).document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    if (!documentSnapshot.exists()) {
                        Log.w("MainActivity", "Tài liệu người dùng không tồn tại. Tạo tài liệu mặc định.");
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("role", Constants.ROLE_STUDENT); // Mặc định là sinh viên
                        userData.put("fullName", mAuth.getCurrentUser().getDisplayName() != null ? mAuth.getCurrentUser().getDisplayName() : "Người dùng mới");
                        userData.put("email", mAuth.getCurrentUser().getEmail());

                        db.collection(Constants.USERS_COLLECTION).document(uid).set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("MainActivity", "Đã tạo tài liệu người dùng mặc định.");
                                    userRole = Constants.ROLE_STUDENT;
                                    continueSetup(savedInstanceState);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("MainActivity", "Lỗi khi tạo tài liệu người dùng mặc định", e);
                                    mAuth.signOut();
                                    sendToLogin();
                                });
                    } else {
                        userRole = documentSnapshot.getString("role");
                        if (userRole == null || userRole.isEmpty()) {
                            Log.w("MainActivity", "Tài liệu người dùng tồn tại nhưng không có vai trò. Đặt mặc định là sinh viên.");
                            userRole = Constants.ROLE_STUDENT;
                            db.collection(Constants.USERS_COLLECTION).document(uid).update("role", Constants.ROLE_STUDENT);
                        }
                        continueSetup(savedInstanceState);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MainActivity", "Failed to get user role", e);
                    mAuth.signOut();
                    sendToLogin();
                });
    }

    private void continueSetup(Bundle savedInstanceState) {
        setupBottomNavigation();
        if (savedInstanceState == null) {
            // Tải fragment mặc định dựa trên vai trò người dùng
            loadFragment(getDefaultFragmentForRole());
        }
    }

    private void setupBottomNavigation() {
        bottomNav.getMenu().clear();
        if (userRole == null) {
            Log.e("MainActivity", "userRole vẫn null, đang gửi đến Login.");
            sendToLogin();
            return;
        }
        switch (userRole) {
            case Constants.ROLE_STUDENT:
                getMenuInflater().inflate(R.menu.student_bottom_navigation_menu, bottomNav.getMenu());
                break;
            case Constants.ROLE_EMPLOYER:
                getMenuInflater().inflate(R.menu.employer_bottom_navigation_menu, bottomNav.getMenu());
                break;
            case Constants.ROLE_ADMIN:
                getMenuInflater().inflate(R.menu.admin_bottom_navigation_menu, bottomNav.getMenu());
                break;
            default:
                Log.e("MainActivity", "Vai trò không xác định: " + userRole + ". Đang gửi đến Login.");
                mAuth.signOut();
                sendToLogin();
                break;
        }
        bottomNav.setOnItemSelectedListener(navListener);
    }

    private Fragment getDefaultFragmentForRole() {
        if (userRole == null) return null;
        switch (userRole) {
            case Constants.ROLE_STUDENT:
                return new StudentHomeFragment();
            // LỖI XẢY RA Ở ĐÂY vÌ EmployerDashboardFragment KHÔNG KẾ THỪA TỪ Fragment
            // Dòng code này là ĐÚNG. Vấn đề nằm ở file EmployerDashboardFragment.java
            case Constants.ROLE_EMPLOYER:
                return new EmployerDashboardFragment();
            case Constants.ROLE_ADMIN:
                return new AdminDashboardFragment();
            default:
                return null;
        }
    }

    private final BottomNavigationView.OnItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                // Logic tạo fragment dựa trên menu item được chọn. Logic này là chính xác.
                if (itemId == R.id.nav_student_home) {
                    selectedFragment = new StudentHomeFragment();
                } else if (itemId == R.id.nav_student_applications) {
                    selectedFragment = new StudentApplicationsFragment();
                } else if (itemId == R.id.nav_student_profile) {
                    selectedFragment = new ProfileFragment();
                }
                // LỖI CŨNG XẢY RA Ở ĐÂY vÌ EmployerDashboardFragment KHÔNG KẾ THỪA TỪ Fragment
                else if (itemId == R.id.nav_recruiter_dashboard) {
                    selectedFragment = new EmployerDashboardFragment();
                } else if (itemId == R.id.nav_recruiter_profile) {
                    selectedFragment = new ProfileFragment(); // Giả định ProfileFragment cũng là một Fragment hợp lệ
                }
                else if (itemId == R.id.nav_admin_dashboard) {
                    selectedFragment = new AdminDashboardFragment();
                } else if (itemId == R.id.nav_admin_approve_jobs) {
                    selectedFragment = new AdminApproveJobsFragment();
                } else if (itemId == R.id.nav_admin_manage_users) {
                    selectedFragment = new AdminManageUsersFragment();
                }

                // Phương thức loadFragment xử lý transaction một cách chính xác.
                return loadFragment(selectedFragment);
            };

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null && !isFinishing() && !getSupportFragmentManager().isStateSaved()) {
            try {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                return true;
            } catch (IllegalStateException e) {
                Log.e("MainActivity", "Lỗi khi thực hiện giao dịch fragment", e);
            }
        }
        return false;
    }

    private void sendToLogin() {
        if (!isFinishing()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}
