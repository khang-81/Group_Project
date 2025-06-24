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
import com.google.firebase.auth.FirebaseAuth;

// FIX: Đảm bảo class này kế thừa từ `androidx.fragment.app.Fragment`
public class AdminDashboardFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        Button btnManageUsers = view.findViewById(R.id.buttonManageUsers);
        Button btnManageCategories = view.findViewById(R.id.buttonManageCategories);
        Button btnApproveJobs = view.findViewById(R.id.buttonApproveJobs);
        Button btnViewStats = view.findViewById(R.id.buttonViewStats);
        Button btnLogout = view.findViewById(R.id.buttonAdminLogout);

//        btnManageUsers.setOnClickListener(v -> replaceFragment(new AdminManageUsersFragment()));
//        btnManageCategories.setOnClickListener(v -> replaceFragment(new AdminManageCategoriesFragment()));
        btnApproveJobs.setOnClickListener(v -> replaceFragment(new AdminApproveJobsFragment()));
        btnViewStats.setOnClickListener(v -> replaceFragment(new AdminStatsFragment()));

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        if (getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}
