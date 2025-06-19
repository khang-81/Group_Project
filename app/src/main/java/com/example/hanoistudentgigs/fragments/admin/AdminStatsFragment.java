package com.example.hanoistudentgigs.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hanoistudentgigs.R;

public class AdminStatsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_stats, container, false);
        Button btnTotalJobs = view.findViewById(R.id.btnTotalJobs);
        Button btnTotalApplications = view.findViewById(R.id.btnTotalApplications);
        Button btnTotalStudents = view.findViewById(R.id.btnTotalStudents);
        Button btnTotalEmployers = view.findViewById(R.id.btnTotalEmployers);

        // Dummy data
        btnTotalJobs.setText("Tổng số tin đăng: 245");
        btnTotalApplications.setText("Số lượng ứng tuyển: 789");
        btnTotalStudents.setText("Tài khoản Student: 1340");
        btnTotalEmployers.setText("Tài khoản Employer: 256");

        return view;
    }
}
