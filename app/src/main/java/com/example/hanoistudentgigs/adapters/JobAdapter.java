package com.example.hanoistudentgigs.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.activities.PostJobActivity;
import com.example.hanoistudentgigs.models.Job;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    private final List<Job> jobList;
    private final Context context; // Thêm lại biến context
    private static final String TAG = "JobAdapter";

    // --- SỬA LỖI TẠI ĐÂY: Hàm khởi tạo chấp nhận cả Context và List<Job> ---
    public JobAdapter(Context context, List<Job> jobList) {
        this.context = context;
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng context đã được truyền vào
        View view = LayoutInflater.from(context).inflate(R.layout.item_job_posting, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        try {
            Job job = jobList.get(position);
            if (job == null) {
                Log.w(TAG, "Job object is null at position: " + position);
                return;
            }

            // Gán dữ liệu một cách an toàn
            holder.textViewJobTitle.setText(job.getTitle() != null ? job.getTitle() : "Không có tiêu đề");
            holder.textViewCompanyName.setText(job.getCompanyName() != null ? job.getCompanyName() : "Không có tên công ty");
            holder.textViewSalary.setText(job.getSalaryDescription() != null ? job.getSalaryDescription() : "Thỏa thuận");
            holder.textViewLocation.setText(job.getLocationName() != null ? job.getLocationName() : "Không có địa điểm");

            Glide.with(context)
                    .load(job.getCompanyLogoUrl())
                    .placeholder(R.drawable.logo_placeholder_background)
                    .error(R.drawable.logo_placeholder_background)
                    .into(holder.imageViewCompanyLogo);

            if ("Open".equalsIgnoreCase(job.getStatus())) {
                holder.viewStatusIndicator.setBackground(ContextCompat.getDrawable(context, R.drawable.status_indicator_open));
            } else {
                holder.viewStatusIndicator.setBackground(ContextCompat.getDrawable(context, R.drawable.status_indicator_closed));
            }
        } catch (Exception e) {
            // Bắt tất cả các lỗi có thể xảy ra để ứng dụng không bị crash
            // Ghi lại lỗi để bạn có thể xem trong Logcat
            Log.e(TAG, "Error binding view at position " + position, e);
        }
    }

    @Override
    public int getItemCount() {
        return jobList != null ? jobList.size() : 0;
    }

    class JobViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCompanyLogo;
        TextView textViewJobTitle, textViewCompanyName, textViewSalary, textViewLocation;
        View viewStatusIndicator;
        TextView buttonEdit, buttonDelete;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCompanyLogo = itemView.findViewById(R.id.imageViewCompanyLogo);
            textViewJobTitle = itemView.findViewById(R.id.textViewJobTitle);
            textViewCompanyName = itemView.findViewById(R.id.textViewCompanyName);
            textViewSalary = itemView.findViewById(R.id.textViewSalary);
            textViewLocation = itemView.findViewById(R.id.textViewLocation);
            viewStatusIndicator = itemView.findViewById(R.id.viewStatusIndicator);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);

            buttonEdit.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Job job = jobList.get(position);
                    if (job != null && job.getId() != null) {
                        Intent intent = new Intent(context, PostJobActivity.class);
                        intent.putExtra("EDIT_JOB_ID", job.getId());
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Không thể sửa tin này do thiếu ID.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            buttonDelete.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Job job = jobList.get(position);
                    if (job != null && job.getId() != null) {
                        showDeleteConfirmationDialog(job, position);
                    }
                }
            });
        }

        private void showDeleteConfirmationDialog(Job job, int position) {
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa tin tuyển dụng '" + job.getTitle() + "' không?")
                    .setPositiveButton("Xóa", (dialog, which) -> deleteJobFromFirestore(job, position))
                    .setNegativeButton("Hủy", null)
                    .show();
        }

        private void deleteJobFromFirestore(Job job, int position) {
            FirebaseFirestore.getInstance().collection("jobs").document(job.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Đã xóa tin thành công.", Toast.LENGTH_SHORT).show();
                        if (jobList != null && position < jobList.size()) {
                            jobList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, jobList.size());
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Xóa tin thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
