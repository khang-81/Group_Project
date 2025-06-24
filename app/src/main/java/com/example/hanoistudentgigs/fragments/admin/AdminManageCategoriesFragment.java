package com.example.hanoistudentgigs.fragments.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.adapters.CategoryAdapter;
import com.example.hanoistudentgigs.models.Category;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import java.util.regex.Pattern;

public class AdminManageCategoriesFragment extends Fragment implements CategoryAdapter.OnCategoryActionListener {
    private Button btnTabJob, btnTabSkill, btnTabLocation;
    private Button buttonAddCategory;
    private EditText editTextNewCategory;
    private RecyclerView recyclerViewCategories;
    private CategoryAdapter categoryAdapter;
    private List<Category> allIndustries = new ArrayList<>();
    private List<Category> allSkills = new ArrayList<>();
    private List<Category> allLocations = new ArrayList<>();
    private List<Category> currentList = new ArrayList<>();
    private int currentTab = 0; // 0: Industry, 1: Skill, 2: Location
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_manager_categories, container, false);
        btnTabJob = view.findViewById(R.id.btnTabJob);
        btnTabSkill = view.findViewById(R.id.btnTabSkill);
        btnTabLocation = view.findViewById(R.id.btnTabLocation);
        buttonAddCategory = view.findViewById(R.id.buttonAddCategory);
        editTextNewCategory = view.findViewById(R.id.editTextNewCategory);
        recyclerViewCategories = view.findViewById(R.id.recyclerViewCategories);

        db = FirebaseFirestore.getInstance();
        categoryAdapter = new CategoryAdapter(currentList, this);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCategories.setAdapter(categoryAdapter);

        btnTabJob.setOnClickListener(v -> switchTab(0));
        btnTabSkill.setOnClickListener(v -> switchTab(1));
        btnTabLocation.setOnClickListener(v -> switchTab(2));
        buttonAddCategory.setOnClickListener(v -> addCategory());

        // Mặc định chọn ngành nghề
        btnTabJob.setSelected(true);
        switchTab(0);
        return view;
    }

    private void switchTab(int tab) {
        currentTab = tab;
        btnTabJob.setEnabled(tab != 0);
        btnTabSkill.setEnabled(tab != 1);
        btnTabLocation.setEnabled(tab != 2);
        currentList.clear();
        categoryAdapter.notifyDataSetChanged();
        if (tab == 0) {
            loadCategoriesFromFirestore();
            editTextNewCategory.setHint("Nhập ngành nghề...");
        } else if (tab == 1) {
            loadSkillsFromFirestore();
            editTextNewCategory.setHint("Nhập kỹ năng...");
        } else {
            loadLocationsFromFirestore();
            editTextNewCategory.setHint("Nhập địa điểm...");
        }
        btnTabJob.setSelected(tab == 0);
        btnTabSkill.setSelected(tab == 1);
        btnTabLocation.setSelected(tab == 2);
    }

    private String generateIdFromName(String name) {
        String prefix;
        switch (currentTab) {
            case 0:
                prefix = "cat_";
                break;
            case 1:
                prefix = "skill_";
                break;
            case 2:
                prefix = "loc_";
                break;
            default:
                prefix = "";
        }

        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String slug = pattern.matcher(normalized).replaceAll("")
                .toLowerCase()
                .replaceAll("đ", "d")
                .replaceAll(" ", "_")
                .replaceAll("[^a-z0-9_]", "");
        return prefix + slug;
    }

    private void addCategory() {
        String name = editTextNewCategory.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
            return;
        }
        String collectionPath;
        switch (currentTab) {
            case 0: collectionPath = "categories"; break;
            case 1: collectionPath = "skills"; break;
            case 2: collectionPath = "locations"; break;
            default: return;
        }
        String generatedId = generateIdFromName(name);
        Category newCategory = new Category();
        newCategory.setName(name);
        newCategory.setId(generatedId);
        db.collection(collectionPath).document(generatedId).set(newCategory)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Thêm thành công!", Toast.LENGTH_SHORT).show();
                    editTextNewCategory.setText("");
                    switchTab(currentTab);
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Thêm thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadCategoriesFromFirestore() {
        db.collection("categories")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                allIndustries.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Category category = doc.toObject(Category.class);
                    category.setId(doc.getId()); // Important for deletion
                    allIndustries.add(category);
                }
                currentList.clear();
                currentList.addAll(allIndustries);
                categoryAdapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e -> Log.e("FirestoreError", "Error loading categories", e));
    }

    private void loadSkillsFromFirestore() {
        db.collection("skills")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                allSkills.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Category skill = doc.toObject(Category.class);
                    skill.setId(doc.getId()); // Important for deletion
                    allSkills.add(skill);
                }
                currentList.clear();
                currentList.addAll(allSkills);
                categoryAdapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e -> Log.e("FirestoreError", "Error loading skills", e));
    }

    private void loadLocationsFromFirestore() {
        db.collection("locations")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                allLocations.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Category location = doc.toObject(Category.class);
                    location.setId(doc.getId()); // Get the document ID
                    allLocations.add(location);
                }
                currentList.clear();
                currentList.addAll(allLocations);
                categoryAdapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e -> {
                Log.e("FirestoreError", "Error loading locations", e);
                Toast.makeText(getContext(), "Lỗi tải địa điểm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    // Hiển thị đẹp hơn: "loc_dong_da" -> "Dong Da"
    private String formatRentalName(String id) {
        String name = id.replace("loc_", "").replace("_", " ");
        String[] words = name.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }

    @Override
    public void onDelete(Category category) {
        String collectionPath;
        switch (currentTab) {
            case 0:
                collectionPath = "categories";
                break;
            case 1:
                collectionPath = "skills";
                break;
            case 2:
                collectionPath = "locations";
                break;
            default:
                return;
        }

        if (category.getId() == null || category.getId().isEmpty()) {
            Toast.makeText(getContext(), "Không thể xóa: ID không tồn tại.", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa: " + category.getName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    db.collection(collectionPath).document(category.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Đã xóa thành công!", Toast.LENGTH_SHORT).show();
                                // Refresh the list after deletion
                                switchTab(currentTab);
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Xóa thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onEdit(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sửa danh mục");
        final EditText input = new EditText(getContext());
        input.setText(category.getName());
        builder.setView(input);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (TextUtils.isEmpty(newName)) {
                Toast.makeText(getContext(), "Tên không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            String collectionPath;
            switch (currentTab) {
                case 0: collectionPath = "categories"; break;
                case 1: collectionPath = "skills"; break;
                case 2: collectionPath = "locations"; break;
                default: return;
            }
            db.collection(collectionPath).document(category.getId())
                .update("name", newName)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Đã cập nhật!", Toast.LENGTH_SHORT).show();
                    switchTab(currentTab);
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}