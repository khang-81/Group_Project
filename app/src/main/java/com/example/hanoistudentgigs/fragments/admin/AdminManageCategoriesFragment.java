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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.adapters.CategoryAdapter;
import com.example.hanoistudentgigs.models.Category;
import com.example.hanoistudentgigs.models.Rental;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class AdminManageCategoriesFragment extends Fragment implements CategoryAdapter.OnCategoryActionListener {
    private Button btnTabIndustry, btnTabSkill, btnTabLocation, btnAddCategory;
    private EditText etNewCategory;
    private RecyclerView rvCategoryList;
    private CategoryAdapter categoryAdapter;
    private List<Category> allIndustries = new ArrayList<>();
    private List<Category> allSkills = new ArrayList<>();
    private List<Rental> allRentals = new ArrayList<>();
    private List<Category> currentList = new ArrayList<>();
    private int currentTab = 0; // 0: Industry, 1: Skill, 2: Location
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_manager_categories, container, false);
        btnTabIndustry = view.findViewById(R.id.btnTabIndustry);
        btnTabSkill = view.findViewById(R.id.btnTabSkill);
        btnTabLocation = view.findViewById(R.id.btnTabLocation);
        btnAddCategory = view.findViewById(R.id.btnAddCategory);
        etNewCategory = view.findViewById(R.id.etNewCategory);
        rvCategoryList = view.findViewById(R.id.rvCategoryList);

        db = FirebaseFirestore.getInstance();
        categoryAdapter = new CategoryAdapter(currentList, this);
        rvCategoryList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCategoryList.setAdapter(categoryAdapter);

        btnTabIndustry.setOnClickListener(v -> switchTab(0));
        btnTabSkill.setOnClickListener(v -> switchTab(1));
        btnTabLocation.setOnClickListener(v -> switchTab(2));
        btnAddCategory.setOnClickListener(v -> addCategory());

        switchTab(0);
        return view;
    }

    private void switchTab(int tab) {
        currentTab = tab;
        btnTabIndustry.setEnabled(tab != 0);
        btnTabSkill.setEnabled(tab != 1);
        btnTabLocation.setEnabled(tab != 2);
        currentList.clear();
        categoryAdapter.notifyDataSetChanged();
        if (tab == 0) loadCategoriesFromFirestore();
        else if (tab == 1) loadSkillsFromFirestore();
        else loadRentalsFromFirestore();
        etNewCategory.setHint(tab == 0 ? "Nhập ngành nghề..." : tab == 1 ? "Nhập kỹ năng..." : "Nhập địa điểm...");
    }

    private void addCategory() {
        String name = etNewCategory.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentTab == 0) {
            Category newCat = new Category(name);
            allIndustries.add(newCat);
        } else if (currentTab == 1) {
            Category newCat = new Category(name);
            allSkills.add(newCat);
        } else {
            Rental newRental = new Rental(name);
            allRentals.add(newRental);
        }
        switchTab(currentTab);
        etNewCategory.setText("");
    }

    private void loadCategoriesFromFirestore() {
        allIndustries.clear();
        db.collection("categories")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                allIndustries.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Category category = doc.toObject(Category.class);
                    allIndustries.add(category);
                }
                if (currentTab == 0) {
                    currentList.clear();
                    currentList.addAll(allIndustries);
                    categoryAdapter.notifyDataSetChanged();
                }
            });
    }

    private void loadSkillsFromFirestore() {
        allSkills.clear();
        db.collection("skills")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                allSkills.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Category skill = doc.toObject(Category.class);
                    allSkills.add(skill);
                }
                if (currentTab == 1) {
                    currentList.clear();
                    currentList.addAll(allSkills);
                    categoryAdapter.notifyDataSetChanged();
                }
            });
    }

    private void loadRentalsFromFirestore() {
        allRentals.clear();
        db.collection("rentals")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                allRentals.clear();
                int count = 0;
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String id = doc.getId();
                    Rental rental = new Rental();
                    rental.setId(id);
                    rental.setName(formatRentalName(id));
                    allRentals.add(rental);
                    count++;
                }
                Log.d("FirestoreDebug", "Số lượng địa điểm (rental) lấy được: " + count);
                if (currentTab == 2) {
                    currentList.clear();
                    for (Rental rental : allRentals) {
                        Category cat = new Category();
                        cat.setId(rental.getId());
                        cat.setName(rental.getName());
                        currentList.add(cat);
                    }
                    categoryAdapter.notifyDataSetChanged();
                    if (allRentals.isEmpty()) {
                        Toast.makeText(getContext(), "Không có địa điểm nào trong hệ thống!", Toast.LENGTH_SHORT).show();
                    }
                }
            })
            .addOnFailureListener(e -> {
                if (currentTab == 2) {
                    currentList.clear();
                    categoryAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Lỗi tải địa điểm: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
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
        new AlertDialog.Builder(getContext())
                .setMessage("Bạn có chắc chắn xóa danh mục: " + category.getName() + " không?")
                .setPositiveButton("Có", (dialog, which) -> {
                    if (currentTab == 0) allIndustries.remove(category);
                    else if (currentTab == 1) allSkills.remove(category);
                    else allRentals.removeIf(r -> r.getId().equals(category.getId()));
                    switchTab(currentTab);
                })
                .setNegativeButton("Không", null)
                .show();
    }
}