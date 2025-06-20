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
    }

    public AdminJobAdapter(@NonNull FirestoreRecyclerOptions<Job> options, Context context, OnJobActionListener listener) {
        super(options);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull JobViewHolder holder, int position, @NonNull Job job) {
        holder.tvCompanyName.setText(job.getCompanyName());
        holder.tvPostedDate.setText("Ngày đăng: " + job.getPostedDate());

        // Set trạng thái nút Duyệt
        if (job.isApproved()) {
            holder.btnApproveJob.setEnabled(false);
            holder.btnApproveJob.setText("Đã duyệt");
        } else {
            holder.btnApproveJob.setEnabled(true);
            holder.btnApproveJob.setText("Duyệt");
        }


        holder.btnApproveJob.setOnClickListener(v -> {
            if (!job.isApproved()) {
                new AlertDialog.Builder(context)
                        .setTitle("Xác nhận duyệt")
                        .setMessage("Bạn có chắc chắn muốn duyệt tin đăng này?")
                        .setPositiveButton("Duyệt", (dialog, which) -> {
                            db.collection(Constants.JOBS_COLLECTION)
                                    .document(job.getId())
                                    .update("approved", true)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Đã duyệt tin đăng!", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(context, "Lỗi khi duyệt tin: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                    );
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });

        // Xử lý sự kiện nút Xem
        holder.btnViewJob.setOnClickListener(v -> {
            if (listener != null) {
                listener.onView(job);
            }
        });

        // Xử lý sự kiện nút Xóa
        holder.btnDeleteJob.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa tin đăng này?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        db.collection(Constants.JOBS_COLLECTION)
                                .document(job.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Đã xóa tin đăng!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Lỗi khi xóa tin: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
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
        Button btnApproveJob, btnViewJob, btnDeleteJob;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            tvPostedDate = itemView.findViewById(R.id.tvPostedDate);
            btnApproveJob = itemView.findViewById(R.id.btnApproveJob);
            btnViewJob = itemView.findViewById(R.id.btnViewJob);
            btnDeleteJob = itemView.findViewById(R.id.btnDeleteJob);
        }
    }
}