package com.example.hanoistudentgigs.fragments.filter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
// import android.widget.EditText; // KHÔNG CẦN NỮA NẾU CHỈ DÙNG SPINNER CHO DATE RANGE
// import androidx.core.util.Pair; // KHÔNG CẦN NỮA
// import com.google.android.material.datepicker.MaterialDatePicker; // KHÔNG CẦN NỮA
// import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener; // KHÔNG CẦN NỮA

import java.text.SimpleDateFormat; // Vẫn có thể cần nếu bạn muốn định dạng ngày cho mục đích khác, nhưng không cho DatePicker nữa
import java.util.Calendar; // CẦN ĐỂ TÍNH TOÁN NGÀY THÁNG
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


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

    private Spinner spinnerCategory, spinnerLocation, spinnerJobType, spinnerDateRange; // THÊM spinnerDateRange
    // private EditText editTextDateRange; // XÓA DÒNG NÀY

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Filter currentFilter;
    private FilterListener filterListener;
    private Button buttonApplyFilter;

    // Biến để lưu trữ ngày bắt đầu và ngày kết thúc đã chọn (dưới dạng long milliseconds)
    private Long selectedStartDateMillis = null;
    private Long selectedEndDateMillis = null;

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
            // Khôi phục trạng thái ngày đã chọn nếu có trong filter
            if (currentFilter != null) {
                selectedStartDateMillis = currentFilter.getMinPostedDateMillis();
                selectedEndDateMillis = currentFilter.getMaxPostedDateMillis();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_filter, container, false); // ĐẢM BẢO ĐÂY LÀ activity_filter.xml

        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerLocation = view.findViewById(R.id.spinnerLocation);
        spinnerJobType = view.findViewById(R.id.spinnerJobType);
        spinnerDateRange = view.findViewById(R.id.spinnerDateRange); // ÁNH XẠ SPINNER MỚI
        buttonApplyFilter = view.findViewById(R.id.buttonApplyFilter);

        // KHÔNG CẦN setOnClickListener cho EditText nữa
        // KHÔNG CẦN CẬP NHẬT HIỂN THỊ NGÀY TRÊN EDITTEXT NỮA

        loadSpinnerData(); // Sẽ bao gồm cả việc thiết lập dữ liệu cho spinnerDateRange

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

            // Lấy lựa chọn từ spinnerDateRange và tính toán min/max millis
            String selectedDateRangeOption = spinnerDateRange.getSelectedItem().toString();
            Long calculatedMinDateMillis = null;
            Long calculatedMaxDateMillis = System.currentTimeMillis(); // Mặc định là thời điểm hiện tại

            Calendar calendar = Calendar.getInstance(); // Sử dụng Calendar để tính toán ngày tháng chính xác

            switch (selectedDateRangeOption) {
                case "Tất cả thời gian":
                    calculatedMinDateMillis = null;
                    calculatedMaxDateMillis = null; // Hoặc để lại System.currentTimeMillis() nếu bạn muốn giới hạn tối đa là hiện tại
                    break;
                case "Bài đăng mới nhất (trong 24 giờ qua)":
                    calendar.add(Calendar.HOUR_OF_DAY, -24);
                    calculatedMinDateMillis = calendar.getTimeInMillis();
                    break;
                case "Bài đăng trong 7 ngày qua":
                    calendar.add(Calendar.DAY_OF_YEAR, -7);
                    calculatedMinDateMillis = calendar.getTimeInMillis();
                    break;
                case "Bài đăng trong 30 ngày qua":
                    calendar.add(Calendar.DAY_OF_YEAR, -30);
                    calculatedMinDateMillis = calendar.getTimeInMillis();
                    break;
                case "Bài đăng trong 3 tháng qua":
                    calendar.add(Calendar.MONTH, -3);
                    calculatedMinDateMillis = calendar.getTimeInMillis();
                    break;
                case "Bài đăng trong 6 tháng qua":
                    calendar.add(Calendar.MONTH, -6);
                    calculatedMinDateMillis = calendar.getTimeInMillis();
                    break;
                case "Bài đăng trong 1 năm qua":
                    calendar.add(Calendar.YEAR, -1);
                    calculatedMinDateMillis = calendar.getTimeInMillis();
                    break;
                // Thêm các case khác nếu bạn có thêm tùy chọn
            }

            newFilter.setMinPostedDateMillis(calculatedMinDateMillis);
            newFilter.setMaxPostedDateMillis(calculatedMaxDateMillis); // Max luôn là hiện tại, trừ khi là "Tất cả thời gian"

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

        // THIẾT LẬP DỮ LIỆU CHO spinnerDateRange
        List<String> dateRangeOptions = new ArrayList<>();
        dateRangeOptions.add("Tất cả thời gian");
        dateRangeOptions.add("Bài đăng mới nhất (trong 24 giờ qua)");
        dateRangeOptions.add("Bài đăng trong 7 ngày qua");
        dateRangeOptions.add("Bài đăng trong 30 ngày qua");
        dateRangeOptions.add("Bài đăng trong 3 tháng qua");
        dateRangeOptions.add("Bài đăng trong 6 tháng qua");
        dateRangeOptions.add("Bài đăng trong 1 năm qua");

        ArrayAdapter<String> dateRangeAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, dateRangeOptions);
        dateRangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDateRange.setAdapter(dateRangeAdapter);

        // Khôi phục lựa chọn của spinnerDateRange nếu có trong currentFilter
// Logic này phức tạp hơn một chút vì bạn phải ánh xạ ngược từ min/maxMillis về chuỗi option
        if (currentFilter != null) {
            // ... (Logic khôi phục lựa chọn cho category, location, job type)
            if (currentFilter.getMinPostedDateMillis() == null && currentFilter.getMaxPostedDateMillis() == null) {
                setSpinnerSelection(spinnerDateRange, dateRangeOptions, "Tất cả thời gian");
            } else if (currentFilter.getMinPostedDateMillis() != null && currentFilter.getMaxPostedDateMillis() != null) {
                // Đây là phần phức tạp: bạn phải xác định xem selectedStartDateMillis và selectedEndDateMillis
                // có khớp với một trong các tùy chọn cố định của bạn không.
                // Ví dụ, nếu minPostedDateMillis gần bằng (currentTime - 24h) và maxPostedDateMillis gần bằng currentTime,
                // thì chọn "Bài đăng mới nhất (trong 24 giờ qua)".
                // Việc này yêu cầu tính toán và so sánh cẩn thận, có thể thêm một hàm helper riêng.
                // Để đơn giản, ban đầu bạn có thể bỏ qua việc khôi phục lựa chọn cho spinnerDateRange hoặc chỉ đặt mặc định.
            }
        }
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

                // Khôi phục lựa chọn của spinner nếu có trong currentFilter
                if (currentFilter != null) {
                    if (spinner.getId() == R.id.spinnerCategory && currentFilter.getCategory() != null) {
                        setSpinnerSelection(spinner, items, currentFilter.getCategory());
                    } else if (spinner.getId() == R.id.spinnerLocation && currentFilter.getLocation() != null) {
                        setSpinnerSelection(spinner, items, currentFilter.getLocation());
                    }
                    // Khôi phục JobType (nếu có)
                    if (spinner.getId() == R.id.spinnerJobType && currentFilter.getJobType() != null) {
                        setSpinnerSelection(spinner, items, currentFilter.getJobType());
                    }
                }
            } else {
                Toast.makeText(getContext(), "Lỗi tải dữ liệu lọc.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method to set spinner selection
    private void setSpinnerSelection(Spinner spinner, List<String> items, String value) {
        int position = items.indexOf(value);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }

    // XÓA PHƯƠNG THỨC NÀY VÀ MỌI LỜI GỌI ĐẾN NÓ
    // private void showDateRangePicker() { ... }
}
