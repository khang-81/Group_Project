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

    public UserAdapter(List<User> userList) {
        this.userList = userList;
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
        String displayName = user.getRole().equals("student") ? user.getFullName() : user.getCompanyName();
        holder.tvName.setText(displayName + " (" + user.getEmail() + ")");
        // Ẩn/hiện nút xác thực tuỳ role, không xử lý callback
        if (user.getRole().equals("employer")) {
            holder.btnVerify.setVisibility(View.VISIBLE);
        } else {
            holder.btnVerify.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        Button btnView, btnVerify, btnDelete;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            btnView = itemView.findViewById(R.id.btnViewUser);
            btnVerify = itemView.findViewById(R.id.btnVerifyUser);
            btnDelete = itemView.findViewById(R.id.btnDeleteUser);
        }
    }
}