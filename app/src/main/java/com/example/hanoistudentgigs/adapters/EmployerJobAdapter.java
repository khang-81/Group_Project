package com.example.hanoistudentgigs.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.activities.PostJobActivity;
import com.example.hanoistudentgigs.models.Job;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

public class EmployerJobAdapter extends FirestoreRecyclerAdapter<Job, EmployerJobAdapter.JobViewHolder> {

    public EmployerJobAdapter(@NonNull FirestoreRecyclerOptions<Job> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull JobViewHolder holder, int position, @NonNull Job model) {
        holder.bind(model);
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employer_job, parent, false);
        return new JobViewHolder(view);
    }

    class JobViewHolder extends RecyclerView.ViewHolder {
        TextView textViewJobTitle, textViewJobLocation, textViewJobSalary;
        Button buttonEdit, buttonToggleStatus, buttonViewApplicants;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewJobTitle = itemView.findViewById(R.id.textViewJobTitle);
            textViewJobLocation = itemView.findViewById(R.id.textViewJobLocation);
            textViewJobSalary = itemView.findViewById(R.id.textViewJobSalary);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonToggleStatus = itemView.findViewById(R.id.buttonToggleStatus);
            buttonViewApplicants = itemView.findViewById(R.id.buttonViewApplicants);
        }

        public void bind(Job job) {
            textViewJobTitle.setText(job.getTitle());
            textViewJobLocation.setText(job.getLocationName());
            textViewJobSalary.setText(job.getSalaryDescription());

            // Cập nhật nút Đóng/Mở tin
            boolean isOpen = "Open".equalsIgnoreCase(job.getStatus());
            buttonToggleStatus.setText(isOpen ? "Đóng tin" : "Mở lại");

            // Xử lý sự kiện click
            buttonEdit.setOnClickListener(v -> {
                Context context = itemView.getContext();
                Intent intent = new Intent(context, PostJobActivity.class);
                intent.putExtra("EDIT_JOB_ID", job.getId());
                context.startActivity(intent);
            });

            buttonToggleStatus.setOnClickListener(v -> {
                String newStatus = isOpen ? "Closed" : "Open";
                FirebaseFirestore.getInstance().collection("jobs").document(job.getId())
                        .update("status", newStatus)
                        .addOnSuccessListener(aVoid -> Toast.makeText(itemView.getContext(), "Đã cập nhật trạng thái.", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(itemView.getContext(), "Lỗi cập nhật.", Toast.LENGTH_SHORT).show());
            });

            buttonViewApplicants.setOnClickListener(v-> {
                // TODO: Mở màn hình xem danh sách ứng viên cho công việc này
                Toast.makeText(itemView.getContext(), "Xem ứng viên cho: " + job.getTitle(), Toast.LENGTH_SHORT).show();
            });
        }
    }
}
