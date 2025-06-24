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
    private RecyclerView recyclerViewApproveJobs;
    private EditText editTextSearchJob;

    private AdminJobAdapter jobAdapter;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_approve_jobs, container, false);
        editTextSearchJob = view.findViewById(R.id.editTextSearchJob);
        recyclerViewApproveJobs = view.findViewById(R.id.recyclerViewApproveJobs);

        db = FirebaseFirestore.getInstance();

        setupRecyclerView(""); // Initial load

        editTextSearchJob.addTextChangedListener(new TextWatcher() {
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

        jobAdapter = new AdminJobAdapter(options, getContext(), new AdminJobAdapter.OnJobActionListener() {
            @Override
            public void onApprove(Job job) { showApproveDialog(job); }
            @Override
            public void onView(Job job) { showEditJobDialog(job); }
            @Override
            public void onDelete(Job job) { showDeleteDialog(job); }
            @Override
            public void onEdit(Job job) { showEditJobDialog(job); }
        });
        recyclerViewApproveJobs.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewApproveJobs.setAdapter(jobAdapter);
        jobAdapter.startListening();
    }

    private void showApproveDialog(Job job) {
        new AlertDialog.Builder(getContext())
                .setTitle("Duyệt tin đăng")
                .setMessage("Bạn có chắc chắn muốn duyệt công việc: " + job.getTitle() + "?")
                .setPositiveButton("Duyệt", (dialog, which) -> {
                    db.collection(Constants.JOBS_COLLECTION).document(job.getFireStoreId())
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
                    db.collection(Constants.JOBS_COLLECTION).document(job.getFireStoreId())
                            .delete()
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Đã xóa tin thành công!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi xóa tin.", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showEditJobDialog(Job job) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sửa tin đăng");
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_job, null, false);
        builder.setView(dialogView);
        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etCompanyName = dialogView.findViewById(R.id.etCompanyName);
        EditText etLocation = dialogView.findViewById(R.id.etLocation);
        EditText etSalary = dialogView.findViewById(R.id.etSalary);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        etTitle.setText(job.getTitle());
        etCompanyName.setText(job.getCompanyName());
        etLocation.setText(job.getLocationName());
        etSalary.setText(job.getSalaryDescription());
        etDescription.setText(job.getDescription());
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String companyName = etCompanyName.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String salary = etSalary.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            if (title.isEmpty() || companyName.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đủ tiêu đề và tên công ty", Toast.LENGTH_SHORT).show();
                return;
            }
            db.collection(Constants.JOBS_COLLECTION).document(job.getFireStoreId())
                .update(
                    "title", title,
                    "companyName", companyName,
                    "locationName", location,
                    "salaryDescription", salary,
                    "description", description
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Đã cập nhật tin đăng!", Toast.LENGTH_SHORT).show();
                    setupRecyclerView("");
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
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