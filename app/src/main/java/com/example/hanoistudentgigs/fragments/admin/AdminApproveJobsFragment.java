package com.example.hanoistudentgigs.fragments.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.activities.JobDetailActivity;
import com.example.hanoistudentgigs.adapters.AdminJobAdapter;
import com.example.hanoistudentgigs.models.Job;
import com.example.hanoistudentgigs.utils.Constants;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AdminApproveJobsFragment extends Fragment {
    private RecyclerView rvJobList;
    private EditText etSearchJob;
    private AdminJobAdapter jobAdapter;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_approve_jobs, container, false);
//        rvJobList = view.findViewById(R.id.rvJobList);
//        etSearchJob = view.findViewById(R.id.etSearchJob);
        db = FirebaseFirestore.getInstance();

        setupRecyclerView(""); // Initial load

        etSearchJob.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setupRecyclerView(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void setupRecyclerView(String searchText) {
        Query query = db.collection(Constants.JOBS_COLLECTION);

        if (searchText != null && !searchText.trim().isEmpty()) {
            query = query.orderBy("companyName").startAt(searchText).endAt(searchText + '\uf8ff');
        }

        FirestoreRecyclerOptions<Job> options = new FirestoreRecyclerOptions.Builder<Job>()
                .setQuery(query, Job.class)
                .build();

        if (jobAdapter != null) {
            jobAdapter.stopListening();
        }

        AdminJobAdapter.OnJobActionListener listener = new AdminJobAdapter.OnJobActionListener() {
            @Override
            public void onApprove(Job job) {
                showApproveDialog(job);
            }

            @Override
            public void onView(Job job) {
                Intent intent = new Intent(getContext(), JobDetailActivity.class);
                intent.putExtra("JOB_ID", job.getId());
                intent.putExtra("IS_ADMIN", true);
                startActivity(intent);
            }

            @Override
            public void onDelete(Job job) {
                showDeleteDialog(job);
            }
        };

        jobAdapter = new AdminJobAdapter(options, getContext(), listener);
        rvJobList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvJobList.setAdapter(jobAdapter);
        jobAdapter.startListening();
    }

    private void showApproveDialog(Job job) {
        new AlertDialog.Builder(getContext())
                .setTitle("Duyệt tin đăng")
                .setMessage("Bạn có chắc chắn muốn duyệt công việc: " + job.getTitle() + "?")
                .setPositiveButton("Duyệt", (dialog, which) -> {
                    db.collection(Constants.JOBS_COLLECTION).document(job.getId())
                            .update("approved", true)
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Đã duyệt tin thành công!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi duyệt tin.", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showDeleteDialog(Job job) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa tin đăng")
                .setMessage("Bạn có chắc chắn muốn xóa công việc: " + job.getTitle() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    db.collection(Constants.JOBS_COLLECTION).document(job.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Đã xóa tin thành công!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi xóa tin.", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (jobAdapter != null) {
            jobAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (jobAdapter != null) {
            jobAdapter.stopListening();
        }
    }
} 