package com.example.hanoistudentgigs.fragments.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.activities.LoginActivity;
import com.example.hanoistudentgigs.fragments.admin.AdminApproveJobsFragment;
import com.google.firebase.auth.FirebaseAuth;

// FIX: Đảm bảo class này kế thừa từ `androidx.fragment.app.Fragment`
public class AdminDashboardFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        Button btnManageUsers = view.findViewById(R.id.btnManageUsers);
        Button btnManageCategories = view.findViewById(R.id.btnManageCategories);
        Button btnApproveJobs = view.findViewById(R.id.btnApproveJobs);
        Button btnSystemStats = view.findViewById(R.id.btnSystemStats);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        btnManageUsers.setOnClickListener(v -> navigateTo(new AdminManageUsersFragment()));
        btnManageCategories.setOnClickListener(v -> navigateTo(new AdminManageCategoriesFragment()));
        btnApproveJobs.setOnClickListener(v -> navigateTo(new AdminApproveJobsFragment()));
        btnSystemStats.setOnClickListener(v -> navigateTo(new AdminStatsFragment()));
        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void navigateTo(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
