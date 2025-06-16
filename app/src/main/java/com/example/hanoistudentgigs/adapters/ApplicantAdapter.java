package com.example.hanoistudentgigs.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.models.Application;

public class ApplicantAdapter extends FirestoreRecyclerAdapter<Application, ApplicantAdapter.ApplicantViewHolder> {
    private Context context;

    public ApplicantAdapter(@NonNull FirestoreRecyclerOptions<Application> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ApplicantViewHolder holder, int position, @NonNull Application model) {
        holder.bind(model);
        holder.buttonViewCv.setOnClickListener(v -> {
            if (model.getCvUrl() != null && !model.getCvUrl().isEmpty()) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getCvUrl()));
                    context.startActivity(browserIntent);
                } catch (Exception e) {
                    Toast.makeText(context, "Không thể mở link CV.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Ứng viên này chưa tải lên CV.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public ApplicantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_applicant, parent, false);
        return new ApplicantViewHolder(view);
    }

    public static class ApplicantViewHolder extends RecyclerView.ViewHolder {
        TextView textViewApplicantName, textViewApplicationStatus;
        Button buttonViewCv;

        public ApplicantViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewApplicantName = itemView.findViewById(R.id.textViewApplicantName);
            textViewApplicationStatus = itemView.findViewById(R.id.textViewApplicationStatus);
            buttonViewCv = itemView.findViewById(R.id.buttonViewCv);
        }

        public void bind(Application application) {
            if (application == null) return;
            textViewApplicantName.setText(application.getStudentName() != null ? application.getStudentName() : "Không rõ tên");
            textViewApplicationStatus.setText(application.getStatus() != null ? "Trạng thái: " + application.getStatus() : "Trạng thái: Chưa rõ");
        }
    }
}