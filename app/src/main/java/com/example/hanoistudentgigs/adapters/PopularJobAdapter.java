package com.example.hanoistudentgigs.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

public class PopularJobAdapter extends FirestoreRecyclerAdapter<Job, PopularJobAdapter.PopularJobViewHolder> {
    private Context context;

    public PopularJobAdapter(@NonNull FirestoreRecyclerOptions<Job> options, Context context) {
        super(options);
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    protected void onBindViewHolder(@NonNull PopularJobViewHolder holder, int position, @NonNull Job model) {
        String jobId = getSnapshots().getSnapshot(position).getId(); // Lấy ID của tài liệu
        model.setId(jobId); // Gán ID vào đối tượng Job của bạn

        holder.bind(model);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, JobDetailActivity.class);
            intent.putExtra("JOB_ID", model.getId());
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public PopularJobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_popular_job_card, parent, false);
        return new PopularJobViewHolder(view);
    }
    @Override
    public long getItemId(int position) {
        // Đây là cách lấy ID duy nhất của tài liệu Firestore
        // FirestoreRecyclerAdapter có một phương thức getSnapshots() để truy cập DocumentSnapshots
        // Mỗi DocumentSnapshot có một ID duy nhất
        return getSnapshots().getSnapshot(position).getId().hashCode();
        // Hoặc nếu bạn có một trường 'jobId' trong model Job của mình
        // thì bạn có thể dùng return model.getJobId().hashCode();
        // Nhưng getSnapshots().getSnapshot(position).getId() là cách đáng tin cậy hơn
    }
    public static class PopularJobViewHolder extends RecyclerView.ViewHolder {
        TextView textViewJobTitle, textViewCompanyName, textViewSalary, textViewLocation;
        ImageView imageViewCompanyLogo;

        public PopularJobViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewJobTitle = itemView.findViewById(R.id.textViewJobTitle);
            textViewCompanyName = itemView.findViewById(R.id.textViewCompanyName);
            textViewSalary = itemView.findViewById(R.id.textViewSalary);
            textViewLocation = itemView.findViewById(R.id.textViewLocation);
            imageViewCompanyLogo = itemView.findViewById(R.id.imageViewCompanyLogo);
        }

        public void bind(Job job) {
            Log.d("JobDataCheck", "Binding Job ID: " + job.getId());
            Log.d("JobDataCheck", "Title: " + job.getTitle());
            Log.d("JobDataCheck", "Company: " + job.getCompanyName());
            Log.d("JobDataCheck", "Salary: " + job.getSalaryDescription());
            Log.d("JobDataCheck", "Location: " + job.getLocationName());
            Log.d("JobDataCheck", "Logo URL: " + job.getCompanyLogoUrl());
            textViewJobTitle.setText(job.getTitle());
            textViewCompanyName.setText(job.getCompanyName());
            textViewSalary.setText(job.getSalaryDescription());
            textViewLocation.setText(job.getLocationName());
            if (job.getCompanyLogoUrl() != null && !job.getCompanyLogoUrl().isEmpty()) {
                Picasso.get().load(job.getCompanyLogoUrl())
                        .placeholder(R.drawable.ic_business_24) // Placeholder để dễ thấy lỗi tải ảnh
                        .error(R.drawable.ic_error_24)       // Ảnh lỗi nếu tải thất bại
                        .into(imageViewCompanyLogo);
            } else {
                imageViewCompanyLogo.setImageResource(R.drawable.ic_business_24);
                Log.w("JobDataCheck", "URL logo công ty rỗng hoặc null cho Job ID: " + job.getId());
            }
        }
    }
}