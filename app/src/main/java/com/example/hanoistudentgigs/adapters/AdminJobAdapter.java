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
        holder.tvCompanyName.setText(job.getCompanyName());
        holder.tvJobTitle.setText(job.getTitle());

        // Set trạng thái nút Duyệt
        if (job.isApproved()) {
            holder.btnApproveJob.setVisibility(View.GONE);
        } else {
            holder.btnApproveJob.setVisibility(View.VISIBLE);
            holder.btnApproveJob.setText("Duyệt");
            holder.btnApproveJob.setEnabled(true);
        }

        // Xử lý sự kiện nút Duyệt
        holder.btnApproveJob.setOnClickListener(v -> {
            if (listener != null) {
                listener.onApprove(job);
            }
        });

        // Xử lý sự kiện nút Xem
        holder.btnViewJob.setOnClickListener(v -> {
            if (listener != null) {
                listener.onView(job);
            }
        });

        // Xử lý sự kiện nút Sửa
        holder.btnEditJob.setOnClickListener(v -> {
            if(listener != null) {
                listener.onEdit(job);
            }
        });

        // Xử lý sự kiện nút Xóa
        holder.btnDeleteJob.setOnClickListener(v -> {
            if(listener != null) {
                listener.onDelete(job);
            }
        });
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_job_card, parent, false);
        return new JobViewHolder(view);
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView tvCompanyName, tvJobTitle;
        Button btnApproveJob, btnViewJob, btnDeleteJob, btnEditJob;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
            btnApproveJob = itemView.findViewById(R.id.btnApproveJob);
            btnViewJob = itemView.findViewById(R.id.btnViewJob);
            btnDeleteJob = itemView.findViewById(R.id.btnDeleteJob);
            btnEditJob = itemView.findViewById(R.id.btnEditJob);
        }
    }
}