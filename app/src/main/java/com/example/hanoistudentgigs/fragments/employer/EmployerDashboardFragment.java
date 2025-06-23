package com.example.hanoistudentgigs.fragments.employer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // <-- Import Log
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.activities.PostJobActivity;
import com.example.hanoistudentgigs.adapters.JobAdapter;
import com.example.hanoistudentgigs.models.Job;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EmployerDashboardFragment extends Fragment {

    // Thêm một Tag để lọc log cho dễ
    private static final String TAG = "EmployerDashboard";

    private RecyclerView recyclerViewJobs;
    private JobAdapter jobAdapter;
    private List<Job> jobList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private ProgressBar progressBar;
    private TextView textViewNoJobs;
    private FloatingActionButton fabAddJob;

    public EmployerDashboardFragment() {
        // Constructor rỗng là bắt buộc
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_employer_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewJobs = view.findViewById(R.id.recyclerViewJobs);
        progressBar = view.findViewById(R.id.progressBar);
        textViewNoJobs = view.findViewById(R.id.textViewNoJobs);
        fabAddJob = view.findViewById(R.id.fab_add_job);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerViewJobs.setLayoutManager(new LinearLayoutManager(getContext()));
        jobList = new ArrayList<>();
        jobAdapter = new JobAdapter(getContext(), jobList);
        recyclerViewJobs.setAdapter(jobAdapter);

        fabAddJob.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PostJobActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadJobsFromFirestore();
    }

    private void loadJobsFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);
        textViewNoJobs.setVisibility(View.GONE);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        String employerId = currentUser.getUid();

        // --- BƯỚC 1: LOG LẠI UID CỦA BẠN ---
        // In ra UID của người dùng đang đăng nhập để bạn có thể sao chép và kiểm tra trên Firebase.
        Log.d(TAG, "Đang truy vấn jobs cho Employer UID: " + employerId);

        db.collection("jobs")
                .whereEqualTo("employerUid", employerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (getActivity() == null || !isAdded()) {
                        return;
                    }
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        QuerySnapshot documents = task.getResult();

                        // --- BƯỚC 2: LOG LẠI SỐ LƯỢNG KẾT QUẢ ---
                        // Dòng này sẽ cho bạn biết chính xác truy vấn đã tìm thấy bao nhiêu tin đăng.
                        Log.d(TAG, "Truy vấn thành công! Tìm thấy " + documents.size() + " tin đăng.");

                        jobList.clear();
                        jobList.addAll(documents.toObjects(Job.class)); // Cách thêm dữ liệu nhanh hơn

                        if (jobList.isEmpty()) {
                            // Nếu không có tin nào, hiển thị thông báo.
                            textViewNoJobs.setVisibility(View.VISIBLE);
                        } else {
                            textViewNoJobs.setVisibility(View.GONE);
                        }

                        jobAdapter.notifyDataSetChanged();
                    } else {
                        // --- BƯỚC 3: LOG LẠI LỖI CỤ THỂ ---
                        // Nếu có lỗi, dòng này sẽ in ra nguyên nhân chính xác (ví dụ: thiếu index, không có quyền,...)
                        Log.e(TAG, "Lỗi khi tải dữ liệu jobs: ", task.getException());
                        Toast.makeText(getContext(), "Lỗi khi tải dữ liệu. Vui lòng kiểm tra Logcat.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
