package com.example.hanoistudentgigs.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hanoistudentgigs.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminStatsFragment extends Fragment {
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_stats, container, false);
        TextView tvTotalJobs = view.findViewById(R.id.textViewTotalJobs);
        TextView tvTotalApplications = view.findViewById(R.id.textViewTotalApplications);
        TextView tvTotalStudents = view.findViewById(R.id.textViewTotalStudents);
        TextView tvTotalEmployers = view.findViewById(R.id.textViewTotalEmployers);

        db = FirebaseFirestore.getInstance();

        // Tổng số tin đăng (jobs)
        db.collection("jobs").get().addOnSuccessListener(snapshot -> {
            int count = snapshot.size();
            tvTotalJobs.setText("Tổng số tin đăng: " + count);
        });
        // Tổng số ứng tuyển (applications)
        db.collection("jobs").get().addOnSuccessListener(snapshot -> {
            final int[] totalApplications = {0};
            if (!snapshot.isEmpty()) {
                for (com.google.firebase.firestore.DocumentSnapshot jobDoc : snapshot.getDocuments()) {
                    db.collection("jobs").document(jobDoc.getId()).collection("applications").get().addOnSuccessListener(appSnap -> {
                        totalApplications[0] += appSnap.size();
                        tvTotalApplications.setText("Số lượng ứng tuyển: " + totalApplications[0]);
                    });
                }
            } else {
                tvTotalApplications.setText("Số lượng ứng tuyển: 0");
            }
        });
        // Tổng số tài khoản student
        db.collection("users").whereEqualTo("role", "STUDENT").get().addOnSuccessListener(snapshot -> {
            int count = snapshot.size();
            tvTotalStudents.setText("Tài khoản Student: " + count);
        });
        // Tổng số tài khoản employer
        db.collection("users").whereEqualTo("role", "EMPLOYER").get().addOnSuccessListener(snapshot -> {
            int count = snapshot.size();
            tvTotalEmployers.setText("Tài khoản Employer: " + count);
        });

        return view;
    }
}
