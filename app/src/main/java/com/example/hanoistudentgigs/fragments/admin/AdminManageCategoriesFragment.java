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

import java.util.ArrayList;
import java.util.List;

public class AdminManageCategoriesFragment extends Fragment implements CategoryAdapter.OnCategoryActionListener {
    private Button btnTabIndustry, btnTabSkill, btnTabLocation, btnAddCategory;
    private EditText etNewCategory;
    private RecyclerView rvCategoryList;
    private CategoryAdapter categoryAdapter;
    private List<Category> allIndustries = new ArrayList<>();
    private List<Category> allSkills = new ArrayList<>();
    private List<Category> allLocations = new ArrayList<>();
    private List<Category> currentList = new ArrayList<>();
    private int currentTab = 0; // 0: Industry, 1: Skill, 2: Location

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

        categoryAdapter = new CategoryAdapter(currentList, this);
        rvCategoryList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCategoryList.setAdapter(categoryAdapter);

        btnTabIndustry.setOnClickListener(v -> switchTab(0));
        btnTabSkill.setOnClickListener(v -> switchTab(1));
        btnTabLocation.setOnClickListener(v -> switchTab(2));
        btnAddCategory.setOnClickListener(v -> addCategory());

        loadDummyCategories();
        switchTab(0);
        return view;
    }

    private void switchTab(int tab) {
        currentTab = tab;
        btnTabIndustry.setEnabled(tab != 0);
        btnTabSkill.setEnabled(tab != 1);
        btnTabLocation.setEnabled(tab != 2);
        currentList.clear();
        if (tab == 0) currentList.addAll(allIndustries);
        else if (tab == 1) currentList.addAll(allSkills);
        else currentList.addAll(allLocations);
        categoryAdapter.notifyDataSetChanged();
        etNewCategory.setHint(tab == 0 ? "Nhập ngành nghề..." : tab == 1 ? "Nhập kỹ năng..." : "Nhập địa điểm...");
    }

    private void addCategory() {
        String name = etNewCategory.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
            return;
        }
        Category newCat = new Category(name);
        if (currentTab == 0) allIndustries.add(newCat);
        else if (currentTab == 1) allSkills.add(newCat);
        else allLocations.add(newCat);
        switchTab(currentTab);
        etNewCategory.setText("");
    }

    private void loadDummyCategories() {
        allIndustries.clear();
        allSkills.clear();
        allLocations.clear();
        allIndustries.add(new Category("Marketing"));
        allIndustries.add(new Category("Sales"));
        allIndustries.add(new Category("CSKH"));
        allSkills.add(new Category("SEO"));
        allSkills.add(new Category("Java"));
        allSkills.add(new Category("Photoshop"));
        allLocations.add(new Category("Quận 1"));
        allLocations.add(new Category("Quận 2"));
        allLocations.add(new Category("Quận Tân Bình"));
    }

    @Override
    public void onDelete(Category category) {
        new AlertDialog.Builder(getContext())
                .setMessage("Bạn có chắc chắn xóa danh mục: " + category.getName() + " không?")
                .setPositiveButton("Có", (dialog, which) -> {
                    if (currentTab == 0) allIndustries.remove(category);
                    else if (currentTab == 1) allSkills.remove(category);
                    else allLocations.remove(category);
                    switchTab(currentTab);
                })
                .setNegativeButton("Không", null)
                .show();
    }
}