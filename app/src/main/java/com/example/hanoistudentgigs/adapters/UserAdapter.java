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
        // Ẩn/hiện nút xác thực tuỳ role, không xử lý callback
        if (user.getRole().equalsIgnoreCase("employer")) {
            holder.btnVerify.setVisibility(View.VISIBLE);
            if (user.isVerified()) {
                holder.btnVerify.setText("Đã duyệt");
                holder.btnVerify.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFBDBDBD));
                holder.btnVerify.setEnabled(false);
            } else {
                holder.btnVerify.setText("Duyệt");
                holder.btnVerify.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
                holder.btnVerify.setEnabled(true);
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
        holder.btnVerify.setOnClickListener(v -> {
            if (listener != null) listener.onVerify(user);
        });
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(user);
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
            btnVerify = itemView.findViewById(R.id.btnVerifyUser);
            btnDelete = itemView.findViewById(R.id.btnDeleteUser);
            btnEdit = itemView.findViewById(R.id.btnEditUser);

        }
    }
}