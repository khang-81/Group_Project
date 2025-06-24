package com.example.hanoistudentgigs.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.models.Category;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    public interface OnCategoryActionListener {
        void onDelete(Category category);
        void onEdit(Category category);
    }

    private List<Category> categoryList;
    private OnCategoryActionListener listener;

    public CategoryAdapter(List<Category> categoryList, OnCategoryActionListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getName());
        holder.btnDeleteCategory.setOnClickListener(v -> listener.onDelete(category));
        holder.btnEditCategory.setOnClickListener(v -> listener.onEdit(category));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        Button btnDeleteCategory, btnEditCategory;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.textViewCategoryName);
            btnDeleteCategory = itemView.findViewById(R.id.btnDeleteCategory);
            btnEditCategory = itemView.findViewById(R.id.btnEditCategory);
        }
    }
}