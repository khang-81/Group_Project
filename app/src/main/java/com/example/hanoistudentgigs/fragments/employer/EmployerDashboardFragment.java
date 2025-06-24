package com.example.hanoistudentgigs.fragments.employer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.activities.PostJobActivity;
import com.example.hanoistudentgigs.adapters.EmployerJobAdapter;
import com.example.hanoistudentgigs.models.Job;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class EmployerDashboardFragment extends Fragment {
    private static final String TAG = "EmployerDashboard"; // Tag để lọc Logcat

    private RecyclerView recyclerViewOpenJobs, recyclerViewClosedJobs;
    private EmployerJobAdapter openJobsAdapter, closedJobsAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FloatingActionButton fabAddJob;
    private ProgressBar progressBar;
    private TextView textViewNoOpenJobs, textViewNoClosedJobs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_employer_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerViewOpenJobs = view.findViewById(R.id.recyclerViewOpenJobs);
        recyclerViewClosedJobs = view.findViewById(R.id.recyclerViewClosedJobs);
        fabAddJob = view.findViewById(R.id.fab_add_job);
        progressBar = view.findViewById(R.id.progressBar);
        textViewNoOpenJobs = view.findViewById(R.id.textViewNoOpenJobs);
        textViewNoClosedJobs = view.findViewById(R.id.textViewNoClosedJobs);

        setupRecyclerViews();

        fabAddJob.setOnClickListener(v -> {
            if (getActivity() != null) {
                startActivity(new Intent(getActivity(), PostJobActivity.class));
            }
        });
    }

    private void setupRecyclerViews() {
        if (mAuth.getCurrentUser() == null) {
            return; // Chưa đăng nhập, không làm gì cả
        }
        String employerUid = mAuth.getCurrentUser().getUid();

        /*
         * =====================================================================================
         * LƯU Ý QUAN TRỌNG VỀ LỖI TẢI DỮ LIỆU VÀ INDEX
         *
         * Truy vấn dưới đây (kết hợp `whereEqualTo` và `orderBy`) là một truy vấn phức hợp.
         * Firestore YÊU CẦU một CHỈ MỤC (INDEX) để thực hiện nó.
         *
         * NẾU BẠN VẪN THẤY LỖI "KHÔNG CÓ VIỆC LÀM NÀO":
         * 1. Mở cửa sổ "Logcat" trong Android Studio.
         * 2. Chạy lại ứng dụng.
         * 3. Bạn sẽ thấy một thông báo lỗi MÀU ĐỎ chứa một ĐƯỜNG LINK dài.
         * 4. HÃY NHẤN VÀO ĐƯỜNG LINK ĐÓ.
         * 5. Trình duyệt sẽ mở ra trang Firebase Console để tạo chỉ mục tự động.
         * 6. Nhấn "Save" hoặc "Create" và đợi vài phút cho Firebase xây dựng xong chỉ mục.
         * 7. Chạy lại ứng dụng, dữ liệu sẽ được hiển thị.
         * =====================================================================================
         */

        // --- FIX LỖI #1: Sửa lại giá trị status trong truy vấn ---
        // Query cho các công việc đang tuyển ("Đang tuyển")
        Query openJobsQuery = db.collection("jobs")
                .whereEqualTo("employerUid", employerUid)
                .whereEqualTo("status", "Đang tuyển") // <-- ĐÃ SỬA
                .orderBy("createdAt", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Job> openOptions = new FirestoreRecyclerOptions.Builder<Job>()
                .setQuery(openJobsQuery, Job.class)
                .setLifecycleOwner(this)
                .build();

        openJobsAdapter = new EmployerJobAdapter(openOptions) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (textViewNoOpenJobs != null) {
                    textViewNoOpenJobs.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
                }
            }
        };
        recyclerViewOpenJobs.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewOpenJobs.setAdapter(openJobsAdapter);


        // Query cho các công việc đã đóng ("Đã đóng")
        Query closedJobsQuery = db.collection("jobs")
                .whereEqualTo("employerUid", employerUid)
                .whereEqualTo("status", "Đã đóng") // <-- ĐÃ SỬA
                .orderBy("createdAt", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Job> closedOptions = new FirestoreRecyclerOptions.Builder<Job>()
                .setQuery(closedJobsQuery, Job.class)
                .setLifecycleOwner(this)
                .build();

        closedJobsAdapter = new EmployerJobAdapter(closedOptions) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if(textViewNoClosedJobs != null) {
                    textViewNoClosedJobs.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
                }
            }
        };
        recyclerViewClosedJobs.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewClosedJobs.setAdapter(closedJobsAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (openJobsAdapter != null) openJobsAdapter.startListening();
        if (closedJobsAdapter != null) closedJobsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (openJobsAdapter != null) openJobsAdapter.stopListening();
        if (closedJobsAdapter != null) closedJobsAdapter.stopListening();
    }
}
