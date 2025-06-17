package com.example.hanoistudentgigs.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.activities.JobDetailActivity;
import com.example.hanoistudentgigs.models.Job;
import com.squareup.picasso.Picasso;

public class FeaturedJobAdapter extends FirestoreRecyclerAdapter<Job, FeaturedJobAdapter.FeaturedJobViewHolder> {
    private Context context;

    public FeaturedJobAdapter(@NonNull FirestoreRecyclerOptions<Job> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull FeaturedJobViewHolder holder, int position, @NonNull Job model) {
        // Lấy ID của document từ snapshot
        String jobId = getSnapshots().getSnapshot(position).getId();
        // Gán ID vào model nếu Job class có setter cho ID
        model.setId(jobId); // Đảm bảo Job model của bạn có setId(String id)

        holder.bind(model);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, JobDetailActivity.class);
            intent.putExtra("JOB_ID", model.getId());
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public FeaturedJobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_featured_job_card, parent, false);
        return new FeaturedJobViewHolder(view);
    }

    public static class FeaturedJobViewHolder extends RecyclerView.ViewHolder {
        TextView textViewJobTitle, textViewCompanyName, textViewSalary, textViewLocation;
        ImageView imageViewCompanyLogo;

        public FeaturedJobViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewJobTitle = itemView.findViewById(R.id.textViewJobTitle);
            textViewCompanyName = itemView.findViewById(R.id.textViewCompanyName);
            textViewSalary = itemView.findViewById(R.id.textViewSalary);
            textViewLocation = itemView.findViewById(R.id.textViewLocation);
            imageViewCompanyLogo = itemView.findViewById(R.id.imageViewCompanyLogo);
        }

        public void bind(Job job) {
            textViewJobTitle.setText(job.getTitle());
            textViewCompanyName.setText(job.getCompanyName());
            textViewSalary.setText(job.getSalaryDescription());
            textViewLocation.setText(job.getLocationName());
            // Trong FeaturedJobViewHolder.bind(Job job)
            if (job.getCompanyLogoUrl() != null && !job.getCompanyLogoUrl().isEmpty()) {
                Picasso.get().load(job.getCompanyLogoUrl()).into(imageViewCompanyLogo);
            } else {
                // Cung cấp một ảnh mặc định nếu không có logo hoặc URL trống
                imageViewCompanyLogo.setImageResource(R.drawable.ic_default_company_logo); // Bạn cần tạo drawable này
            }
        }
    }
}
