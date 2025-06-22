package com.example.hanoistudentgigs.adapters;

import android.app.AlertDialog;
import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.models.User;
import com.example.hanoistudentgigs.utils.Constants;

public class UserAdapter extends FirestoreRecyclerAdapter<User, UserAdapter.UserViewHolder> {
    private Context context;

    public UserAdapter(@NonNull FirestoreRecyclerOptions<User> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
        holder.bind(model);
        holder.buttonDeleteUser.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận xóa người dùng")
                    .setMessage("Hành động này không thể hoàn tác. Bạn có chắc chắn muốn xóa " + model.getFullName() + "?")
                    .setPositiveButton("Xóa", (dialog, which) -> deleteUser(model.getUid()))
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    private void deleteUser(String uid) {
        // Xóa khỏi Firestore trước
        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION).document(uid)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Đã xóa người dùng khỏi CSDL.", Toast.LENGTH_SHORT).show();
                    // Lưu ý: Xóa khỏi Authentication phức tạp hơn và cần xử lý lại xác thực
                    // hoặc thực hiện qua Cloud Function để đảm bảo an toàn.
                    // Đoạn code dưới đây chỉ mang tính minh họa.
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Lỗi khi xóa khỏi CSDL.", Toast.LENGTH_SHORT).show());
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserName, textViewUserEmail;
        Button buttonDeleteUser;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            textViewUserEmail = itemView.findViewById(R.id.textViewUserEmail);
            buttonDeleteUser = itemView.findViewById(R.id.buttonDeleteUser);
        }

        public void bind(User user) {
            textViewUserName.setText(user.getFullName());
            textViewUserEmail.setText(user.getEmail());
        }
    }
}