package com.example.hanoistudentgigs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.models.Filter;
import com.example.hanoistudentgigs.utils.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AppCompatActivity {
    private Spinner spinnerCategory, spinnerLocation, spinnerJobType;
    private Button buttonApplyFilter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        spinnerJobType = findViewById(R.id.spinnerJobType);
        buttonApplyFilter = findViewById(R.id.buttonApplyFilter);

        loadSpinnerData();

        buttonApplyFilter.setOnClickListener(v -> applyFiltersAndReturn());
    }

    private void applyFiltersAndReturn() {
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

        Intent resultIntent = new Intent();
        resultIntent.putExtra("applied_filter", newFilter);
        setResult(RESULT_OK, resultIntent);
        finish(); // Đóng màn hình lọc và quay lại màn hình tìm kiếm
    }

    private void loadSpinnerData() {
        loadDataForSpinner(Constants.CATEGORIES_COLLECTION, spinnerCategory, "Tất cả ngành nghề");
        loadDataForSpinner(Constants.LOCATIONS_COLLECTION, spinnerLocation, "Tất cả địa điểm");

        List<String> jobTypes = new ArrayList<>();
        jobTypes.add("Tất cả loại hình");
        jobTypes.add("PartTime");
        jobTypes.add("Freelance");
        jobTypes.add("Internship");
        ArrayAdapter<String> jobTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jobTypes);
        jobTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJobType.setAdapter(jobTypeAdapter);
    }

    private void loadDataForSpinner(String collectionPath, Spinner spinner, String defaultText) {
        db.collection(collectionPath).orderBy("name").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> items = new ArrayList<>();
                items.add(defaultText);
                for (QueryDocumentSnapshot document : task.getResult()) {
                    items.add(document.getString("name"));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            } else {
                Toast.makeText(this, "Lỗi tải dữ liệu lọc.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}