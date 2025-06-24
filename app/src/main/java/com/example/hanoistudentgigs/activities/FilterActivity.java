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
import java.util.Calendar;
import java.util.List;

public class FilterActivity extends AppCompatActivity {
    private Spinner spinnerCategory, spinnerLocation, spinnerJobType, spinnerDateRange;
    private Button buttonApplyFilter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        spinnerJobType = findViewById(R.id.spinnerJobType);
        spinnerDateRange = findViewById(R.id.spinnerDateRange);
        buttonApplyFilter = findViewById(R.id.buttonApplyFilter);

        loadSpinnerData();

        // TODO: Khôi phục bộ lọc đã chọn trước đó (nếu có) - PHẢI ĐẶT SAU loadSpinnerData() để đảm bảo adapter đã có dữ liệu
        if (getIntent() != null && getIntent().hasExtra("current_filter")) {
            Filter currentFilter = (Filter) getIntent().getSerializableExtra("current_filter");
            if (currentFilter != null) {
                // Gọi restoreFilterSelection sau khi tất cả các spinner đã được điền dữ liệu
                // Có thể cần một slight delay nếu loadDataForSpinner là bất đồng bộ và cần dữ liệu
                // nhưng với setup hiện tại (loadDataForSpinner dùng addOnCompleteListener),
                // chúng ta sẽ phải đảm bảo rằng adapter đã được set trước khi gọi restore.
                // Để đơn giản, giả định loadDataForSpinner đã hoàn tất việc setup adapter cho category và location
                // trước khi restoreFilterSelection được gọi.
                // Đối với jobType và dateRange, dữ liệu là cố định, nên không sao.
                restoreFilterSelection(currentFilter);
            }
        }

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

        String selectedDateRangeOption = spinnerDateRange.getSelectedItem().toString();
        Long calculatedMinDateMillis = null;
        Long calculatedMaxDateMillis = null;

        Calendar calendar = Calendar.getInstance();

        switch (selectedDateRangeOption) {
            case "Tất cả thời gian":
                calculatedMinDateMillis = null;
                calculatedMaxDateMillis = null;
                break;
            case "Bài đăng mới nhất (trong 24 giờ qua)":
                calendar.add(Calendar.HOUR_OF_DAY, -24);
                calculatedMinDateMillis = calendar.getTimeInMillis();
                calculatedMaxDateMillis = System.currentTimeMillis();
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
        }

        newFilter.setMinPostedDateMillis(calculatedMinDateMillis);
        newFilter.setMaxPostedDateMillis(calculatedMaxDateMillis);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("applied_filter", newFilter);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void loadSpinnerData() {
        loadDataForSpinner(Constants.CATEGORIES_COLLECTION, spinnerCategory, "Tất cả ngành nghề");
        loadDataForSpinner(Constants.LOCATIONS_COLLECTION, spinnerLocation, "Tất cả địa điểm");

        List<String> jobTypes = new ArrayList<>();
        jobTypes.add("Tất cả loại hình");
        jobTypes.add("Part-time");
        jobTypes.add("Freelance");
        jobTypes.add("Full-time");
        jobTypes.add("Internship");
        ArrayAdapter<String> jobTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jobTypes);
        jobTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJobType.setAdapter(jobTypeAdapter);

        // THIẾT LẬP DỮ LIỆU CHO spinnerDateRange (Dữ liệu cố định)
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

                // Nếu bạn khôi phục lọc ở đây, bạn cần truyền filter vào hoặc xử lý trong onCreate sau khi tất cả đã tải.
                // Hiện tại, việc gọi restoreFilterSelection ở onCreate là đúng nếu các adapter khác được set đồng bộ.
            } else {
                Toast.makeText(this, "Lỗi tải dữ liệu lọc.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void restoreFilterSelection(Filter filter) {
        // Khôi phục lựa chọn cho Spinner Category
        if (filter.getCategory() != null && !filter.getCategory().isEmpty()) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerCategory.getAdapter();
            if (adapter != null) {
                int spinnerPosition = adapter.getPosition(filter.getCategory());
                if (spinnerPosition >= 0) {
                    spinnerCategory.setSelection(spinnerPosition);
                }
            }
        }
        // Khôi phục lựa chọn cho Spinner Location
        if (filter.getLocation() != null && !filter.getLocation().isEmpty()) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerLocation.getAdapter();
            if (adapter != null) {
                int spinnerPosition = adapter.getPosition(filter.getLocation());
                if (spinnerPosition >= 0) {
                    spinnerLocation.setSelection(spinnerPosition);
                }
            }
        }
        // Khôi phục lựa chọn cho Spinner Job Type
        if (filter.getJobType() != null && !filter.getJobType().isEmpty()) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerJobType.getAdapter();
            if (adapter != null) {
                int spinnerPosition = adapter.getPosition(filter.getJobType());
                if (spinnerPosition >= 0) {
                    spinnerJobType.setSelection(spinnerPosition);
                }
            }
        }

        // Khôi phục lựa chọn cho Spinner Date Range
        if (filter.getMinPostedDateMillis() != null || filter.getMaxPostedDateMillis() != null) {
            long currentFilterMinMillis = (filter.getMinPostedDateMillis() != null) ? filter.getMinPostedDateMillis() : -1; // Sử dụng -1 để phân biệt null
            long currentFilterMaxMillis = (filter.getMaxPostedDateMillis() != null) ? filter.getMaxPostedDateMillis() : -1; // Sử dụng -1 để phân biệt null

            // Lấy trực tiếp danh sách các tùy chọn dateRangeOptions mà bạn đã dùng để tạo adapter
            // Nó là một danh sách cố định, nên không cần lấy từ adapter.
            List<String> dateRangeOptions = new ArrayList<>();
            dateRangeOptions.add("Tất cả thời gian");
            dateRangeOptions.add("Bài đăng mới nhất (trong 24 giờ qua)");
            dateRangeOptions.add("Bài đăng trong 7 ngày qua");
            dateRangeOptions.add("Bài đăng trong 30 ngày qua");
            dateRangeOptions.add("Bài đăng trong 3 tháng qua");
            dateRangeOptions.add("Bài đăng trong 6 tháng qua");
            dateRangeOptions.add("Bài đăng trong 1 năm qua");


            for (int i = 0; i < dateRangeOptions.size(); i++) {
                String option = dateRangeOptions.get(i);
                Long tempMinMillis = null;
                Long tempMaxMillis = null;
                Calendar tempCalendar = Calendar.getInstance();

                switch (option) {
                    case "Tất cả thời gian":
                        tempMinMillis = null;
                        tempMaxMillis = null;
                        break;
                    case "Bài đăng mới nhất (trong 24 giờ qua)":
                        tempCalendar.add(Calendar.HOUR_OF_DAY, -24);
                        tempMinMillis = tempCalendar.getTimeInMillis();
                        tempMaxMillis = System.currentTimeMillis();
                        break;
                    case "Bài đăng trong 7 ngày qua":
                        tempCalendar.add(Calendar.DAY_OF_YEAR, -7);
                        tempMinMillis = tempCalendar.getTimeInMillis();
                        tempMaxMillis = System.currentTimeMillis();
                        break;
                    case "Bài đăng trong 30 ngày qua":
                        tempCalendar.add(Calendar.DAY_OF_YEAR, -30);
                        tempMinMillis = tempCalendar.getTimeInMillis();
                        tempMaxMillis = System.currentTimeMillis();
                        break;
                    case "Bài đăng trong 3 tháng qua":
                        tempCalendar.add(Calendar.MONTH, -3);
                        tempMinMillis = tempCalendar.getTimeInMillis();
                        tempMaxMillis = System.currentTimeMillis();
                        break;
                    case "Bài đăng trong 6 tháng qua":
                        tempCalendar.add(Calendar.MONTH, -6);
                        tempMinMillis = tempCalendar.getTimeInMillis();
                        tempMaxMillis = System.currentTimeMillis();
                        break;
                    case "Bài đăng trong 1 năm qua":
                        tempCalendar.add(Calendar.YEAR, -1);
                        tempMinMillis = tempCalendar.getTimeInMillis();
                        tempMaxMillis = System.currentTimeMillis();
                        break;
                }

                // So sánh các giá trị tính toán với bộ lọc hiện tại
                // Cần xử lý trường hợp null một cách cẩn thận
                boolean minMatch;
                if (currentFilterMinMillis == -1 && tempMinMillis == null) {
                    minMatch = true;
                } else if (currentFilterMinMillis != -1 && tempMinMillis != null) {
                    minMatch = Math.abs(tempMinMillis - currentFilterMinMillis) < 1000; // 1 giây dung sai
                } else {
                    minMatch = false;
                }

                boolean maxMatch;
                if (currentFilterMaxMillis == -1 && tempMaxMillis == null) {
                    maxMatch = true;
                } else if (currentFilterMaxMillis != -1 && tempMaxMillis != null) {
                    maxMatch = Math.abs(tempMaxMillis - currentFilterMaxMillis) < 1000; // 1 giây dung sai
                } else {
                    maxMatch = false;
                }

                if (minMatch && maxMatch) {
                    spinnerDateRange.setSelection(i);
                    break;
                }
            }
        } else {
            // Nếu không có bộ lọc thời gian nào, chọn "Tất cả thời gian"
            spinnerDateRange.setSelection(0);
        }
    }
}