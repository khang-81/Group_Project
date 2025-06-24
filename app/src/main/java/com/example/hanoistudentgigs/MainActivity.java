package com.example.hanoistudentgigs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.activities.LoginActivity;
import com.example.hanoistudentgigs.activities.TrangChuActivity;
import com.example.hanoistudentgigs.fragments.admin.AdminApproveJobsFragment;
import com.example.hanoistudentgigs.fragments.admin.AdminDashboardFragment;
//import com.example.hanoistudentgigs.fragments.admin.AdminManageUsersFragment;
import com.example.hanoistudentgigs.fragments.employer.EmployerDashboardFragment;
import com.example.hanoistudentgigs.fragments.student.ProfileFragment;
import com.example.hanoistudentgigs.fragments.student.StudentApplicationsFragment;
import com.example.hanoistudentgigs.fragments.student.StudentHomeFragment;
import com.example.hanoistudentgigs.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap; // Thêm import này
import java.util.Map; // Thêm import này

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
        setupUserInterface(savedInstanceState);
    }

    private void setupUserInterface(Bundle savedInstanceState) {
        String uid = mAuth.getCurrentUser().getUid();
        db.collection(Constants.USERS_COLLECTION).document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (isFinishing() || isDestroyed()) {
                        // Nếu Activity đang bị hủy, không làm gì cả
                        return;
                    }

                    if (!documentSnapshot.exists()) {
                        // Tài liệu người dùng không tồn tại, tạo một tài liệu mặc định
                        Log.w("MainActivity", "Tài liệu người dùng không tồn tại. Tạo tài liệu mặc định.");
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("role", Constants.ROLE_STUDENT); // Mặc định là sinh viên
                        // Thêm các trường mặc định khác nếu cần, ví dụ:
                        userData.put("fullName", mAuth.getCurrentUser().getDisplayName() != null ? mAuth.getCurrentUser().getDisplayName() : "Người dùng mới");
                        userData.put("email", mAuth.getCurrentUser().getEmail());

                        db.collection(Constants.USERS_COLLECTION).document(uid).set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("MainActivity", "Đã tạo tài liệu người dùng mặc định.");
                                    userRole = Constants.ROLE_STUDENT; // Đặt vai trò sau khi tạo
                                    continueSetup(savedInstanceState); // Tiếp tục thiết lập UI
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("MainActivity", "Lỗi khi tạo tài liệu người dùng mặc định", e);
                                    // Nếu không tạo được tài liệu, có thể đăng xuất để tránh vòng lặp lỗi
                                    mAuth.signOut();
                                    sendToLogin();
                                });
                    } else {
                        // Tài liệu người dùng đã tồn tại, lấy vai trò và tiếp tục
                        userRole = documentSnapshot.getString("role");
                        if (userRole == null || userRole.isEmpty()) {
                            // Trường hợp hiếm: tài liệu tồn tại nhưng không có vai trò.
                            // Có thể xử lý bằng cách đặt vai trò mặc định hoặc đăng xuất.
                            Log.w("MainActivity", "Tài liệu người dùng tồn tại nhưng không có vai trò. Đặt mặc định là sinh viên.");
                            userRole = Constants.ROLE_STUDENT;
                            // Cập nhật vai trò vào Firestore nếu bạn muốn
                            db.collection(Constants.USERS_COLLECTION).document(uid).update("role", Constants.ROLE_STUDENT)
                                    .addOnSuccessListener(aVoid -> Log.d("MainActivity", "Đã cập nhật vai trò mặc định cho người dùng."))
                                    .addOnFailureListener(e -> Log.e("MainActivity", "Lỗi cập nhật vai trò mặc định.", e));
                        }
                        continueSetup(savedInstanceState); // Tiếp tục thiết lập UI
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MainActivity", "Failed to get user role", e);
                    // Nếu không lấy được tài liệu người dùng vì lỗi, thì đăng xuất
                    mAuth.signOut(); // Giữ lại dòng này trong trường hợp lỗi mạng hoặc quyền truy cập
                    sendToLogin();
                });
    }

    // Phương thức mới để tiếp tục thiết lập UI sau khi vai trò được xác định/tạo
    private void continueSetup(Bundle savedInstanceState) {
        setupBottomNavigation();
        if (savedInstanceState == null) {
            loadFragment(getDefaultFragmentForRole());
        }
    }

    private void setupBottomNavigation() {
        bottomNav.getMenu().clear();
        if (userRole == null) {
            // Điều này không nên xảy ra sau khi continueSetup được gọi, nhưng là một kiểm tra an toàn
            Log.e("MainActivity", "userRole vẫn null sau khi thiết lập. Đang gửi đến Login.");
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
                mAuth.signOut(); // Đăng xuất nếu vai trò không hợp lệ
                sendToLogin();
                break;
        }
        bottomNav.setOnItemSelectedListener(navListener);
    }

    private Fragment getDefaultFragmentForRole() {
        if (userRole == null) return null; // Vẫn cần kiểm tra an toàn
        switch (userRole) {
            case Constants.ROLE_STUDENT:
                return new StudentHomeFragment();
            case Constants.ROLE_EMPLOYER:
                startActivity(new Intent(this, TrangChuActivity.class));
                finish(); // Đóng Activity hiện tại để tránh quay lại Fragment cũ
                return null; // Không trả về Fragment nữa
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

                // --- Student Navigation ---
                if (itemId == R.id.nav_student_home) {
                    selectedFragment = new StudentHomeFragment();
                } else if (itemId == R.id.nav_student_applications) {
                    selectedFragment = new StudentApplicationsFragment();
                } else if (itemId == R.id.nav_student_profile) {
                    selectedFragment = new ProfileFragment();
                }
                // --- Employer (Recruiter) Navigation ---
                else if (itemId == R.id.nav_recruiter_dashboard) {
                    selectedFragment = new EmployerDashboardFragment();
                } else if (itemId == R.id.nav_recruiter_profile) {
                    selectedFragment = new ProfileFragment();
                }
// --- Admin Navigation ---
                else if (itemId == R.id.nav_admin_dashboard) {
                    selectedFragment = new AdminDashboardFragment();
                } else if (itemId == R.id.nav_admin_approve_jobs) {
                    selectedFragment = new AdminApproveJobsFragment();
                } else if (itemId == R.id.nav_admin_manage_users) {
//                    selectedFragment = new AdminManageUsersFragment();
                }

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