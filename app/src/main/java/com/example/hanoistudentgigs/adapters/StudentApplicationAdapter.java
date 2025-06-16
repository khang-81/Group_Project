package com.example.hanoistudentgigs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.models.Application;
import com.example.hanoistudentgigs.models.Job;
import com.example.hanoistudentgigs.utils.Constants;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentApplicationAdapter extends FirestoreRecyclerAdapter<Application, StudentApplicationAdapter.ApplicationViewHolder> {

    public StudentApplicationAdapter(@NonNull FirestoreRecyclerOptions<Application> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position, @NonNull Application model) {
        holder.bind(model);
    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_application, parent, false);
        return new ApplicationViewHolder(view);
    }

    public static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        TextView textViewJobTitle, textViewCompanyName, textViewApplicationStatus;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewJobTitle = itemView.findViewById(R.id.textViewAppliedJobTitle);
            textViewCompanyName = itemView.findViewById(R.id.textViewAppliedCompanyName);
            textViewApplicationStatus = itemView.findViewById(R.id.textViewAppliedStatus);
        }

        public void bind(Application application) {
            if (application.getStatus() != null) {
                textViewApplicationStatus.setText("Trạng thái: " + application.getStatus());
            }

            // Lấy thông tin chi tiết của Job từ jobId để hiển thị Title và Company Name
            if (application.getJobId() != null) {
                FirebaseFirestore.getInstance().collection(Constants.JOBS_COLLECTION)
                        .document(application.getJobId()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Job job = documentSnapshot.toObject(Job.class);
                                if (job != null) {
                                    textViewJobTitle.setText(job.getTitle());
                                    textViewCompanyName.setText(job.getCompanyName());
                                }
                            } else {
                                textViewJobTitle.setText("Công việc không còn tồn tại");
                                textViewCompanyName.setText("");
                            }
                        });
            }
        }
    }
}