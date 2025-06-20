package com.example.hanoistudentgigs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.hanoistudentgigs.R;
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
<<<<<<< HEAD
        Intent intent = getIntent();
        boolean selectApproveTab = intent != null && intent.getBooleanExtra("SELECT_ADMIN_APPROVE_TAB", false);
        setupUserInterface(savedInstanceState, selectApproveTab);
=======

        setupUserInterface(savedInstanceState);
>>>>>>> 0daf4c96e5b8d3237940472bc009dd824b26659e
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getBooleanExtra("SELECT_ADMIN_APPROVE_TAB", false)) {
            if (userRole != null && userRole.equals(Constants.ROLE_ADMIN)) {
                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.nav_admin_approve_jobs);
                }
            }
        }
    }

    private void setupUserInterface(Bundle savedInstanceState, boolean selectApproveTab) {
        String uid = mAuth.getCurrentUser().getUid();
        db.collection(Constants.USERS_COLLECTION).document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    // FIX: Kiểm tra xem Activity có còn tồn tại không trước khi làm bất cứ điều gì
                    if (isFinishing() || isDestroyed() || !documentSnapshot.exists()) {
                        mAuth.signOut();
                        sendToLogin();
                        return;
                    }

                    userRole = documentSnapshot.getString("role");
<<<<<<< HEAD
                    setupBottomNavigation(selectApproveTab, savedInstanceState);
=======
                    setupBottomNavigation();

                    // Chỉ load Fragment mặc định nếu đây là lần đầu tiên Activity được tạo.
                    if (savedInstanceState == null) {
                        loadFragment(getDefaultFragmentForRole());
                    }
>>>>>>> 0daf4c96e5b8d3237940472bc009dd824b26659e
                })
                .addOnFailureListener(e -> {
                    Log.e("MainActivity", "Failed to get user role", e);
                    sendToLogin();
                });
    }

    private void setupBottomNavigation(boolean selectApproveTab, Bundle savedInstanceState) {
        bottomNav.getMenu().clear();
        if (userRole == null) return;

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
        }
        bottomNav.setOnItemSelectedListener(navListener);
        if (savedInstanceState == null) {
            if (selectApproveTab && userRole.equals(Constants.ROLE_ADMIN)) {
                bottomNav.setSelectedItemId(R.id.nav_admin_approve_jobs);
                loadFragment(new AdminApproveJobsFragment());
            } else {
                loadFragment(getDefaultFragmentForRole());
            }
        }
    }

    private Fragment getDefaultFragmentForRole() {
        if (userRole == null) return null;
        switch (userRole) {
            case Constants.ROLE_STUDENT:
                return new StudentHomeFragment();
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

                // Logic điều hướng giữ nguyên
                // --- Student Navigation ---
                if (itemId == R.id.nav_student_home) {
                    selectedFragment = new StudentHomeFragment();
                } else if (itemId == R.id.nav_student_applications) {
                    selectedFragment = new StudentApplicationsFragment();
                } else if (itemId == R.id.nav_student_profile) {
                    selectedFragment = new ProfileFragment();
                }

                // --- Recruiter (Employer) Navigation ---
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
                    selectedFragment = new AdminManageUsersFragment();
                }

                return loadFragment(selectedFragment);
            };

    private boolean loadFragment(Fragment fragment) {
        // FIX: Thêm kiểm tra an toàn toàn diện trước khi thực hiện FragmentTransaction
        if (fragment != null && !isFinishing() && !getSupportFragmentManager().isStateSaved()) {
            try {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                return true;
            } catch (IllegalStateException e) {
                // Ghi log lỗi nếu vẫn xảy ra để điều tra thêm
                Log.e("MainActivity", "Error committing fragment transaction", e);
            }
        }
        return false;
    }

    private void sendToLogin() {
        // FIX: Thêm kiểm tra an toàn trước khi chuyển Activity
        if (!isFinishing()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}
