package com.example.hanoistudentgigs.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.utils.Constants;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminStatsFragment extends Fragment {
    private TextView textViewTotalJobs, textViewTotalApplications, textViewTotalStudents, textViewTotalEmployers;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_stats, container, false);

        // Ánh xạ các TextView từ layout
        textViewTotalJobs = view.findViewById(R.id.textViewTotalJobs);
        textViewTotalApplications = view.findViewById(R.id.textViewTotalApplications);
        textViewTotalStudents = view.findViewById(R.id.textViewTotalStudents);
        textViewTotalEmployers = view.findViewById(R.id.textViewTotalEmployers);

        // Bắt đầu tải dữ liệu thống kê
        loadStatistics();

        return view;
    }

    private void loadStatistics() {
        // Đếm tổng số tin đăng
        db.collection(Constants.JOBS_COLLECTION).get().addOnCompleteListener(task -> {
            if (isAdded() && getContext() != null && task.isSuccessful()) {
                textViewTotalJobs.setText("Tổng số tin đăng: " + task.getResult().size());
            }
        });

        // Đếm tổng số đơn ứng tuyển có status = Submitted
        db.collection(Constants.APPLICATIONS_COLLECTION)
            .whereEqualTo("status", "Submitted")
            .get().addOnCompleteListener(task -> {
            if (isAdded() && getContext() != null && task.isSuccessful()) {
                textViewTotalApplications.setText("Số lượng ứng tuyển: " + task.getResult().size());
            }
        });

        // Đếm tổng số sinh viên
        db.collection(Constants.USERS_COLLECTION).whereEqualTo("role", Constants.ROLE_STUDENT).get().addOnCompleteListener(task -> {
            if (isAdded() && getContext() != null && task.isSuccessful()) {
                textViewTotalStudents.setText("Tài khoản Student: " + task.getResult().size());
            }
        });

        // Đếm tổng số nhà tuyển dụng
        db.collection(Constants.USERS_COLLECTION).whereEqualTo("role", Constants.ROLE_EMPLOYER).get().addOnCompleteListener(task -> {
            if (isAdded() && getContext() != null && task.isSuccessful()) {
                textViewTotalEmployers.setText("Tài khoản Employer: " + task.getResult().size());
            }
        });
    }
}
