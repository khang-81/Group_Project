package com.example.hanoistudentgigs.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.adapters.CategoryAdapter;
import com.example.hanoistudentgigs.models.Category; // Bạn cần tạo model này
import com.example.hanoistudentgigs.utils.Constants;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class AdminManageCategoriesFragment extends Fragment {

    private RecyclerView recyclerViewCategories;
    private RadioGroup radioGroupCategoryType;
    private EditText editTextNewCategory;
    private Button buttonAddCategory;
    private CategoryAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String currentCollection = Constants.CATEGORIES_COLLECTION; // Mặc định

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_manager_categories, container, false);

        recyclerViewCategories = view.findViewById(R.id.recyclerViewCategories);
        radioGroupCategoryType = view.findViewById(R.id.radioGroupCategoryType);
        editTextNewCategory = view.findViewById(R.id.editTextNewCategory);
        buttonAddCategory = view.findViewById(R.id.buttonAddCategory);

        setupRecyclerView();

        radioGroupCategoryType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioCategoryJob) {
                currentCollection = Constants.CATEGORIES_COLLECTION;
            } else if (checkedId == R.id.radioCategorySkill) {
                currentCollection = Constants.SKILLS_COLLECTION;
            } else if (checkedId == R.id.radioCategoryLocation) {
                currentCollection = Constants.LOCATIONS_COLLECTION;
            }
            // Tải lại adapter với collection mới
            setupRecyclerView();
            adapter.startListening();
        });

        buttonAddCategory.setOnClickListener(v -> addNewCategory());

        return view;
    }

    private void setupRecyclerView() {
        if (adapter != null) {
            adapter.stopListening();
        }
        Query query = db.collection(currentCollection).orderBy("name");
        FirestoreRecyclerOptions<Category> options = new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(query, Category.class)
                .build();
        adapter = new CategoryAdapter(options, currentCollection);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCategories.setAdapter(adapter);
        adapter.startListening();
    }

    private void addNewCategory() {
        String name = editTextNewCategory.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Tên danh mục không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = db.collection(currentCollection).document().getId();
        Map<String, String> category = new HashMap<>();
        category.put("id", id);
        category.put("name", name);

        db.collection(currentCollection).document(id).set(category)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Thêm thành công!", Toast.LENGTH_SHORT).show();
                    editTextNewCategory.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Thêm thất bại.", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }
}