package com.example.hanoistudentgigs.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.models.Job;
import com.example.hanoistudentgigs.utils.Constants;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AdminJobAdapter extends FirestoreRecyclerAdapter<Job, AdminJobAdapter.JobViewHolder> {
    private Context context;
    private OnJobActionListener listener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface OnJobActionListener {
        void onApprove(Job job);
        void onView(Job job);
        void onDelete(Job job);
        void onEdit(Job job);
    }

    public AdminJobAdapter(@NonNull FirestoreRecyclerOptions<Job> options, Context context, OnJobActionListener listener) {
        super(options);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull JobViewHolder holder, int position, @NonNull Job job) {
        // Lấy id Firestore thực sự
        String fireStoreId = getSnapshots().getSnapshot(position).getId();
        job.setFireStoreId(fireStoreId);
        holder.tvCompanyName.setText(job.getCompanyName());
        holder.tvPostedDate.setText("Ngày đăng: " + job.getPostedDate());

        // Chỉ set màu động cho btnApproveJob
        if (job.isApproved()) {
            holder.btnApproveJob.setText("ĐÃ DUYỆT");
            holder.btnApproveJob.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFBDBDBD));
            holder.btnApproveJob.setEnabled(false);
        } else {
            holder.btnApproveJob.setText("DUYỆT");
            holder.btnApproveJob.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
            holder.btnApproveJob.setEnabled(true);
        }

        holder.btnApproveJob.setOnClickListener(v -> {
            if (!job.isApproved() && listener != null) listener.onApprove(job);
        });

        // Xử lý sự kiện nút Xóa
        holder.btnDeleteJob.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(job);
        });

        holder.btnViewJob.setOnClickListener(v -> {
            if (listener != null) listener.onView(job);
        });

        holder.btnEditJob.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(job);

        });
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_job_card, parent, false);
        return new JobViewHolder(view);
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView tvCompanyName, tvPostedDate;

        Button btnApproveJob, btnDeleteJob, btnViewJob, btnEditJob;


        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            tvPostedDate = itemView.findViewById(R.id.tvPostedDate);
            btnApproveJob = itemView.findViewById(R.id.btnApproveJob);
            btnDeleteJob = itemView.findViewById(R.id.btnDeleteJob);
            btnViewJob = itemView.findViewById(R.id.btnViewJob);
            btnEditJob = itemView.findViewById(R.id.btnEditJob);

        }
    }
}