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

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.models.Application;

import java.util.List;

public class ApplicantStaticAdapter extends RecyclerView.Adapter<ApplicantStaticAdapter.ApplicantViewHolder> {
    private List<Application> applicantList;
    private Context context;

    public ApplicantStaticAdapter(List<Application> applicantList, Context context) {
        this.applicantList = applicantList;
        this.context = context;
    }

    @NonNull
    @Override
    public ApplicantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_applicant, parent, false);
        return new ApplicantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicantViewHolder holder, int position) {
        Application model = applicantList.get(position);

        holder.textViewApplicantName.setText(model.getStudentUid() != null ? model.getStudentUid() : "Không rõ tên");
        holder.textViewApplicationStatus.setText("Trạng thái: " + (model.getStatus() != null ? model.getStatus() : "Chưa rõ"));

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

    @Override
    public int getItemCount() {
        return applicantList.size();
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
    }
}
