package com.example.hanoistudentgigs.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.models.User;

import java.util.List;

import android.content.res.ColorStateList;
import android.graphics.Color;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onDelete(User user);
        void onView(User user);
        void onVerify(User user);
        void onEdit(User user);
    }

    public UserAdapter(List<User> userList, OnUserActionListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        String displayName = user.getRole().equalsIgnoreCase("student") ? user.getFullName() : user.getCompanyName();
        holder.tvName.setText(displayName + " (" + user.getEmail() + ")");

        // Chỉ hiển thị nút duyệt cho nhà tuyển dụng
        if (user.getRole().equalsIgnoreCase("employer")) {
            holder.btnVerify.setVisibility(View.VISIBLE);

            // Cập nhật trạng thái nút "Duyệt"
            if (user.isVerified()) {
                holder.btnVerify.setText("Đã duyệt");
                holder.btnVerify.setEnabled(false);
                holder.btnVerify.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BDBDBD"))); // Màu xám
            } else {
                holder.btnVerify.setText("Duyệt");
                holder.btnVerify.setEnabled(true);
                holder.btnVerify.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50"))); // Màu xanh lá
            }

        } else {
            holder.btnVerify.setVisibility(View.GONE);
        }
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(user);
        });
        holder.btnView.setOnClickListener(v -> {
            if (listener != null) listener.onView(user);
        });
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(user);
        });
        holder.btnVerify.setOnClickListener(v -> {
            if (listener != null) listener.onVerify(user);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        Button btnView, btnVerify, btnDelete, btnEdit;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            btnView = itemView.findViewById(R.id.btnViewUser);
            btnEdit = itemView.findViewById(R.id.btnEditUser);
            btnVerify = itemView.findViewById(R.id.btnVerifyUser);
            btnDelete = itemView.findViewById(R.id.btnDeleteUser);
        }
    }
}