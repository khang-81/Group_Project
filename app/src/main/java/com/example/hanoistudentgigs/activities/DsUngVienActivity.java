package com.example.hanoistudentgigs.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.adapters.ApplicantAdapter;
import com.example.hanoistudentgigs.models.Application;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class DsUngVienActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ApplicantAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dsungvien);

        recyclerView = findViewById(R.id.recyclerViewAllApplicants);
        setupRecyclerView();
    }
    private void setupRecyclerView() {
        Query query = FirebaseFirestore.getInstance()
                .collection("applications")
                .whereEqualTo("status", "Đang chờ"); // hoặc bỏ điều kiện nếu muốn tất cả

        FirestoreRecyclerOptions<Application> options = new FirestoreRecyclerOptions.Builder<Application>()
                .setQuery(query, Application.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new ApplicantAdapter(options, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
