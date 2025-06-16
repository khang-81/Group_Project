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

public class AdminJobAdapter extends FirestoreRecyclerAdapter<Job, AdminJobAdapter.AdminJobViewHolder> {
    private Context context;

    public AdminJobAdapter(@NonNull FirestoreRecyclerOptions<Job> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull AdminJobViewHolder holder, int position, @NonNull Job model) {
        holder.bind(model);

        // Xử lý sự kiện cho nút "Duyệt"
        holder.buttonApprove.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận duyệt")
                    .setMessage("Bạn có chắc chắn muốn duyệt tin đăng này?")
                    .setPositiveButton("Duyệt", (dialog, which) -> {
                        FirebaseFirestore.getInstance().collection(Constants.JOBS_COLLECTION)
                                .document(model.getId())
                                .update("approved", true)
                                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Đã duyệt tin.", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(context, "Có lỗi xảy ra.", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        // Xử lý sự kiện cho nút "Xóa"
        holder.buttonDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa vĩnh viễn tin đăng này?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        FirebaseFirestore.getInstance().collection(Constants.JOBS_COLLECTION)
                                .document(model.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Đã xóa tin.", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(context, "Có lỗi xảy ra.", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    @NonNull
    @Override
    public AdminJobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_job_card, parent, false);
        return new AdminJobViewHolder(view);
    }

    public static class AdminJobViewHolder extends RecyclerView.ViewHolder {
        TextView textViewJobTitle, textViewCompanyName, textViewDescription;
        Button buttonApprove, buttonDelete;

        public AdminJobViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewJobTitle = itemView.findViewById(R.id.textViewAdminJobTitle);
            textViewCompanyName = itemView.findViewById(R.id.textViewAdminCompanyName);
            textViewDescription = itemView.findViewById(R.id.textViewAdminJobDescription);
            buttonApprove = itemView.findViewById(R.id.buttonApprove);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }

        public void bind(Job job) {
            textViewJobTitle.setText(job.getTitle());
            textViewCompanyName.setText(job.getCompanyName());
            textViewDescription.setText(job.getDescription());
        }
    }
}