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
        // Tổng số ứng tuyển (applications) có status = 'Submitted'
        db.collection("applications")
          .whereEqualTo("status", "Submitted")
          .get()
          .addOnSuccessListener(snapshot -> {
              int count = snapshot.size();
              tvTotalApplications.setText("Số lượng ứng tuyển: " + count);
          })
          .addOnFailureListener(e -> {
              tvTotalApplications.setText("Số lượng ứng tuyển: ...");
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
