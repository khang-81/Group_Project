package com.example.hanoistudentgigs.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.fragments.employer.EmployerViewApplicantsFragment;
import com.example.hanoistudentgigs.models.Job;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class EmployerJobAdapter extends FirestoreRecyclerAdapter<Job, EmployerJobAdapter.EmployerJobViewHolder> {
    private Context context;

    public EmployerJobAdapter(@NonNull FirestoreRecyclerOptions<Job> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull EmployerJobViewHolder holder, int position, @NonNull Job model) {
        holder.bind(model);
        holder.itemView.setOnClickListener(v -> {
            if (context == null) return;
            AppCompatActivity activity = (AppCompatActivity) context;
            EmployerViewApplicantsFragment fragment = new EmployerViewApplicantsFragment();

            Bundle args = new Bundle();
            args.putString("JOB_ID", model.getId());
            fragment.setArguments(args);

            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @NonNull
    @Override
    public EmployerJobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employer_job, parent, false);
        return new EmployerJobViewHolder(view);
    }

    public static class EmployerJobViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewJobTitle;
        // Có thể thêm các TextView khác như số lượng ứng viên, ngày đăng...

        public EmployerJobViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewJobTitle = itemView.findViewById(R.id.textViewJobTitle);
        }

        public void bind(Job job) {
            if (job != null) {
                textViewJobTitle.setText(job.getTitle());
            }
        }
    }
}
