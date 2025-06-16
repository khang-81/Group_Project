package com.example.hanoistudentgigs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class SearchActivity extends AppCompatActivity {

    private EditText editTextSearchQuery;
    private ImageButton buttonFilter;
    private RecyclerView recyclerViewSearchResults;
    private PopularJobAdapter adapter;
    private FirebaseFirestore db;
    private Filter currentFilter = new Filter(); // Lưu trữ các bộ lọc hiện tại
    private String currentSearchText = "";

    // Trình khởi chạy để nhận kết quả từ FilterActivity
    private final ActivityResultLauncher<Intent> filterLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Filter receivedFilter = (Filter) result.getData().getSerializableExtra("applied_filter");
                    if (receivedFilter != null) {
                        this.currentFilter = receivedFilter;
                        performSearch(); // Thực hiện lại tìm kiếm với bộ lọc mới
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

        setupRecyclerView();

        buttonFilter.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, FilterActivity.class);
            intent.putExtra("current_filter", currentFilter);
            filterLauncher.launch(intent);
        });

        editTextSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchText = s.toString().trim();
                performSearch();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupRecyclerView() {
        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(this));
        performSearch();
    }

    private void performSearch() {
        Query query = db.collection(Constants.JOBS_COLLECTION).whereEqualTo("approved", true);

        if (currentFilter.getCategory() != null && !currentFilter.getCategory().isEmpty()) {
            query = query.whereEqualTo("categoryName", currentFilter.getCategory());
        }
        if (currentFilter.getLocation() != null && !currentFilter.getLocation().isEmpty()) {
            query = query.whereEqualTo("locationName", currentFilter.getLocation());
        }
        if (currentFilter.getJobType() != null && !currentFilter.getJobType().isEmpty()) {
            query = query.whereEqualTo("jobType", currentFilter.getJobType());
        }

        if (!currentSearchText.isEmpty()) {
            query = query.whereArrayContains("searchKeywords", currentSearchText.toLowerCase());
        }

        query = query.orderBy("createdAt", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Job> newOptions = new FirestoreRecyclerOptions.Builder<Job>()
                .setQuery(query, Job.class)
                .build();

        if (adapter == null) {
            adapter = new PopularJobAdapter(newOptions, this);
            recyclerViewSearchResults.setAdapter(adapter);
            adapter.startListening();
        } else {
            adapter.updateOptions(newOptions);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }
}