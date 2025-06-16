package com.example.hanoistudentgigs.fragments.filter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.models.Filter;
import com.example.hanoistudentgigs.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FilterBottomSheetFragment extends BottomSheetDialogFragment {

    public interface FilterListener {
        void onFilterApplied(Filter filter);
    }

    private Spinner spinnerCategory, spinnerLocation, spinnerJobType;
    private Button buttonApplyFilter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Filter currentFilter;
    private FilterListener filterListener;

    public static FilterBottomSheetFragment newInstance(Filter filter) {
        FilterBottomSheetFragment fragment = new FilterBottomSheetFragment();
        Bundle args = new Bundle();
        args.putSerializable("current_filter", filter);
        fragment.setArguments(args);
        return fragment;
    }

    public void setFilterListener(FilterListener listener) {
        this.filterListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentFilter = (Filter) getArguments().getSerializable("current_filter");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_bottom_sheet, container, false);

        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerLocation = view.findViewById(R.id.spinnerLocation);
        spinnerJobType = view.findViewById(R.id.spinnerJobType);
        buttonApplyFilter = view.findViewById(R.id.buttonApplyFilter);

        loadSpinnerData();

        buttonApplyFilter.setOnClickListener(v -> {
            Filter newFilter = new Filter();
            if (spinnerCategory.getSelectedItemPosition() > 0) {
                newFilter.setCategory(spinnerCategory.getSelectedItem().toString());
            }
            if (spinnerLocation.getSelectedItemPosition() > 0) {
                newFilter.setLocation(spinnerLocation.getSelectedItem().toString());
            }
            if (spinnerJobType.getSelectedItemPosition() > 0) {
                newFilter.setJobType(spinnerJobType.getSelectedItem().toString());
            }

            if (filterListener != null) {
                filterListener.onFilterApplied(newFilter);
            }
            dismiss(); // Đóng BottomSheet
        });

        return view;
    }

    private void loadSpinnerData() {
        // Load Categories
        loadDataForSpinner(Constants.CATEGORIES_COLLECTION, spinnerCategory, "Tất cả ngành nghề");
        // Load Locations
        loadDataForSpinner(Constants.LOCATIONS_COLLECTION, spinnerLocation, "Tất cả địa điểm");
        // Load Job Types
        ArrayAdapter<String> jobTypeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new String[]{"Tất cả loại hình", "PartTime", "Freelance", "Internship"});
        jobTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJobType.setAdapter(jobTypeAdapter);
    }

    private void loadDataForSpinner(String collectionPath, Spinner spinner, String defaultText) {
        db.collection(collectionPath).orderBy("name").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> items = new ArrayList<>();
                items.add(defaultText); // Thêm lựa chọn mặc định
                for (QueryDocumentSnapshot document : task.getResult()) {
                    items.add(document.getString("name"));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            } else {
                Toast.makeText(getContext(), "Lỗi tải dữ liệu lọc.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}