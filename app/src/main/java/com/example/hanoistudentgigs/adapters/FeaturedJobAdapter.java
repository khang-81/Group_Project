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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

public class FeaturedJobAdapter extends FirestoreRecyclerAdapter<Job, FeaturedJobAdapter.FeaturedJobViewHolder> {
    private Context context;
    private RecyclerView recyclerView; // THÊM DÒNG NÀY
    private TextView textViewNoResults; // THÊM DÒNG NÀY
    @Override
    public void onDataChanged() {
        super.onDataChanged();
        Log.d("MyAdapter", "onDataChanged called. Item Count: " + getItemCount());
        if (recyclerView != null && textViewNoResults != null) {
            if (getItemCount() == 0) {
                recyclerView.setVisibility(View.GONE);
                textViewNoResults.setVisibility(View.VISIBLE);
                textViewNoResults.setText("Không tìm thấy công việc nổi bật nào."); // Có thể tùy chỉnh text này
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                textViewNoResults.setVisibility(View.GONE);
            }
        } else {
            Log.e("FeaturedJobAdapter", "RecyclerView or TextViewNoResults is null in onDataChanged().");
        }
    }

    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
        super.onError(e);
        Log.e("MyAdapter", "Firestore error: " + e.getMessage(), e);
        if (recyclerView != null && textViewNoResults != null) {
            recyclerView.setVisibility(View.GONE);
            textViewNoResults.setVisibility(View.VISIBLE);
            textViewNoResults.setText("Đã xảy ra lỗi khi tải công việc nổi bật: " + e.getMessage());
        } else {
            Log.e("FeaturedJobAdapter", "RecyclerView or TextViewNoResults is null in onError().");
        }
    }
//    @Override
//    public long getItemId(int position) {
//        if (position >= 0 && position < getSnapshots().size()) {
//            String documentId = getSnapshots().getSnapshot(position).getId();
//            // Đây là dòng duy nhất bạn cần để trả về ID.
//            return documentId.hashCode();
//        }
//        return RecyclerView.NO_ID;
//    }
    public FeaturedJobAdapter(@NonNull FirestoreRecyclerOptions<Job> options, Context context,
                              RecyclerView recyclerView, TextView textViewNoResults) { // <-- THÊM THAM SỐ VÀO ĐÂY
        super(options);
        this.context = context;
        this.recyclerView = recyclerView;         // <-- Gán tham số nhận được vào biến thành viên
        this.textViewNoResults = textViewNoResults; // <-- Gán tham số nhận được vào biến thành viên
//        setHasStableIds(true);
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

            Picasso.get().load(job.getCompanyLogoUrl()).into(imageViewCompanyLogo);
        }
    }
}