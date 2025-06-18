package com.example.hanoistudentgigs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log; // Import Log
import android.widget.EditText;
import android.widget.ImageButton;

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
import android.view.MotionEvent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager; // Cần thêm
import android.content.Context; // Cần thêm
import android.view.KeyEvent; // Cần thêm
public class SearchActivity extends AppCompatActivity {

    private EditText editTextSearchQuery;
    private ImageButton buttonFilter;
    private RecyclerView recyclerViewSearchResults;
    private PopularJobAdapter adapter;
    private FirebaseFirestore db;
    private Filter currentFilter = new Filter();
    private String currentSearchText = "";

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    private TextView textViewNoResults;

    private final ActivityResultLauncher<Intent> filterLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Filter receivedFilter = (Filter) result.getData().getSerializableExtra("applied_filter");
                    if (receivedFilter != null) {
                        this.currentFilter = receivedFilter;
                        // Đảm bảo rằng việc gọi performSearch() diễn ra trên luồng chính
                        handler.post(() -> performSearch());
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

        editTextSearchQuery.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                // Đảm bảo dừng mọi tìm kiếm đang chờ xử lý từ TextWatcher
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
                currentSearchText = editTextSearchQuery.getText().toString().trim().toLowerCase();
                performSearch();
                // Ẩn bàn phím sau khi tìm kiếm
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(editTextSearchQuery.getWindowToken(), 0);
                }
                return true; // Xử lý sự kiện
            }
            return false; // Không xử lý sự kiện
        });
        // Khởi tạo Adapter với một query ban đầu.
        // Đây là Query mặc định khi không có tìm kiếm/lọc nào được áp dụng.
        Query initialQuery = db.collection(Constants.JOBS_COLLECTION)
                .whereEqualTo("isApproved", true)
                .orderBy("createdAt", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Job> initialOptions = new FirestoreRecyclerOptions.Builder<Job>()
                .setQuery(initialQuery, Job.class)
                .build();

        adapter = new PopularJobAdapter(initialOptions, this);
        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSearchResults.setAdapter(adapter);

        // Gọi performSearch() lần đầu để áp dụng các bộ lọc/tìm kiếm ban đầu
        // (nếu có, ví dụ từ một Intent) hoặc để hiển thị tất cả các công việc đã duyệt.
        performSearch();

        buttonFilter.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, FilterActivity.class);
            intent.putExtra("current_filter", currentFilter);
            filterLauncher.launch(intent);
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
                    currentSearchText = editTextSearchQuery.getText().toString().trim().toLowerCase();
                    // Loại bỏ bất kỳ runnable tìm kiếm đang chờ xử lý nào
                    if (searchRunnable != null) {
                        handler.removeCallbacks(searchRunnable);
                    }
                    performSearch(); // Thực hiện tìm kiếm ngay lập tức khi click icon
                    return true; // Xử lý sự kiện chạm
                }
            }
            return false; // Không xử lý sự kiện chạm nếu không phải click icon
        });


        editTextSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchText = s.toString().trim().toLowerCase();
                // Loại bỏ mọi callback tìm kiếm trước đó để chỉ thực hiện tìm kiếm sau khi người dùng ngừng nhập
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> performSearch();
                // Đặt một độ trễ để tránh tìm kiếm quá thường xuyên
                handler.postDelayed(searchRunnable, 300); // 300ms delay
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void performSearch() {
        Log.d("SearchActivity", "Starting performSearch() for keyword: '" + currentSearchText + "' and filter: " + currentFilter.toString());

        Query query = db.collection(Constants.JOBS_COLLECTION).whereEqualTo("isApproved", true);

        if (currentFilter.getCategory() != null && !currentFilter.getCategory().isEmpty()) {
            query = query.whereEqualTo("categoryName", currentFilter.getCategory());
            Log.d("SearchActivity", "Filtering by category: " + currentFilter.getCategory());
        }
        if (currentFilter.getLocation() != null && !currentFilter.getLocation().isEmpty()) {
            query = query.whereEqualTo("locationName", currentFilter.getLocation());
            Log.d("SearchActivity", "Filtering by location: " + currentFilter.getLocation());
        }
        if (currentFilter.getJobType() != null && !currentFilter.getJobType().isEmpty()) {
            query = query.whereEqualTo("jobType", currentFilter.getJobType());
            Log.d("SearchActivity", "Filtering by job type: " + currentFilter.getJobType());
        }

        if (!currentSearchText.isEmpty()) {
            // Chuẩn hóa chuỗi tìm kiếm để tìm kiếm không dấu nếu cần (đã nhập Normalizer)
            // String normalizedSearchText = normalizeString(currentSearchText);
            Log.d("SearchActivity", "Applying search keyword (normalized): " + currentSearchText);
            // Lưu ý: whereArrayContains("searchKeywords", currentSearchText) yêu cầu
            // trường "searchKeywords" trong Firestore phải là một mảng chứa các từ khóa đã chuẩn hóa.
            // Nếu bạn muốn tìm kiếm text toàn văn trên các trường khác (ví dụ: title, companyName),
            // bạn sẽ cần một giải pháp tìm kiếm toàn văn phức tạp hơn (ví dụ: Algolia, ElasticSearch)
            // hoặc phải lưu trữ các từ khóa theo từng trường cụ thể.
            // Với whereArrayContains, "uniqlo" phải là một phần tử trong mảng searchKeywords.
            query = query.whereArrayContains("searchKeywords", currentSearchText);
        }

        query = query.orderBy("createdAt", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Job> newOptions = new FirestoreRecyclerOptions.Builder<Job>()
                .setQuery(query, Job.class)
                .setLifecycleOwner(this)
                .build();

        Log.d("SearchActivity", "Query being applied to adapter: " + query.toString());
        Log.d("SearchActivity", "Firestore query constructed.");

        // Dừng adapter lắng nghe query cũ trước khi cập nhật options.
        // Mặc dù updateOptions() tự làm điều này, nhưng việc gọi rõ ràng có thể hữu ích
        // trong một số trường hợp để đảm bảo trạng thái sạch.
        if (adapter != null) {
            adapter.stopListening();
            Log.d("SearchActivity", "Adapter stopped listening before updating options.");
        }

        // Cập nhật options cho adapter
        adapter.updateOptions(newOptions);
        adapter.notifyDataSetChanged();
        adapter.startListening();
        Log.d("SearchActivity", "Adapter updated options.");

        // Sau khi updateOptions, adapter sẽ tự động startListening() lại.
        // Tuy nhiên, nếu bạn muốn đảm bảo, bạn có thể gọi adapter.startListening() ở đây,
        // nhưng hãy cẩn thận để không gọi nó hai lần.
        // Thông thường, FirestoreRecyclerAdapter sẽ tự động lắng nghe sau khi updateOptions.

        // Listener để kiểm tra kết quả truy vấn và hiển thị thông báo "No Results"
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int resultCount = task.getResult().size();
                Log.d("SearchActivity", "Query success for text: '" + currentSearchText + "', count: " + resultCount);

                // Cập nhật trạng thái hiển thị của RecyclerView và TextViewNoResults
                if (resultCount == 0) {
                    Log.d("SearchActivity", "No documents found for '" + currentSearchText + "'. Displaying no results message.");
                    recyclerViewSearchResults.setVisibility(View.GONE);
                    textViewNoResults.setVisibility(View.VISIBLE);
                    textViewNoResults.setText("Không tìm thấy kết quả phù hợp."); // Đặt tin nhắn rõ ràng
                } else {
                    Log.d("SearchActivity", "Documents found for '" + currentSearchText + "'. Displaying results.");
                    recyclerViewSearchResults.setVisibility(View.VISIBLE);
                    textViewNoResults.setVisibility(View.GONE);
                    // In ra các ID công việc tìm thấy để debug
                    task.getResult().getDocuments().forEach(doc -> Log.d("SearchActivity", "Found Job ID: " + doc.getId()));
                }
            } else {
                Log.e("SearchActivity", "Error getting documents for text: '" + currentSearchText + "'", task.getException());
                recyclerViewSearchResults.setVisibility(View.GONE);
                textViewNoResults.setVisibility(View.VISIBLE);
                textViewNoResults.setText("Đã xảy ra lỗi khi tìm kiếm: " + task.getException().getMessage());
            }
        });
    }

    // Phương thức normalizeString (nếu bạn muốn tìm kiếm không dấu)
    // private String normalizeString(String input) {
    //     String nfdNormalizedString = Normalizer.normalize(input, Normalizer.Form.NFD);
    //     Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    //     return pattern.matcher(nfdNormalizedString).replaceAll("").toLowerCase();
    // }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
            Log.d("SearchActivity", "Adapter started listening from onStart.");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
            Log.d("SearchActivity", "Adapter stopped listening from onStop.");
        }
    }
}