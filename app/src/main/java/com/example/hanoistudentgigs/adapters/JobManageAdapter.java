package com.example.hanoistudentgigs.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.activities.SuaTinActivity;
import com.example.hanoistudentgigs.activities.XemTinActivity;
import com.example.hanoistudentgigs.models.Job;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class JobManageAdapter extends FirestoreRecyclerAdapter<Job, JobManageAdapter.JobViewHolder> {

    private final Context context;

    public JobManageAdapter(@NonNull FirestoreRecyclerOptions<Job> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull JobViewHolder holder, int position, @NonNull Job job) {
        holder.txtJobTitle.setText(job.getTitle());
        holder.txtCompany.setText(job.getCompanyName());
        holder.txtSalary.setText(job.getSalaryDescription());
        holder.txtLocation.setText(job.getLocationName());


        holder.btnDelete.setOnClickListener(v -> {
            getSnapshots().getSnapshot(position).getReference().delete()
                    .addOnSuccessListener(unused ->
                            Toast.makeText(context, "Đã xoá công việc", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Lỗi khi xoá", Toast.LENGTH_SHORT).show());
        });

        holder.btnEdit.setOnClickListener(v -> {
            String jobId = getSnapshots().getSnapshot(position).getId();
            Intent intent = new Intent(v.getContext(), SuaTinActivity.class);
            intent.putExtra("JOB_ID", jobId);
            v.getContext().startActivity(intent);
        });

        holder.btnView.setOnClickListener(v -> {
            Intent intent = new Intent(context, XemTinActivity.class);
            intent.putExtra("title", job.getTitle());
            intent.putExtra("company", job.getCompanyName());
            intent.putExtra("salary", job.getSalaryDescription());
            intent.putExtra("location", job.getLocationName());
            intent.putExtra("contact", job.getContact());
            intent.putExtra("description", job.getDescription());
            context.startActivity(intent);
        });


    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView txtJobTitle, txtCompany, txtSalary, txtLocation;
        ImageView btnEdit, btnDelete, btnView;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            txtJobTitle = itemView.findViewById(R.id.txtJobTitle);
            txtCompany = itemView.findViewById(R.id.txtCompany);
            txtSalary = itemView.findViewById(R.id.txtSalary);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnView = itemView.findViewById(R.id.btnView);
        }
    }
}
