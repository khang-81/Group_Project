package com.example.hanoistudentgigs.adapters;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.models.Category;

public class CategoryAdapter extends FirestoreRecyclerAdapter<Category, CategoryAdapter.CategoryViewHolder> {
    private String collectionPath;

    public CategoryAdapter(@NonNull FirestoreRecyclerOptions<Category> options, String collectionPath) {
        super(options);
        this.collectionPath = collectionPath;
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull Category model) {
        holder.textViewCategoryName.setText(model.getName());
        holder.buttonDeleteCategory.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa '" + model.getName() + "'?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        FirebaseFirestore.getInstance().collection(collectionPath).document(model.getId()).delete();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCategoryName;
        Button buttonDeleteCategory;
        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);
            buttonDeleteCategory = itemView.findViewById(R.id.buttonDeleteCategory);
        }
    }
}