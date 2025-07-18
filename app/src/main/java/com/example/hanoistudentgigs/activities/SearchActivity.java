package com.example.hanoistudentgigs.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.adapters.PopularJobAdapter;
import com.example.hanoistudentgigs.models.Filter;
import com.example.hanoistudentgigs.models.Job;
import com.example.hanoistudentgigs.utils.Constants;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.Timestamp; // Thêm import này
import java.util.Date; // Thêm import này
import java.text.Normalizer; // Thêm import này
import java.util.regex.Pattern; // Thêm import này

public class SearchActivity extends AppCompatActivity {

    private EditText editTextSearchQuery;
    private ImageButton buttonFilter;
    private RecyclerView recyclerViewSearchResults;
    private PopularJobAdapter adapter;
    private FirebaseFirestore db;
    private Filter currentFilter = new Filter(); // Đảm bảo đã khởi tạo
    private String currentSearchText = "";

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    private TextView textViewNoResults;

    private static final int YOUR_REQUEST_CODE_FOR_FILTER_ACTIVITY = 1; // Đảm bảo request code này khớp với FilterActivity

    private final ActivityResultLauncher<Intent> filterLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Filter receivedFilter = (Filter) result.getData().getSerializableExtra("applied_filter");
                    Log.d("SearchActivity", "Filter received: " + (receivedFilter != null ? receivedFilter.toString() : "null"));
                    if (receivedFilter != null) {
                        this.currentFilter = receivedFilter;
                        handler.post(() -> performSearch()); // Đảm bảo gọi trên luồng chính
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        db = FirebaseFirestore.getInstance();
        editTextSearchQuery = findViewById(R.id.editTextSearchQuery);
        buttonFilter = findViewById(R.id.buttonFilter);
        recyclerViewSearchResults = findViewById(R.id.recyclerViewSearchResults);
        textViewNoResults = findViewById(R.id.textViewNoResults);

        // THÊM DÒNG NÀY: Vô hiệu hóa ItemAnimator để tránh lỗi đồng bộ hóa
        recyclerViewSearchResults.setItemAnimator(null);

        // Thiết lập lắng nghe sự kiện nhấn Enter trên bàn phím cho ô tìm kiếm
        editTextSearchQuery.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable); // Hủy tìm kiếm đang chờ
                }
                // Chuẩn hóa chuỗi tìm kiếm trước khi gán
                currentSearchText = removeAccents(editTextSearchQuery.getText().toString()).trim().toLowerCase();
                performSearch(); // Thực hiện tìm kiếm ngay lập tức
                // Ẩn bàn phím sau khi tìm kiếm
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(editTextSearchQuery.getWindowToken(), 0);
                }
                return true; // Đã xử lý sự kiện
            }
            return false; // Không xử lý sự kiện
        });

        // Gọi performSearch() lần đầu để thiết lập adapter và tải dữ liệu ban đầu
        performSearch();

        // Thiết lập lắng nghe sự kiện click cho nút Filter
        buttonFilter.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, FilterActivity.class);
            intent.putExtra("current_filter", currentFilter); // Truyền bộ lọc hiện tại
            filterLauncher.launch(intent); // Khởi chạy FilterActivity và chờ kết quả
        });

        // Xử lý sự kiện chạm vào biểu tượng tìm kiếm trong EditText
        editTextSearchQuery.setOnTouchListener((v, event) -> {
            final int DRAWABLE_LEFT = 0; // Vị trí của icon search bên trái
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Kiểm tra xem vị trí chạm có nằm trong bounds của icon không
                if (editTextSearchQuery.getCompoundDrawables()[DRAWABLE_LEFT] != null &&
                        event.getRawX() <= (editTextSearchQuery.getLeft() +
                                editTextSearchQuery.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width() +
                                editTextSearchQuery.getCompoundDrawablePadding())) {
                    Log.d("SearchActivity", "Search icon clicked!");
                    // Chuẩn hóa chuỗi tìm kiếm trước khi gán
                    currentSearchText = removeAccents(editTextSearchQuery.getText().toString()).trim().toLowerCase();
                    if (searchRunnable != null) {
                        handler.removeCallbacks(searchRunnable); // Hủy tìm kiếm đang chờ
                    }
                    performSearch(); // Thực hiện tìm kiếm ngay lập tức khi click icon
                    return true; // Xử lý sự kiện chạm
                }
            }
            return false; // Không xử lý sự kiện chạm nếu không phải click icon
        });

        // Lắng nghe sự thay đổi văn bản trong ô tìm kiếm để thực hiện tìm kiếm tự động
        editTextSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Chuẩn hóa chuỗi tìm kiếm trước khi gán
                currentSearchText = removeAccents(s.toString()).trim().toLowerCase();
                // Loại bỏ mọi callback tìm kiếm trước đó để chỉ thực hiện tìm kiếm sau khi người dùng ngừng nhập
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> performSearch();
                // Đặt một độ trễ để tránh tìm kiếm quá thường xuyên (ví dụ: gõ từng ký tự)
                handler.postDelayed(searchRunnable, 300); // 300ms delay
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void performSearch() {
        Log.d("SearchActivity", "Starting performSearch() for keyword: '" + currentSearchText + "' and filter: " + currentFilter.toString());

        // Xây dựng truy vấn Firestore dựa trên từ khóa tìm kiếm và bộ lọc
        Query query = db.collection(Constants.JOBS_COLLECTION);
//                .whereEqualTo("approved", false); // Bỏ comment nếu bạn muốn lọc theo trạng thái duyệt

        // Lọc theo Category
        if (currentFilter.getCategory() != null && !currentFilter.getCategory().isEmpty() && !currentFilter.getCategory().equals("Tất cả ngành nghề")) {
            query = query.whereEqualTo("categoryName", currentFilter.getCategory());
            Log.d("SearchActivity", "Filtering by category: " + currentFilter.getCategory());
        }
        // Lọc theo Location
        if (currentFilter.getLocation() != null && !currentFilter.getLocation().isEmpty() && !currentFilter.getLocation().equals("Tất cả địa điểm")) {
            query = query.whereEqualTo("locationName", currentFilter.getLocation());
            Log.d("SearchActivity", "Filtering by location: " + currentFilter.getLocation());
        }
        // Lọc theo Job Type
        if (currentFilter.getJobType() != null && !currentFilter.getJobType().isEmpty() && !currentFilter.getJobType().equals("Tất cả loại hình")) {
            query = query.whereEqualTo("jobType", currentFilter.getJobType());
            Log.d("SearchActivity", "Filtering by job type: " + currentFilter.getJobType());
        }

        // Lọc theo từ khóa tìm kiếm (searchKeywords)
        if (!currentSearchText.isEmpty()) {
            Log.d("SearchActivity", "Applying search keyword: " + currentSearchText);
            // Sử dụng whereArrayContains cho các từ khóa tìm kiếm.
            // Đảm bảo trường 'searchKeywords' là một mảng trong Firestore
            // và đã được chuẩn hóa (không dấu, chữ thường)
            query = query.whereArrayContains("searchKeywords", currentSearchText);
        }

        // ---------- THÊM LOGIC LỌC THỜI GIAN VÀO ĐÂY ----------
        if (currentFilter.getMinPostedDateMillis() != null) {
            Timestamp minTimestamp = new Timestamp(new Date(currentFilter.getMinPostedDateMillis()));
            // Truy vấn theo đường dẫn "createdAt.timestamp"
            query = query.whereGreaterThanOrEqualTo("createdAt.timestamp", minTimestamp);
            Log.d("SearchActivity", "Filtering by min createdAt.timestamp: " + minTimestamp.toDate().toString());
        }
        if (currentFilter.getMaxPostedDateMillis() != null) {
            Timestamp maxTimestamp = new Timestamp(new Date(currentFilter.getMaxPostedDateMillis()));
            // Truy vấn theo đường dẫn "createdAt.timestamp"
            query = query.whereLessThanOrEqualTo("createdAt.timestamp", maxTimestamp);
            Log.d("SearchActivity", "Filtering by max createdAt.timestamp: " + maxTimestamp.toDate().toString());
        }
        // --------------------------------------------------------

        // Luôn sắp xếp theo 'createdAt.timestamp' để có kết quả nhất quán
        // và quan trọng cho việc tạo chỉ mục.
        query = query.orderBy("createdAt.timestamp", Query.Direction.DESCENDING);

        // Xây dựng FirestoreRecyclerOptions mới với truy vấn đã cập nhật
        FirestoreRecyclerOptions<Job> newOptions = new FirestoreRecyclerOptions.Builder<Job>()
                .setQuery(query, Job.class)
                .setLifecycleOwner(this) // Đặt lifecycle owner là Activity này
                .build();

        Log.d("SearchActivity", "Query being applied to adapter: " + query.toString());
        Log.d("SearchActivity", "Firestore query constructed.");

        // Dừng adapter cũ nếu nó đang lắng nghe trước khi thay thế
        if (adapter != null) {
            adapter.stopListening();
            Log.d("SearchActivity", "Old adapter stopped listening.");
        }

        // TẠO MỚI HOÀN TOÀN ADAPTER VÀ GÁN LẠI CHO RECYCLERVIEW
        adapter = new PopularJobAdapter(newOptions, this, recyclerViewSearchResults, textViewNoResults);
        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(this)); // Đặt lại LayoutManager
        recyclerViewSearchResults.setAdapter(adapter); // Gán adapter mới

        // Bắt đầu lắng nghe dữ liệu ngay lập lập tức với adapter mới
        adapter.startListening();
        Log.d("SearchActivity", "New adapter created, set, and started listening.");

        // Không cần query.get().addOnCompleteListener, vì FirestoreRecyclerAdapter xử lý cập nhật thời gian thực.
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bắt đầu lắng nghe khi Activity được hiển thị (trở lại foreground)
        if (adapter != null) {
            adapter.startListening();
            Log.d("SearchActivity", "Adapter started listening from onStart.");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Dừng lắng nghe khi Activity không còn hiển thị (đi vào background)
        if (adapter != null) {
            adapter.stopListening();
            Log.d("SearchActivity", "Adapter stopped listening from onStop.");
        }
    }

    // Phương thức để chuyển đổi chuỗi tiếng Việt có dấu thành không dấu
    public static String removeAccents(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        text = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        text = pattern.matcher(text).replaceAll("");
        text = text.replace('đ', 'd').replace('Đ', 'D');
        return text;
    }
}