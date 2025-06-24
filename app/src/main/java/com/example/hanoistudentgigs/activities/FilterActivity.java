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
import java.util.Calendar; // Import Calendar
import java.util.List;

public class FilterActivity extends AppCompatActivity {
    // Đảm bảo khai báo spinnerDateRange ở đây
    private Spinner spinnerCategory, spinnerLocation, spinnerJobType, spinnerDateRange;
    private Button buttonApplyFilter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter); // Vẫn sử dụng activity_filter.xml

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        spinnerJobType = findViewById(R.id.spinnerJobType);
        spinnerDateRange = findViewById(R.id.spinnerDateRange); // Ánh xạ spinnerDateRange
        buttonApplyFilter = findViewById(R.id.buttonApplyFilter);

        loadSpinnerData(); // Sẽ bao gồm cả việc thiết lập dữ liệu cho spinnerDateRange

        buttonApplyFilter.setOnClickListener(v -> applyFiltersAndReturn());
    }

    private void applyFiltersAndReturn() {
        Filter newFilter = new Filter();

        // Lấy giá trị từ Spinner Ngành nghề
        if (spinnerCategory.getSelectedItemPosition() > 0) {
            newFilter.setCategory(spinnerCategory.getSelectedItem().toString());
        }
        // Lấy giá trị từ Spinner Địa điểm
        if (spinnerLocation.getSelectedItemPosition() > 0) {
            newFilter.setLocation(spinnerLocation.getSelectedItem().toString());
        }
        // Lấy giá trị từ Spinner Loại hình công việc
        if (spinnerJobType.getSelectedItemPosition() > 0) {
            newFilter.setJobType(spinnerJobType.getSelectedItem().toString());
        }

        // Lấy lựa chọn từ spinnerDateRange và tính toán min/max millis
        String selectedDateRangeOption = spinnerDateRange.getSelectedItem().toString();
        Long calculatedMinDateMillis = null;
        Long calculatedMaxDateMillis = null; // Mặc định là null, sẽ được đặt là System.currentTimeMillis() nếu cần

        Calendar calendar = Calendar.getInstance(); // Sử dụng Calendar để tính toán ngày tháng chính xác

        switch (selectedDateRangeOption) {
            case "Tất cả thời gian":
                calculatedMinDateMillis = null;
                calculatedMaxDateMillis = null;
                break;
            case "Bài đăng mới nhất (trong 24 giờ qua)":
                calendar.add(Calendar.HOUR_OF_DAY, -24);
                calculatedMinDateMillis = calendar.getTimeInMillis();
                calculatedMaxDateMillis = System.currentTimeMillis(); // Đến thời điểm hiện tại
                break;
            case "Bài đăng trong 7 ngày qua":
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                calculatedMinDateMillis = calendar.getTimeInMillis();
                calculatedMaxDateMillis = System.currentTimeMillis();
                break;
            case "Bài đăng trong 30 ngày qua":
                calendar.add(Calendar.DAY_OF_YEAR, -30);
                calculatedMinDateMillis = calendar.getTimeInMillis();
                calculatedMaxDateMillis = System.currentTimeMillis();
                break;
            case "Bài đăng trong 3 tháng qua":
                calendar.add(Calendar.MONTH, -3);
                calculatedMinDateMillis = calendar.getTimeInMillis();
                calculatedMaxDateMillis = System.currentTimeMillis();
                break;
            case "Bài đăng trong 6 tháng qua":
                calendar.add(Calendar.MONTH, -6);
                calculatedMinDateMillis = calendar.getTimeInMillis();
                calculatedMaxDateMillis = System.currentTimeMillis();
                break;
            case "Bài đăng trong 1 năm qua":
                calendar.add(Calendar.YEAR, -1);
                calculatedMinDateMillis = calendar.getTimeInMillis();
                calculatedMaxDateMillis = System.currentTimeMillis();
                break;
            // Thêm các case khác nếu bạn có thêm tùy chọn
        }

        newFilter.setMinPostedDateMillis(calculatedMinDateMillis);
        newFilter.setMaxPostedDateMillis(calculatedMaxDateMillis);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("applied_filter", newFilter);
        setResult(RESULT_OK, resultIntent);
        finish(); // Đóng màn hình lọc và quay lại màn hình tìm kiếm
    }

    private void loadSpinnerData() {
        // Tải dữ liệu cho Spinner Ngành nghề
        loadDataForSpinner(Constants.CATEGORIES_COLLECTION, spinnerCategory, "Tất cả ngành nghề");
        // Tải dữ liệu cho Spinner Địa điểm
        loadDataForSpinner(Constants.LOCATIONS_COLLECTION, spinnerLocation, "Tất cả địa điểm");

        // Thiết lập dữ liệu cố định cho Spinner Loại hình công việc
        List<String> jobTypes = new ArrayList<>();
        jobTypes.add("Tất cả loại hình");
        jobTypes.add("PartTime");
        jobTypes.add("Freelance");
        jobTypes.add("Internship");
        ArrayAdapter<String> jobTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jobTypes);
        jobTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJobType.setAdapter(jobTypeAdapter);

        // THIẾT LẬP DỮ LIỆU CHO spinnerDateRange
        List<String> dateRangeOptions = new ArrayList<>();
        dateRangeOptions.add("Tất cả thời gian");
        dateRangeOptions.add("Bài đăng mới nhất (trong 24 giờ qua)");
        dateRangeOptions.add("Bài đăng trong 7 ngày qua");
        dateRangeOptions.add("Bài đăng trong 30 ngày qua");
        dateRangeOptions.add("Bài đăng trong 3 tháng qua");
        dateRangeOptions.add("Bài đăng trong 6 tháng qua");
        dateRangeOptions.add("Bài đăng trong 1 năm qua");

        ArrayAdapter<String> dateRangeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, dateRangeOptions);
        dateRangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDateRange.setAdapter(dateRangeAdapter);

        // TODO: Nếu bạn muốn khôi phục lựa chọn của spinnerDateRange khi mở FilterActivity
        // (ví dụ: nếu có một bộ lọc cũ được truyền vào Intent), bạn sẽ cần thêm logic tương tự
        // như trong FilterBottomSheetFragment để xác định và đặt lựa chọn phù hợp.
        // Ví dụ:
        /*
        if (getIntent() != null && getIntent().hasExtra("current_filter")) {
            Filter currentFilter = (Filter) getIntent().getSerializableExtra("current_filter");
            if (currentFilter != null) {
                // ... logic để khôi phục lựa chọn cho category, location, job type
                // và logic phức tạp cho dateRangeOptions như trong FilterBottomSheetFragment
            }
        }
        */
    }

    private void loadDataForSpinner(String collectionPath, Spinner spinner, String defaultText) {
        db.collection(collectionPath).orderBy("name").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> items = new ArrayList<>();
                items.add(defaultText);
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