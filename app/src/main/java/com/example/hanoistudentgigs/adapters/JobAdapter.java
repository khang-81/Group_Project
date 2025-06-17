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

public class JobAdapter extends FirestoreRecyclerAdapter<Job, JobAdapter.JobViewHolder> {
    private Context context;

    public JobAdapter(@NonNull FirestoreRecyclerOptions<Job> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull JobViewHolder holder, int position, @NonNull Job model) {
        // Lấy ID của document từ snapshot tại vị trí hiện tại
        String jobId = getSnapshots().getSnapshot(position).getId();
        // Gán ID này vào đối tượng Job (đảm bảo lớp Job có phương thức setId)
        model.setId(jobId);

        holder.bind(model);
        holder.itemView.setOnClickListener(v -> {
            // Khi người dùng nhấn vào một item, mở JobDetailActivity
            Intent intent = new Intent(context, JobDetailActivity.class);
            // Gửi ID của job qua Intent để màn hình chi tiết biết cần tải dữ liệu nào
            intent.putExtra("JOB_ID", model.getId());
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_card, parent, false);
        return new JobViewHolder(view);
    }

    // Class ViewHolder để giữ các view của một item
    public static class JobViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewJobTitle, textViewCompanyName, textViewSalary, textViewLocation;
        private ImageView imageViewCompanyLogo;

        public JobViewHolder(@NonNull View itemView) {
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
            // TODO: Dùng Picasso hoặc Glide để load ảnh logo từ URL (nếu có)
            // Trong JobViewHolder.bind(Job job)
            // Kiểm tra xem URL có tồn tại và không rỗng không
            if (job.getCompanyLogoUrl() != null && !job.getCompanyLogoUrl().isEmpty()) {
                        Picasso.get()
                        .load(job.getCompanyLogoUrl())
                        .placeholder(R.drawable.default_company_logo_placeholder) // Ảnh placeholder khi đang tải
                        .error(R.drawable.error_company_logo_placeholder) // Ảnh hiển thị nếu tải lỗi
                        .into(imageViewCompanyLogo);
            } else {
                // Hiển thị ảnh mặc định nếu không có URL logo
                imageViewCompanyLogo.setImageResource(R.drawable.default_company_logo_placeholder);
            }
        }
    }
}