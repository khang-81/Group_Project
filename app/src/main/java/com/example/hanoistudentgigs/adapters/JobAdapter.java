package com.example.hanoistudentgigs.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.activities.JobDetailActivity;
import com.example.hanoistudentgigs.models.Job;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class JobAdapter extends FirestoreRecyclerAdapter<Job, JobAdapter.JobViewHolder> {

    private final Context context;

    public JobAdapter(@NonNull FirestoreRecyclerOptions<Job> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull JobViewHolder holder, int position, @NonNull Job model) {
        // Lấy ID từ document Firestore
        String jobId = getSnapshots().getSnapshot(position).getId();

        // Gán dữ liệu vào view
        holder.bind(model);

        // Chuyển sang màn chi tiết khi bấm
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, JobDetailActivity.class);
            intent.putExtra("JOB_ID", jobId);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_card, parent, false);
        return new JobViewHolder(view);
    }

    // Cho phép lớp này được dùng ngoài JobAdapter
    public static class JobViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewJobTitle, textViewCompanyName, textViewSalary, textViewLocation, textViewStatus;
        private final ImageView imageViewCompanyLogo;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewJobTitle = itemView.findViewById(R.id.textViewJobTitle);
            textViewCompanyName = itemView.findViewById(R.id.textViewCompanyName);
            textViewSalary = itemView.findViewById(R.id.textViewSalary);
            textViewLocation = itemView.findViewById(R.id.textViewLocation);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            imageViewCompanyLogo = itemView.findViewById(R.id.imageViewCompanyLogo);
        }

        public void bind(Job job) {
            textViewJobTitle.setText(job.getTitle());
            textViewCompanyName.setText(job.getCompanyName());
            textViewSalary.setText(job.getSalaryDescription());
            textViewLocation.setText(job.getLocationName());

            // Hiển thị trạng thái tuyển dụng
            String status = job.getStatus();
            if (status != null) {
                textViewStatus.setText(status);
                switch (status.toLowerCase()) {
                    case "đang tuyển":
                        textViewStatus.setTextColor(Color.parseColor("#4CAF50")); // xanh lá
                        break;
                    case "đã đóng":
                    case "đã tuyển":
                        textViewStatus.setTextColor(Color.RED);
                        break;
                    default:
                        textViewStatus.setTextColor(Color.GRAY);
                        break;
                }
            }

            // Load logo công ty bằng Picasso
            String logoUrl = job.getCompanyLogoUrl();
            if (logoUrl != null && !logoUrl.isEmpty()) {
                Picasso.get()
                        .load(logoUrl)
                        .placeholder(R.drawable.default_company_logo_placeholder)
                        .error(R.drawable.error_company_logo_placeholder)
                        .into(imageViewCompanyLogo);
            } else {
                imageViewCompanyLogo.setImageResource(R.drawable.default_company_logo_placeholder);
            }
        }
    }
}
